/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.localloottracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.NonNull;
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
import java.util.List;

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
   private JsonResult[] records;

   public static class Drop {
      int id;
      int qty;
   }

   @Data
   public static class JsonResult {
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
         addLoots(true);
         added = true;
      }
   }

   @Subscribe
   public void onLootReceived(LootReceived event)
   {
      if (LOOTTRACKER_FILE.exists() && records != null)
      {
         {
            int recordIndex = findRecordIndex(event.getName(), event.getType().toString());

            if (recordIndex > -1)
            {
               log.error("Updating drop count from {}", records[recordIndex].getAmount());
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

                     Drop[] newDrops = Arrays.copyOf(records[recordIndex].drops, records[recordIndex].drops.length + 1);
                     newDrops[records[recordIndex].drops.length] = newDrop;
                     records[recordIndex].drops = newDrops;
                  }
               }
            }
            else
            {
               log.error("Adding new drop");
               JsonResult newRecord = new JsonResult();
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

               JsonResult[] newRecords = Arrays.copyOf(records, records.length + 1);
               newRecords[records.length] = newRecord;
               records = newRecords;
            }
         }

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
      else
      {
         try
         {
            log.error("Creating new file");
            //noinspection ResultOfMethodCallIgnored
            LOOTTRACKER_FILE.createNewFile();

            JsonResult newRecord = new JsonResult();
            JsonResult[] newRecords = new JsonResult[1];
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

            newRecords[0] = newRecord;
            records = newRecords;

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
   }

   private void addLoots(boolean aggregate)
   {
      if (LOOTTRACKER_FILE.exists())
      {
         try
         {
            InputStream fileStream = new BufferedInputStream(new FileInputStream(LOOTTRACKER_FILE));
            Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
            records = new Gson().fromJson(reader, JsonResult[].class);

            {
               // Not aggregated loots
               if (!aggregate)
               {
                  clientThread.invokeLater(() ->
                  {
                     for (JsonResult record : records)
                     {
                        List<ItemStack> collection = new ArrayList<>();

                        // Try to generate amount of loots in a hacky way
                        for (int i = 1; i < record.amount; i++)
                           addLoot(record.eventId, getType(record.type), collection);

                        for (Drop drop : record.drops)
                           collection.add(new ItemStack(drop.id, drop.qty, null));

                        addLoot(record.eventId, getType(record.type), collection);
                     }
                  });
               }
               // Aggregated loots
               else
               {
                  clientThread.invokeLater(() -> addLoot(records));
               }
            }
         } catch (FileNotFoundException e)
         {
            e.printStackTrace();
         }
      }
   }

   private int findRecordIndex(String name, String type)
   {
      int index = 0;

      for (JsonResult record : records)
      {
         if (record.getEventId().equals(name) && record.getType().equals(type))
            return index;
         else
            index++;
      }

      return -1;
   }

   @SneakyThrows
   void addLoot(JsonResult[] loots)
   {
      Class<?> panelClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerPanel");
      Class<?> itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");

      Method addMethod = panelClazz.getDeclaredMethod("addRecords", Collection.class);
      Field panelField = LootTrackerPlugin.class.getDeclaredField("panel");

      addMethod.setAccessible(true);
      panelField.setAccessible(true);

      Object panelObject = panelField.get(lootTrackerPlugin);
      Collection<Object> coll = new ArrayList<>();

      for (JsonResult loot : loots)
      {
         try
         {
            Object lootEntries = Array.newInstance(itemClazz, loot.drops.length);

            int count = 0;
            for (Drop i : loot.drops)
               Array.set(lootEntries, count++, createLootTrackerItem(i.id, i.qty));

            coll.add(createLootTrackerRecord(loot.eventId, getType(loot.type), lootEntries, loot.amount));
         } catch (NullPointerException e)
         {
            log.error("Loot with empty field");
         }
      }

      SwingUtilities.invokeLater(() ->
      {
         try
         {
            //addMethod.invoke(panelObject, entries);
            addMethod.invoke(panelObject, coll);
         } catch (IllegalAccessException | InvocationTargetException e)
         {
            e.printStackTrace();
         }
      });
   }

   @SneakyThrows
   void addLoot(@NonNull String name, LootRecordType type, Collection<ItemStack> items)
   {
      Class<?> panelClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerPanel");
      Class<?> itemAryClazz = Class.forName("[Lnet.runelite.client.plugins.loottracker.LootTrackerItem;");
      Class<?> itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");

      Method addMethod = panelClazz.getDeclaredMethod("add", String.class, LootRecordType.class, int.class, itemAryClazz);
      Field panelField = LootTrackerPlugin.class.getDeclaredField("panel");

      addMethod.setAccessible(true);
      panelField.setAccessible(true);

      Object panelObject = panelField.get(lootTrackerPlugin);
      Object entries = Array.newInstance(itemClazz, items.size());

      int index = 0;

      for (ItemStack i : items)
         Array.set(entries, index++, createLootTrackerItem(i.getId(), i.getQuantity()));

      SwingUtilities.invokeLater(() ->
      {
         try
         {
            addMethod.invoke(panelObject, name, type, -1, entries);
         } catch (IllegalAccessException | InvocationTargetException e)
         {
            e.printStackTrace();
         }
      });
   }

   @SneakyThrows
   private Object createLootTrackerRecord(String title, LootRecordType type, Object items, int kills)
   {
      Class<?> lootClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerRecord");
      Class<?> itemAryClazz = Class.forName("[Lnet.runelite.client.plugins.loottracker.LootTrackerItem;");

      Constructor<?> constructor = lootClazz.getConstructor(
              String.class,
              String.class,
              LootRecordType.class,
              itemAryClazz,
              int.class);

      constructor.setAccessible(true);

      return constructor.newInstance(title, "", type, items, kills);
   }

   @SneakyThrows
   private Object createLootTrackerItem(int itemId, int quantity)
   {
      ItemComposition itemComposition = itemManager.getItemComposition(itemId);
      int gePrice = itemManager.getItemPrice(itemId);
      int haPrice = itemComposition.getHaPrice();
      String name = itemComposition.getName();

      Class<?> itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");

      Constructor<?> constructor = itemClazz.getConstructor(
              int.class,
              String.class,
              int.class,
              int.class,
              int.class,
              boolean.class);

      constructor.setAccessible(true);

      return constructor.newInstance(itemId, name, quantity, gePrice, haPrice, false);
   }

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
