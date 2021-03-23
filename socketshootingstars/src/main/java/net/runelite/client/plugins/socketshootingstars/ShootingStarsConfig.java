/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.socketshootingstars.types.StarLocation;

@ConfigGroup("shootingstars")
public interface ShootingStarsConfig extends Config
{
    @ConfigSection(
        name = "Only show",
        description = "Select filter, set to UNKNOWN to disable filtering",
        position = -1
    )
    String section = "filter";

    @ConfigItem(
        keyName = "regionFilter",
        name = "",
        description = "",
        position = 0,
        section = "filter"
    )
    default StarLocation regionFilter() { return StarLocation.UNKNOWN; }

    @ConfigItem(
            keyName = "expiryPeriod",
            name = "Expire old after",
            description = "Select how long the expired stars should be shown",
            position = 1
    )
    default int expiryPeriod() { return 1; }
}
