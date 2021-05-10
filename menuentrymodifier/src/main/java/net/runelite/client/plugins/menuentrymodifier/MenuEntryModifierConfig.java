/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.menuentrymodifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("menuentrymodifier")
public interface MenuEntryModifierConfig extends Config
{
    //region sections
    @ConfigSection(
            name = "General",
            description = "",
            position = -2,
            keyName = "generalSection"
    )
    String generalSection = "General";

    @ConfigSection(
            name = "Filters",
            description = "",
            position = -1,
            keyName = "filtersSection"
    )
    String filtersSection = "Filters";
    //endregion sections

    //region general
    @ConfigItem(
            keyName = "hotkeyRequired",
            name = "Require hotkey press",
            description = "Require hotkey down to perform filtering",
            position = 0,
            section = generalSection
    )
    default boolean hotkeyRequired() { return true; }

    @ConfigItem(
            keyName = "hotkeyButton",
            name = "Hotkey",
            description = "Hotkey for filtering",
            position = 1,
            section = generalSection
    )
    default Keybind hotkeyButton() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "menuFilter",
            name = "Menu entry modifier filter",
            description = "Choose filter type for menu entry filtering",
            position = 2,
            section = generalSection
    )
    default MenuEntryModifierPlugin.filterOption menuFilter() { return MenuEntryModifierPlugin.filterOption.NONE; }

    @ConfigItem(
            keyName = "removeEnabled",
            name = "Remove customization",
            description = "Remove option(s) enabled",
            position = 3,
            section = generalSection
    )
    default boolean removeEnabled() { return false; }
    //endregion general

    //region filters
    @ConfigItem(
            keyName = "priorityList",
            name = "Priority list filter",
            description = "List of priority configure options. Syntax target,option such as man,pickpocket. Newline for each option, case is insensitive.",
            position = 0,
            section = filtersSection
    )
    default String priorityList() { return "banker,bank\nhammer,buy 50"; }

    @ConfigItem(
            keyName = "removeList",
            name = "Remove list filter",
            description = "List of removed menu entries. Such as examine. Newline for each option, case is insensitive.",
            position = 1,
            section = filtersSection
    )
    default String removeList() { return "examine\nempty"; }
    //endregion filters
}
