/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars.types;

public enum StarLocation
{
    ASGARNIA(0),
    CRANDOR_OR_KARAMJA(1),
    FELDIP_HILLS_OR_ISLE_OF_SOULS(2),
    FOSSIL_OR_MOSLEHARMLESS(3),
    FREMENNIK_LANDS_OR_LUNAR_ISLE(4),
    GREAT_KOUREND(5),
    KANDARIN(6),
    KEBOS_LOWLANDS(7),
    KHARIDIAN_DESERT(8),
    MISTHALIN(9),
    MORYTANIA(10),
    PISCATORIS_OR_GNOME_STRONGHOLD(11),
    TIRANNWN(12),
    WILDERNESS(13),
    UNKNOWN(14);

    public final Integer areaID;

    StarLocation(Integer areaID)
    {
        this.areaID = areaID;
    }
}
