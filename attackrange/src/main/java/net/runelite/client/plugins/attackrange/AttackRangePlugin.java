/* 
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.attackrange;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Extension
@PluginDescriptor(
      name = "Attack Range",
      description = "Visually display weapon attack range",
      tags = {"jz"},
      enabledByDefault = false
)

@Slf4j
public class AttackRangePlugin extends Plugin
{
   @Inject
   private AttackRangeConfig config;

   @Inject
   private AttackRangeOverlay overlay;

   @Inject
   private ConfigManager configManager;

   @Inject
   private OverlayManager overlayManager;

   @Inject
   Client client;

   private final int ATTACK_RANGE_DEFAULT = 1;
   private final int ATTACK_RANGE_MAX     = 10;

   private final Map<Integer, Integer> ranges = new HashMap<Integer, Integer>()
   {
      {
         // Attack range of 2
         put(ItemID.CRYSTAL_HALBERD, 2);
         put(ItemID.CRYSTAL_HALBERD_110, 2);
         put(ItemID.CRYSTAL_HALBERD_110_I, 2);
         put(ItemID.CRYSTAL_HALBERD_210, 2);
         put(ItemID.CRYSTAL_HALBERD_210_I, 2);
         put(ItemID.CRYSTAL_HALBERD_24125, 2);
         put(ItemID.CRYSTAL_HALBERD_310, 2);
         put(ItemID.CRYSTAL_HALBERD_310_I, 2);
         put(ItemID.CRYSTAL_HALBERD_410, 2);
         put(ItemID.CRYSTAL_HALBERD_410_I, 2);
         put(ItemID.CRYSTAL_HALBERD_510, 2);
         put(ItemID.CRYSTAL_HALBERD_510_I, 2);
         put(ItemID.CRYSTAL_HALBERD_610, 2);
         put(ItemID.CRYSTAL_HALBERD_610_I, 2);
         put(ItemID.CRYSTAL_HALBERD_710, 2);
         put(ItemID.CRYSTAL_HALBERD_710_I, 2);
         put(ItemID.CRYSTAL_HALBERD_810, 2);
         put(ItemID.CRYSTAL_HALBERD_810_I, 2);
         put(ItemID.CRYSTAL_HALBERD_910, 2);
         put(ItemID.CRYSTAL_HALBERD_910_I, 2);
         put(ItemID.CRYSTAL_HALBERD_ATTUNED, 2);
         put(ItemID.CRYSTAL_HALBERD_BASIC, 2);
         put(ItemID.CRYSTAL_HALBERD_FULL, 2);
         put(ItemID.CRYSTAL_HALBERD_FULL_I, 2);
         put(ItemID.CRYSTAL_HALBERD_INACTIVE, 2);
         put(ItemID.CRYSTAL_HALBERD_PERFECTED, 2);
         put(ItemID.NEW_CRYSTAL_HALBERD_FULL, 2);
         put(ItemID.NEW_CRYSTAL_HALBERD_FULL_I, 2);
         put(ItemID.NEW_CRYSTAL_HALBERD_FULL_16893, 2);
         put(ItemID.NEW_CRYSTAL_HALBERD_FULL_I_16892, 2);

         put(ItemID.HALBERD, 2);
         put(ItemID.BRONZE_HALBERD, 2);
         put(ItemID.IRON_HALBERD, 2);
         put(ItemID.BLACK_HALBERD, 2);
         put(ItemID.WHITE_HALBERD, 2);
         put(ItemID.STEEL_HALBERD, 2);
         put(ItemID.MITHRIL_HALBERD, 2);
         put(ItemID.ADAMANT_HALBERD, 2);
         put(ItemID.RUNE_HALBERD, 2);
         put(ItemID.DRAGON_HALBERD, 2);

         put(ItemID.CORRUPTED_HALBERD_BASIC, 2);
         put(ItemID.CORRUPTED_HALBERD_ATTUNED, 2);
         put(ItemID.CORRUPTED_HALBERD_PERFECTED, 2);

         // Attack range of 3
         put(ItemID.BRONZE_DART, 3);
         put(ItemID.BRONZE_DARTP, 3);
         put(ItemID.BRONZE_DARTP_5628, 3);
         put(ItemID.BRONZE_DARTP_5635, 3);
         put(ItemID.IRON_DART, 3);
         put(ItemID.IRON_DARTP, 3);
         put(ItemID.IRON_DARTP_5629, 3);
         put(ItemID.IRON_DARTP_5636, 3);
         put(ItemID.BLACK_DART, 3);
         put(ItemID.BLACK_DARTP, 3);
         put(ItemID.BLACK_DARTP_5631, 3);
         put(ItemID.BLACK_DARTP_5638, 3);
         put(ItemID.STEEL_DART, 3);
         put(ItemID.STEEL_DARTP, 3);
         put(ItemID.STEEL_DARTP_5630, 3);
         put(ItemID.STEEL_DARTP_5637, 3);
         put(ItemID.MITHRIL_DART, 3);
         put(ItemID.MITHRIL_DARTP, 3);
         put(ItemID.MITHRIL_DARTP_5632, 3);
         put(ItemID.MITHRIL_DARTP_5639, 3);
         put(ItemID.ADAMANT_DART,3);
         put(ItemID.ADAMANT_DARTP,3);
         put(ItemID.ADAMANT_DARTP_5633,3);
         put(ItemID.ADAMANT_DARTP_5640,3);
         put(ItemID.RUNE_DART, 3);
         put(ItemID.RUNE_DARTP, 3);
         put(ItemID.RUNE_DARTP_5634, 3);
         put(ItemID.RUNE_DARTP_5641, 3);
         put(ItemID.DRAGON_DART, 3);
         put(ItemID.DRAGON_DARTP, 3);
         put(ItemID.DRAGON_DARTP_11233, 3);
         put(ItemID.DRAGON_DARTP_11234, 3);

         // Attack range of 4
         put(ItemID.BRONZE_KNIFE, 4);
         put(ItemID.BRONZE_KNIFEP, 4);
         put(ItemID.BRONZE_KNIFEP_5654, 4);
         put(ItemID.BRONZE_KNIFEP_5661, 4);
         put(ItemID.IRON_KNIFE, 4);
         put(ItemID.IRON_KNIFEP, 4);
         put(ItemID.IRON_KNIFEP_5655, 4);
         put(ItemID.IRON_KNIFEP_5662, 4);
         put(ItemID.BLACK_KNIFE, 4);
         put(ItemID.BLACK_KNIFEP, 4);
         put(ItemID.BLACK_KNIFEP_5658, 4);
         put(ItemID.BLACK_KNIFEP_5665, 4);
         put(ItemID.STEEL_KNIFE, 4);
         put(ItemID.STEEL_KNIFEP, 4);
         put(ItemID.STEEL_KNIFEP_5656, 4);
         put(ItemID.STEEL_KNIFEP_5663, 4);
         put(ItemID.MITHRIL_KNIFE, 4);
         put(ItemID.MITHRIL_KNIFEP, 4);
         put(ItemID.MITHRIL_KNIFEP_5657, 4);
         put(ItemID.MITHRIL_KNIFEP_5664, 4);
         put(ItemID.ADAMANT_KNIFE, 4);
         put(ItemID.ADAMANT_KNIFEP, 4);
         put(ItemID.ADAMANT_KNIFEP_5659, 4);
         put(ItemID.ADAMANT_KNIFEP_5666, 4);
         put(ItemID.RUNE_KNIFE, 4);
         put(ItemID.RUNE_KNIFEP, 4);
         put(ItemID.RUNE_KNIFEP_5660, 4);
         put(ItemID.RUNE_KNIFEP_5667, 4);
         put(ItemID.DRAGON_KNIFE, 4);
         put(ItemID.DRAGON_KNIFEP, 4);
         put(ItemID.DRAGON_KNIFEP_22808, 4);
         put(ItemID.DRAGON_KNIFEP_22810, 4);
         put(ItemID.BRONZE_THROWNAXE, 4);
         put(ItemID.IRON_THROWNAXE, 4);
         put(ItemID.STEEL_THROWNAXE, 4);
         put(ItemID.MITHRIL_THROWNAXE, 4);
         put(ItemID.ADAMANT_THROWNAXE, 4);
         put(ItemID.RUNE_THROWNAXE, 4);
         put(ItemID.DRAGON_THROWNAXE, 4);

         // Attack range of 5
         put(ItemID.COMP_OGRE_BOW, 5);
         put(ItemID.TOXIC_BLOWPIPE, 5);

         // Attack range of 6
         put(ItemID.DORGESHUUN_CROSSBOW, 6);

         // Attack range of 7
         put(ItemID.CROSSBOW, 7);
         put(ItemID.HUNTERS_CROSSBOW, 7);
         put(ItemID.PHOENIX_CROSSBOW, 7);
         put(ItemID.BLURITE_CROSSBOW, 7);
         put(ItemID.BRONZE_CROSSBOW, 7);
         put(ItemID.IRON_CROSSBOW, 7);
         put(ItemID.STEEL_CROSSBOW, 7);
         put(ItemID.MITHRIL_CROSSBOW, 7);
         put(ItemID.ADAMANT_CROSSBOW, 7);
         put(ItemID.RUNE_CROSSBOW, 7);
         put(ItemID.RUNE_CROSSBOW_23601, 7);
         put(ItemID.DRAGON_CROSSBOW, 7);
         put(ItemID.DRAGON_HUNTER_CROSSBOW, 7);
         put(ItemID.SHORTBOW, 7);
         put(ItemID.OAK_SHORTBOW, 7);
         put(ItemID.WILLOW_SHORTBOW, 7);
         put(ItemID.MAPLE_SHORTBOW, 7);
         put(ItemID.YEW_SHORTBOW, 7);
         put(ItemID.MAGIC_SHORTBOW, 7);
         put(ItemID.MAGIC_SHORTBOW_20558, 7);
         put(ItemID.MAGIC_SHORTBOW_I, 7);

         // Attack range of 8
         put(ItemID.ARMADYL_CROSSBOW, 8);
         put(ItemID.ARMADYL_CROSSBOW_23611, 8);
         put(ItemID.KARILS_CROSSBOW, 8);
         put(ItemID.KARILS_CROSSBOW_0, 8);
         put(ItemID.KARILS_CROSSBOW_100, 8);
         put(ItemID.KARILS_CROSSBOW_25, 8);
         put(ItemID.KARILS_CROSSBOW_50, 8);
         put(ItemID.KARILS_CROSSBOW_75, 8);
         put(ItemID.SEERCULL, 8);

         // Attack range of 9
         put(ItemID.LIGHT_BALLISTA, 9);
         put(ItemID.HEAVY_BALLISTA, 9);
         put(ItemID.HEAVY_BALLISTA_23630, 9);
         put(ItemID.CHINCHOMPA, 9);
         put(ItemID.CHINCHOMPA_10033, 9);
         put(ItemID.RED_CHINCHOMPA, 9);
         put(ItemID.RED_CHINCHOMPA_10034, 9);
         put(ItemID.BLACK_CHINCHOMPA, 9);
         put(ItemID._3RD_AGE_BOW, 9);
         put(ItemID.CRAWS_BOW, 9);
         put(ItemID.CRAWS_BOW_U, 9);

         // Attack range of 10
         put(ItemID.LONGBOW, 10);
         put(ItemID.OAK_LONGBOW, 10);
         put(ItemID.WILLOW_LONGBOW, 10);
         put(ItemID.MAPLE_LONGBOW, 10);
         put(ItemID.YEW_LONGBOW, 10);
         put(ItemID.MAGIC_LONGBOW, 10);
         put(ItemID.OGRE_BOW, 10);
         put(ItemID.WILLOW_COMP_BOW, 10);
         put(ItemID.YEW_COMP_BOW, 10);
         put(ItemID.MAGIC_COMP_BOW, 10);
         put(ItemID.CRYSTAL_BOW, 10);
         put(ItemID.CRYSTAL_BOW_110, 10);
         put(ItemID.CRYSTAL_BOW_110_I, 10);
         put(ItemID.CRYSTAL_BOW_210, 10);
         put(ItemID.CRYSTAL_BOW_210_I, 10);
         put(ItemID.CRYSTAL_BOW_310, 10);
         put(ItemID.CRYSTAL_BOW_310_I, 10);
         put(ItemID.CRYSTAL_BOW_410, 10);
         put(ItemID.CRYSTAL_BOW_410_I, 10);
         put(ItemID.CRYSTAL_BOW_510, 10);
         put(ItemID.CRYSTAL_BOW_510_I, 10);
         put(ItemID.CRYSTAL_BOW_610, 10);
         put(ItemID.CRYSTAL_BOW_610_I, 10);
         put(ItemID.CRYSTAL_BOW_710, 10);
         put(ItemID.CRYSTAL_BOW_710_I, 10);
         put(ItemID.CRYSTAL_BOW_810, 10);
         put(ItemID.CRYSTAL_BOW_810_I, 10);
         put(ItemID.CRYSTAL_BOW_910, 10);
         put(ItemID.CRYSTAL_BOW_910_I, 10);
         put(ItemID.CRYSTAL_BOW_24123, 10);
         put(ItemID.CRYSTAL_BOW_ATTUNED, 10);
         put(ItemID.CRYSTAL_BOW_BASIC, 10);
         put(ItemID.CRYSTAL_BOW_FULL, 10);
         put(ItemID.CRYSTAL_BOW_FULL_I, 10);
         put(ItemID.CRYSTAL_BOW_INACTIVE, 10);
         put(ItemID.CRYSTAL_BOW_PERFECTED, 10);
         put(ItemID.DARK_BOW, 10);
         put(ItemID.DARK_BOW_12765, 10);
         put(ItemID.DARK_BOW_12766, 10);
         put(ItemID.DARK_BOW_12767, 10);
         put(ItemID.DARK_BOW_12768, 10);
         put(ItemID.DARK_BOW_20408, 10);
         put(ItemID.TWISTED_BOW, 10);
      }
   };

   public int rangeShort = ATTACK_RANGE_DEFAULT;
   public int rangeLong  = ATTACK_RANGE_DEFAULT;
   public final int rangeMagic = ATTACK_RANGE_MAX;

   @Provides
   AttackRangeConfig provideConfig(ConfigManager configManager)
   {
      return configManager.getConfig(AttackRangeConfig.class);
   }

   @Override
   protected void startUp()
   {
      overlayManager.add(overlay);
   }

   @Override
   protected void shutDown()
   {
      overlayManager.remove(overlay);
   }

   @Subscribe
   public void onPlayerChanged(PlayerChanged playerChanged)
   {
      Player player = playerChanged.getPlayer();

      if (player != client.getLocalPlayer())
         return;

      PlayerComposition composition = player.getPlayerComposition();

      if (composition != null)
      {
         int weaponID = composition.getEquipmentId(KitType.WEAPON);
         int RANGING_LONG_ADDITION = 2;

         rangeShort = ranges.getOrDefault(weaponID, ATTACK_RANGE_DEFAULT);

         if (rangeShort != ATTACK_RANGE_DEFAULT)
            rangeLong = Math.min(rangeShort + RANGING_LONG_ADDITION, ATTACK_RANGE_MAX);
      }
   }
}
