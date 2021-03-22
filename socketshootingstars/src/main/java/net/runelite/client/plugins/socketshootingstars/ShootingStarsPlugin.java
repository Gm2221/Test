/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.socketprivate.SocketPlugin;
import net.runelite.client.plugins.socketprivate.packet.SocketReceivePacket;
import net.runelite.client.eventbus.EventBus;

@Extension
@PluginDescriptor(
        name = "Shooting stars",
        description = "Displays scout information on shooting stars",
        tags = {"shooting", "star", "dust"},
        enabledByDefault = false
)

@Slf4j
@PluginDependency(SocketPlugin.class)
public class ShootingStarsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ShootingStarsConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    private static final int[] POH_REGION = {7513, 7514, 7769, 7770};
    private boolean isChecking;

    private ExecutorService executorService;
    private NavigationButton navButton;
    private ShootingStarsPanel panel;

    public static final int[] SHOOTING_STAR_OBJECTS = {41221, 41222, 41223, 41224, 41225, 41226, 41227, 41228, 41229};
    public static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    @Provides
    ShootingStarsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ShootingStarsConfig.class);
    }

    protected void startUp()
    {
        panel = new ShootingStarsPanel(config);

        BufferedImage icon = ImageUtil.loadImageResource(ShootingStarsPlugin.class, "icon.png");
        navButton = NavigationButton.builder()
                .tooltip("Shooting stars")
                .icon(icon)
                .priority(3)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
        executorService = Executors.newSingleThreadExecutor();

        fetchData();
    }

    protected void shutDown()
    {
        panel.clearData();
        executorService.shutdown();
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (isChecking)
            scoutData();
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (client.getLocalPlayer() == null)
            return;

        int region = client.getLocalPlayer().getWorldLocation().getRegionID();

        if (ArrayUtils.contains(SHOOTING_STAR_OBJECTS, event.getGameObject().getId()))
        {
            StarInfo info = new StarInfo(region, event.getGameObject().getId(), client.getWorld());
            panel.addInfo(info);

            eventBus.post(SocketUtils.sendFlag(info.toStringForm(), "add", client.getLocalPlayer().getName()));
            SwingUtilities.invokeLater(() -> panel.draw());
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (client.getLocalPlayer() == null)
            return;

        int region = client.getLocalPlayer().getWorldLocation().getRegionID();

        if (event.getGameObject().getId() == SHOOTING_STAR_OBJECTS[8])
        {
            StarInfo info = new StarInfo(region, 0, client.getWorld());
            panel.removeInfo(info);

            eventBus.post(SocketUtils.sendFlag(info.toStringForm(), "remove", client.getLocalPlayer().getName()));
            SwingUtilities.invokeLater(() -> panel.draw());
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event)
    {
        if (ArrayUtils.contains(POH_REGION, client.getMapRegions()[0]))
        {
            if (event.getGroupId() == WidgetID.DIALOG_NOTIFICATION_GROUP_ID)
                isChecking = true;
        }
    }

    private void scoutData()
    {
        if (client.getLocalPlayer() == null)
            return;

        Widget w = client.getWidget(WidgetInfo.DIALOG_NOTIFICATION_CONTINUE);

        if (w != null)
        {
            if (w.getText().contains("shooting star"))
            {
                String text = Text.sanitizeMultilineText(w.getText()).toLowerCase();

                StarInfo info = new StarInfo(text, client.getWorld());
                panel.addInfo(info);

                eventBus.post(SocketUtils.sendFlag(info.toStringForm(), "add", client.getLocalPlayer().getName()));
                SwingUtilities.invokeLater(() -> panel.draw());
            }

            isChecking = false;
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event)
    {
        String name = "";
        if (client.getLocalPlayer() != null)
            name = client.getLocalPlayer().getName();

        SocketUtils.handlePacket(event.getPayload(), name, panel);
    }

    @Schedule(period = 10, unit = ChronoUnit.SECONDS)
    public void refresh()
    {
        SwingUtilities.invokeLater(() -> panel.draw());
    }

    @Schedule(period = 1, unit = ChronoUnit.MINUTES)
    public void fetchData() { executorService.submit(() -> EndpointAPI.getData(panel)); }
}
