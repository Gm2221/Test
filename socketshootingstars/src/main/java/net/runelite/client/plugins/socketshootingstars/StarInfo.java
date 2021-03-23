/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.util.Text;
import net.runelite.client.plugins.socketshootingstars.types.StarExactLocation;
import net.runelite.client.plugins.socketshootingstars.types.StarLocation;
import org.apache.commons.lang3.ArrayUtils;
import java.time.LocalTime;
import java.time.ZoneId;
import static net.runelite.client.plugins.socketshootingstars.ShootingStarsPlugin.timeFormat;

@Data
@Slf4j
public class StarInfo
{
    public static final int[] MISTHALIN_REGION = {StarExactLocation.EAST_LUMBRIDGE_MINE.regionID, StarExactLocation.WEST_LUMBRIDGE_MINE.regionID, StarExactLocation.DRAYNOR_VILLAGE.regionID, StarExactLocation.VARROCK_EAST_BANK.regionID, StarExactLocation.EAST_VARROCK_MINE.regionID, StarExactLocation.WEST_VARROCK_MINE.regionID};
    public static final int[] KHARIDIAN_DESERT_REGION = {StarExactLocation.AL_KHARID_MINE.regionID, StarExactLocation.AL_KHARID_BANK.regionID, StarExactLocation.UZER_MINE.regionID, StarExactLocation.DESERT_QUARRY.regionID, StarExactLocation.AGILITY_PYRAMID_MINE.regionID, StarExactLocation.NARDAH.regionID, StarExactLocation.DUEL_ARENA.regionID};
    public static final int[] WILDERNESS_REGION = {StarExactLocation.ZAMMY_MAGE_MINE.regionID, StarExactLocation.FORTRESS_MINE.regionID, StarExactLocation.HOBGOBLIN_MINE.regionID, StarExactLocation.RUNEROCKS_MINE.regionID, StarExactLocation.RESOURCE_AREA.regionID, StarExactLocation.MAGE_ARENA.regionID, StarExactLocation.WEST_MAGE_ARENA.regionID};
    public static final int[] ASGARNIA_REGION = {StarExactLocation.DRARWEN_MINE.regionID, StarExactLocation.MINING_GUILD.regionID, StarExactLocation.WEST_FALADOR.regionID, StarExactLocation.TAVERLEY.regionID, StarExactLocation.CRAFTING_GUILD.regionID, StarExactLocation.RIMMINGTON.regionID};
    public static final int[] CRANDOR_OR_KARAMJA_REGION = {StarExactLocation.SOUTH_CRANDOR.regionID, StarExactLocation.NORTH_CRANDOR.regionID, StarExactLocation.NORTH_BRIMHAVEN.regionID, StarExactLocation.SOUTH_BRIMHAVEN.regionID, StarExactLocation.NATURE_ALTAR.regionID, StarExactLocation.GEM_MINE.regionID};
    public static final int[] FREMENNIK_LANDS_OR_LUNAR_ISLE_REGION = {StarExactLocation.RELLEKKA_MINE.regionID, StarExactLocation.KELDAGRIM_MINE.regionID, StarExactLocation.MISCELLANIA_MINE.regionID, StarExactLocation.JATIZSO_MINE.regionID, StarExactLocation.CENTRAL_FREMMENIK_ISLES.regionID, StarExactLocation.LUNAR_ISLE_MINE.regionID};
    public static final int[] PISCATORIS_OR_GNOME_STRONGHOLD_REGION = {StarExactLocation.PISCATORIS_MINE.regionID, StarExactLocation.GRAND_TREE.regionID, StarExactLocation.GNOME_STRONGHOLD.regionID};
    public static final int[] TIRANNWN_REGION = {StarExactLocation.ISAFDAR_MINE.regionID, StarExactLocation.ARANDAR_MINE.regionID, StarExactLocation.LLETYA.regionID, StarExactLocation.TRAHAEARN_MINE.regionID, StarExactLocation.MYNYDD_MINE.regionID};
    public static final int[] KANDARIN_REGION = {StarExactLocation.CATHERBY_BANK.regionID, StarExactLocation.YANILLE_BANK.regionID, StarExactLocation.PORT_KHAZARD_MINE.regionID, StarExactLocation.LEGENDS_GUILD.regionID, StarExactLocation.COAL_TRUCKS.regionID, StarExactLocation.ARDOUGNE_MONASTERY.regionID};
    public static final int[] FOSSIL_OR_MOSLEHARMLESS_REGION = {StarExactLocation.FOSSIL_ISLAND_MINE.regionID, StarExactLocation.VOLCANIC_MINE.regionID, StarExactLocation.MOS_LE_HARMLESS.regionID};
    public static final int[] FELDIP_HILLS_OR_ISLE_OF_SOULS_REGION = {StarExactLocation.FELDIP_HUNTER.regionID, StarExactLocation.RANTZ_CAVE.regionID, StarExactLocation.CORSAIR_COVE.regionID, StarExactLocation.CORSAIR_RESOURCE_AREA.regionID, StarExactLocation.MYTHS_GUILD.regionID, StarExactLocation.ISLE_OF_SOULS.regionID};
    public static final int[] KEBOS_LOWLANDS_REGION = {StarExactLocation.KEBOS_SWAMP.regionID, StarExactLocation.MOUNT_KARUULM_MINE.regionID, StarExactLocation.MOUNT_KARUULM_BANK.regionID, StarExactLocation.MOUNT_QUIDAMORTEM.regionID};
    public static final int[] GREAT_KOUREND_REGION = {StarExactLocation.HOSIDIUS_MINE.regionID, StarExactLocation.SHAYZIEN_MINE.regionID, StarExactLocation.PISCARILIUS_MINE.regionID, StarExactLocation.ARCEUUS_ESSENCE.regionID, StarExactLocation.LOVAKITE_MINE.regionID, StarExactLocation.LOVAKENGJ_BANK.regionID};
    public static final int[] MORYTANIA_REGION = {StarExactLocation.CANIFIS_BANK.regionID, StarExactLocation.BURG_DE_ROTT.regionID, StarExactLocation.ABANDONED_MINE.regionID, StarExactLocation.TOB_BANK.regionID, StarExactLocation.DAEYALT_ESSENCE_MINE.regionID};

    private StarExactLocation location;
    private StarLocation area;
    private LocalTime time;
    private int world;
    private int tier;

    StarInfo(String text, int world)
    {
        this.world = world;
        this.area = setArea(text);
        this.time = setTime(text);
        this.location = StarExactLocation.UNKNOWN;
        this.tier = 0;

        log.debug("Scout info: World {} will have star at {} - {}", this.area.toString(), time.format(timeFormat), this.world);
    }

    StarInfo(int regionID, int objectID, int world)
    {
        this.time = LocalTime.now(ZoneId.of("UTC"));
        this.world = world;
        this.tier = checkTier(objectID);

        setAreaLocation(regionID);

        log.debug("StarInfo: World {}, tier {} star at {} ({})", this.world, this.tier, this.location.toString(), this.area.toString());
    }

    StarInfo(String starInfoText)
    {
        String[] parts = starInfoText.split(",");

        for (StarExactLocation e : StarExactLocation.values())
        {
            if (e.regionID == Integer.parseInt(parts[0]))
                this.location = e;
        }

        for (StarLocation e : StarLocation.values())
        {
            if (e.areaID == Integer.parseInt(parts[1]))
                this.area = e;
        }

        this.time = LocalTime.parse(parts[2]);
        this.world = Integer.parseInt(parts[3]);
        this.tier = Integer.parseInt(parts[4]);
    }

    private void setAreaLocation(int area)
    {
        this.location = StarExactLocation.UNKNOWN;
        this.area = StarLocation.UNKNOWN;

        if (ArrayUtils.contains(MISTHALIN_REGION, area))
            this.area = StarLocation.MISTHALIN;
        else if (ArrayUtils.contains(KHARIDIAN_DESERT_REGION, area))
            this.area = StarLocation.KHARIDIAN_DESERT;
        else if (ArrayUtils.contains(WILDERNESS_REGION, area))
            this.area = StarLocation.WILDERNESS;
        else if (ArrayUtils.contains(ASGARNIA_REGION, area))
            this.area = StarLocation.ASGARNIA;
        else if (ArrayUtils.contains(CRANDOR_OR_KARAMJA_REGION, area))
            this.area = StarLocation.CRANDOR_OR_KARAMJA;
        else if (ArrayUtils.contains(FREMENNIK_LANDS_OR_LUNAR_ISLE_REGION, area))
            this.area = StarLocation.FREMENNIK_LANDS_OR_LUNAR_ISLE;
        else if (ArrayUtils.contains(PISCATORIS_OR_GNOME_STRONGHOLD_REGION, area))
            this.area = StarLocation.PISCATORIS_OR_GNOME_STRONGHOLD;
        else if (ArrayUtils.contains(TIRANNWN_REGION, area))
            this.area = StarLocation.TIRANNWN;
        else if (ArrayUtils.contains(KANDARIN_REGION, area))
            this.area = StarLocation.KANDARIN;
        else if (ArrayUtils.contains(FOSSIL_OR_MOSLEHARMLESS_REGION, area))
            this.area = StarLocation.FOSSIL_OR_MOSLEHARMLESS;
        else if (ArrayUtils.contains(FELDIP_HILLS_OR_ISLE_OF_SOULS_REGION, area))
            this.area = StarLocation.FELDIP_HILLS_OR_ISLE_OF_SOULS;
        else if (ArrayUtils.contains(KEBOS_LOWLANDS_REGION, area))
            this.area = StarLocation.KEBOS_LOWLANDS;
        else if (ArrayUtils.contains(GREAT_KOUREND_REGION, area))
            this.area = StarLocation.GREAT_KOUREND;
        else if (ArrayUtils.contains(MORYTANIA_REGION, area))
            this.area = StarLocation.MORYTANIA;

        if (this.area != StarLocation.UNKNOWN)
        {
            for (StarExactLocation e : StarExactLocation.values())
            {
                if (e.regionID == area)
                    this.location = e;
            }
        }
    }

    private int checkTier(int object)
    {
        if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[0])
            return 9;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[1])
            return 8;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[2])
            return 7;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[3])
            return 6;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[4])
            return 5;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[5])
            return 4;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[6])
            return 3;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[7])
            return 2;
        else if (object == ShootingStarsPlugin.SHOOTING_STAR_OBJECTS[8])
            return 1;
        else
            return 0;
    }

    private StarLocation setArea(String s)
    {
        if (s.contains("misthalin"))
            return StarLocation.MISTHALIN;
        else if (s.contains("kharidian"))
            return StarLocation.KHARIDIAN_DESERT;
        else if (s.contains("wilderness"))
            return StarLocation.WILDERNESS;
        else if (s.contains("asgarnia"))
            return StarLocation.ASGARNIA;
        else if (s.contains("crandor"))
            return StarLocation.CRANDOR_OR_KARAMJA;
        else if (s.contains("fremennik"))
            return StarLocation.FREMENNIK_LANDS_OR_LUNAR_ISLE;
        else if (s.contains("piscatoris"))
            return StarLocation.PISCATORIS_OR_GNOME_STRONGHOLD;
        else if (s.contains("tirannwn"))
            return StarLocation.TIRANNWN;
        else if (s.contains("kandarin"))
            return StarLocation.KANDARIN;
        else if (s.contains("fossil"))
            return StarLocation.FOSSIL_OR_MOSLEHARMLESS;
        else if (s.contains("feldip"))
            return StarLocation.FELDIP_HILLS_OR_ISLE_OF_SOULS;
        else if (s.contains("kebos"))
            return StarLocation.KEBOS_LOWLANDS;
        else if (s.contains("kourend"))
            return StarLocation.GREAT_KOUREND;
        else if (s.contains("morytania"))
            return StarLocation.MORYTANIA;
        else
            return StarLocation.UNKNOWN;
    }

    private LocalTime setTime(String s)
    {
        String[] matches = Text.standardize(s.replaceAll("[a-zA-Z!.<>]", "")).split("\\s+");
        LocalTime UTC_time = LocalTime.now(ZoneId.of("UTC"));
        long minutes = 0;

        if (matches.length == 2)
            minutes = Integer.parseInt(matches[0]) + 1;
        else if (matches.length == 4)
            minutes = Integer.parseInt(matches[0]) * 60L + Integer.parseInt(matches[1]) + 1;

        return UTC_time.plusMinutes(minutes);
    }

    public String toStringForm()
    {
        String ret = "";

        ret += location.regionID + ",";
        ret += area.areaID + ",";
        ret += time.toString() + ",";
        ret += world + ",";
        ret += tier;

        return ret;
    }
}
