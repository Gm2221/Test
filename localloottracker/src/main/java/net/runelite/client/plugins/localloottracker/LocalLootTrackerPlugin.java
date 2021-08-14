/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.localloottracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.http.api.loottracker.LootRecordType;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Extension
@PluginDependency(LootTrackerPlugin.class)
@PluginDescriptor(
      name = "Loot Tracker (local)",
      description = "Local data storage extension to loot tracker plugin",
      tags = {"jz", "loot", "local", "track"},
      enabledByDefault = true
)

@Slf4j
public class LocalLootTrackerPlugin extends Plugin
{
   @Inject
   private Client client;

   @Inject
   private ClientThread clientThread;

   @Inject
   private LootTrackerPlugin lootTrackerPlugin;

   @Inject
   private ItemManager itemManager;

   private static final File LOOTTRACKER_FILE = new File(RuneLite.RUNELITE_DIR, "loot-tracker.json");
   private boolean added = false;
   private LootAggregate[] records;

   private static class Drop
   {
      int id;
      int qty;
   }

   @Data
   private static class LootAggregate
   {
      String eventId;
      String type;
      Drop[] drops;
      int amount;
   }

   @Override
   protected void startUp()
   {
   }

   @Override
   protected void shutDown()
   {
   }

   @Subscribe
   public void onGameStateChanged(GameStateChanged event)
   {
      if (event.getGameState() == GameState.LOGGED_IN && !added)
      {
         addLoots();
         added = true;
      }
   }

   @Subscribe
   public void onLootReceived(LootReceived event)
   {
      if (LOOTTRACKER_FILE.exists() && records != null)
      {
         int recordIndex = findRecordIndex(event.getName(), event.getType().toString());

         if (recordIndex > -1)
            updateRecord(event, recordIndex);
         else
            addRecord(event);

         writeFile();
      }
      else
      {
         try
         {
            boolean success = LOOTTRACKER_FILE.createNewFile();

            if (success)
            {
               records = new LootAggregate[0];

               addRecord(event);
               writeFile();
            }

         } catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Add aggregated loots from file to panel
    */
   private void addLoots()
   {
      if (LOOTTRACKER_FILE.exists())
      {
         try
         {
            InputStream fileStream = new BufferedInputStream(new FileInputStream(LOOTTRACKER_FILE));
            Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
            records = new Gson().fromJson(reader, LootAggregate[].class);
            clientThread.invokeLater(() -> addLoot(records));
         } catch (FileNotFoundException e)
         {
            e.printStackTrace();
         }
      }
   }
   /**
    * Find matching records entry from aggregated loots
    *
    * @param name eventId to search for
    * @param type type to search for
    *
    * @return matching index from aggregated loots, else -1
    */
   private int findRecordIndex(String name, String type)
   {
      int index = 0;

      for (LootAggregate record : records)
      {
         if (record.getEventId().equals(name) && record.getType().equals(type))
            return index;
         else
            index++;
      }

      return -1;
   }

   /**
    * Add new aggregated loot entry
    *
    * @param event loot to add
    */
   private void addRecord(LootReceived event)
   {
      LootAggregate newRecord = new LootAggregate();
      Drop[] drops = new Drop[event.getItems().size()];

      int index = 0;
      for (ItemStack items : event.getItems())
      {
         Drop drop = new Drop();
         drop.id = items.getId();
         drop.qty = items.getQuantity();

         drops[index] = drop;
         index++;
      }

      newRecord.type = event.getType().toString();
      newRecord.eventId = event.getName();
      newRecord.drops = drops;
      newRecord.amount = 1;

      LootAggregate[] newRecords = Arrays.copyOf(records, records.length + 1);
      newRecords[records.length] = newRecord;
      records = newRecords;
   }

   /**
    * Update existing aggregated loot at specified index
    *
    * @param event       loot to add
    * @param recordIndex index to update
    */
   private void updateRecord(LootReceived event, int recordIndex)
   {
      records[recordIndex].setAmount(records[recordIndex].getAmount() + 1);
      Drop[] drops = records[recordIndex].getDrops();

      for (ItemStack items : event.getItems())
      {
         boolean added = false;

         for (Drop drop : drops)
         {
            if (drop.id == items.getId())
            {
               added = true;
               drop.qty += items.getQuantity();
               break;
            }
         }

         if (!added)
         {
            Drop newDrop = new Drop();
            newDrop.id = items.getId();
            newDrop.qty = items.getQuantity();

            Drop[] newDrops = Arrays.copyOf(records[recordIndex].getDrops(), records[recordIndex].drops.length + 1);
            newDrops[records[recordIndex].drops.length] = newDrop;
            records[recordIndex].drops = newDrops;
         }
      }
   }

   /**
    * Update loot tracker file contents
    */
   private void writeFile()
   {
      try
      {
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         FileWriter writer = new FileWriter(LOOTTRACKER_FILE);
         gson.toJson(records, writer);
         writer.flush();
         writer.close();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Add aggregated loots from memory to loot tracker panel
    *
    * @param loots aggregated loots to add
    */
   @SneakyThrows
   private void addLoot(LootAggregate[] loots)
   {
      Class<?> panelClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerPanel");
      Class<?> itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");

      Method addMethod = panelClazz.getDeclaredMethod("addRecords", Collection.class);
      Field panelField = LootTrackerPlugin.class.getDeclaredField("panel");

      addMethod.setAccessible(true);
      panelField.setAccessible(true);

      Object panelObject = panelField.get(lootTrackerPlugin);
      Collection<Object> collection = new ArrayList<>();

      for (LootAggregate loot : loots)
      {
         try
         {
            Object lootEntries = Array.newInstance(itemClazz, loot.drops.length);

            int count = 0;

            for (Drop drop : loot.drops)
               Array.set(lootEntries, count++, createLootTrackerItem(drop.id, drop.qty));

            collection.add(createLootTrackerRecord(loot.eventId, getType(loot.type), lootEntries, loot.amount));

         } catch (NullPointerException e)
         {
            e.printStackTrace();
         }
      }

      SwingUtilities.invokeLater(() ->
      {
         try
         {
            addMethod.invoke(panelObject, collection);
         } catch (IllegalAccessException | InvocationTargetException e)
         {
            e.printStackTrace();
         }
      });
   }

   /**
    * Create new loot tracker record
    *
    * @param title Loot name
    * @param type  Loot type
    * @param items Loot drops
    * @param kills Amount of kills
    *
    * @return Object<LootTrackerRecord>
    */
   @SneakyThrows
   private Object createLootTrackerRecord(String title, LootRecordType type, Object items, int kills)
   {
      Class<?> lootClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerRecord");
      Class<?> itemAryClazz = Class.forName("[Lnet.runelite.client.plugins.loottracker.LootTrackerItem;");

      Constructor<?> constructor = lootClazz.getConstructor(
              String.class,         // title
              String.class,         // subTitle
              LootRecordType.class, // type
              itemAryClazz,         // items
              int.class);           // kills

      constructor.setAccessible(true);

      return constructor.newInstance(title, "", type, items, kills);
   }

   /**
    * Create new loot tracker item
    *
    * @param itemId   Item ID
    * @param quantity Item quantity
    *
    * @return Object<LootTrackerItem>
    */
   @SneakyThrows
   private Object createLootTrackerItem(int itemId, int quantity)
   {
      ItemComposition itemComposition = itemManager.getItemComposition(itemId);
      int gePrice = itemManager.getItemPrice(itemId);
      int haPrice = itemComposition.getHaPrice();
      String name = itemComposition.getName();

      Class<?> itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");

      Constructor<?> constructor = itemClazz.getConstructor(
              int.class,      // id
              String.class,   // name
              int.class,      // quantity
              int.class,      // gePrice
              int.class,      // haPrice
              boolean.class); // ignored

      constructor.setAccessible(true);

      return constructor.newInstance(itemId, name, quantity, gePrice, haPrice, false);
   }

   /**
    * Map String to LootRecordType
    *
    * @param type string representation for loot type
    *
    * @return class representation of loot type
    */
   private LootRecordType getType(String type)
   {
      switch (type)
      {
         case "NPC":
            return LootRecordType.NPC;
         case "PLAYER":
            return LootRecordType.PLAYER;
         case "EVENT":
            return LootRecordType.EVENT;
         case "PICKPOCKET":
            return LootRecordType.PICKPOCKET;
         default:
            return LootRecordType.UNKNOWN;
      }
   }
}
