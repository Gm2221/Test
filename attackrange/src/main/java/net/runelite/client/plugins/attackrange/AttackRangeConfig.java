/* 
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.attackrange;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("attackrange")
public interface AttackRangeConfig extends Config
{
    @ConfigItem(
        position = 0,
        keyName = "shortEnabled",
        name = "Short range enabled",
        description = ""
    )
    default boolean shortEnabled()
    {
        return true;
    }

    @ConfigItem(
        position = 1,
        keyName = "shortOp",
        name = "Short range opacity",
        description = ""
    )
    default int shortOp()
    {
        return 20;
    }

    @ConfigItem(
        position = 2,
        keyName = "longEnabled",
        name = "Long range enabled",
        description = ""
    )
    default boolean longEnabled()
    {
        return true;
    }

    @ConfigItem(
        position = 3,
        keyName = "longOp",
        name = "Long range opacity",
        description = ""
    )
    default int longOp()
    {
        return 20;
    }

    @ConfigItem(
        position = 4,
        keyName = "magicEnabled",
        name = "Magic range enabled",
        description = ""
    )
    default boolean magicEnabled()
    {
        return true;
    }

    @ConfigItem(
        position = 5,
        keyName = "magicOp",
        name = "Magic range opacity",
        description = ""
    )
    default int magicOp()
    {
        return 20;
    }
}
