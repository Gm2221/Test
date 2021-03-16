/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.shiftwalker;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;
import javax.inject.Inject;
import java.awt.event.KeyEvent;

@Extension
@PluginDescriptor(
        name = "Shiftwalker",
        description = "Press shift to step under",
        tags = {"shift", "step", "walk"},
        enabledByDefault = false
)

public class ShiftWalkerPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private KeyManager keyManager;

    private boolean active;

    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> Keybind.SHIFT)
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (Keybind.SHIFT.matches(e))
                active = true;
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            if (Keybind.SHIFT.matches(e))
                active = false;
        }
    };

    @Override
    protected void startUp()
    {
        active = false;
        keyManager.registerKeyListener(hotkeyListener);
    }

    @Override
    protected void shutDown()
    {
        active = false;
        keyManager.unregisterKeyListener(hotkeyListener);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
            active = false;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (active)
        {
            MenuEntry[] currentEntries = client.getMenuEntries();

            for (int walkIndex = 0; walkIndex < currentEntries.length; walkIndex++)
            {
                if (currentEntries[walkIndex].getOption().toLowerCase().contains("walk"))
                {
                    MenuEntry[] newEntries = new MenuEntry[1];
                    newEntries[0] = currentEntries[walkIndex];
                    client.setMenuEntries(newEntries);
                    break;
                }
            }
        }
    }
}

