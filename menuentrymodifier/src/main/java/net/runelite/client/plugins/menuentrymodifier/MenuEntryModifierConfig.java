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
            name = "Filters",
            description = "",
            position = 10,
            keyName = "filtersSection"
    )
    String filtersSection = "Filters";
    //endregion sections

    //region general
    @ConfigItem(
            keyName = "hotkeyButton",
            name = "Hotkey",
            description = "Hotkey for filtering",
            position = 0
    )
    default Keybind hotkeyButton() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "removeEnabled",
            name = "Removing enabled",
            description = "Remove option(s) enabled",
            position = 1
    )
    default boolean removeEnabled() { return false; }
    //endregion general

    //region filters
    @ConfigItem(
            keyName = "menuFilter",
            name = "Priority filter",
            description = "Choose filter type for menu entry priority filtering",
            position = 0,
            section = filtersSection
    )
    default MenuEntryModifierPlugin.filterOption menuFilter() { return MenuEntryModifierPlugin.filterOption.NONE; }

    @ConfigItem(
            keyName = "priorityList",
            name = "Priority list filter",
            description = "List of priority configure options. Syntax target,option such as man,pickpocket. Newline for each option, case is insensitive.",
            position = 1,
            section = filtersSection
    )
    default String priorityList() { return "banker,bank\nhammer,buy 50"; }

    @ConfigItem(
            keyName = "hotkeyFilter",
            name = "Hotkey filter",
            description = "Choose filter type for menu entry hotkey filtering",
            position = 2,
            section = filtersSection
    )
    default MenuEntryModifierPlugin.filterOption hotkeyFilter() { return MenuEntryModifierPlugin.filterOption.OPTION; }

    @ConfigItem(
            keyName = "hotkeyList",
            name = "Hotkey list filter",
            description = "List of hotkey configure options. Syntax target,option such as man,pickpocket. Newline for each option, case is insensitive.",
            position = 3,
            section = filtersSection
    )
    default String hotkeyList() { return "*,trade"; }

    @ConfigItem(
            keyName = "removeList",
            name = "Remove list filter",
            description = "List of removed menu entries. Such as examine. Newline for each option, case is insensitive.",
            position = 4,
            section = filtersSection
    )
    default String removeList() { return "examine\nempty"; }
    //endregion filters
}
