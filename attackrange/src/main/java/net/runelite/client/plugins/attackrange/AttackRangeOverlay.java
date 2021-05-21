/* 
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.attackrange;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class AttackRangeOverlay extends Overlay
{
    private Client client;
    private AttackRangePlugin plugin;
    private AttackRangeConfig config;

    @Inject
    AttackRangeOverlay(Client client, AttackRangePlugin plugin, AttackRangeConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();

        if (player == null)
            return null;

        WorldPoint middle = player.getWorldLocation();

        // Magic range
        if (config.magicEnabled())
        {
            for (int x = -plugin.rangeMagic; x <= plugin.rangeMagic; x++)
            {
                for (int y = -plugin.rangeMagic; y <= plugin.rangeMagic; y++)
                {
                    WorldPoint worldPoint = new WorldPoint(middle.getX() + x, middle.getY() + y, middle.getPlane());
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

                    if (localPoint == null)
                        continue;

                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
                    renderAreaTilePolygon(graphics, tilePoly, Color.BLUE, config.magicOp());
                }
            }
        }

        // Long range
        if (config.longEnabled())
        {
            for (int x = -plugin.rangeLong; x <= plugin.rangeLong; x++)
            {
                for (int y = -plugin.rangeLong; y <= plugin.rangeLong; y++)
                {
                    WorldPoint worldPoint = new WorldPoint(middle.getX() + x, middle.getY() + y, middle.getPlane());
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

                    if (localPoint == null)
                        continue;

                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
                    renderAreaTilePolygon(graphics, tilePoly, Color.YELLOW, config.longOp());
                }
            }
        }

        // Short range
        if (config.shortEnabled())
        {
            for (int x = -plugin.rangeShort; x <= plugin.rangeShort; x++)
            {
                for (int y = -plugin.rangeShort; y <= plugin.rangeShort; y++)
                {
                    WorldPoint worldPoint = new WorldPoint(middle.getX() + x, middle.getY() + y, middle.getPlane());
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

                    if (localPoint == null)
                        continue;

                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
                    renderAreaTilePolygon(graphics, tilePoly, Color.RED, config.shortOp());
                }
            }
        }

        return null;
    }

    public static void renderAreaTilePolygon(Graphics2D graphics, Shape poly, Color color, int opacity)
    {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
        graphics.fill(poly);
    }
}
