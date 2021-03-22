/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import com.google.common.collect.Ordering;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.function.Function;
import javax.swing.*;

import net.runelite.client.plugins.socketshootingstars.types.StarLocation;
import net.runelite.client.plugins.socketshootingstars.types.StarOrder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;

class ShootingStarsPanel extends PluginPanel
{
    private static final int WORLD_COLUMN_WIDTH = 55;
    private static final int PLAYERS_COLUMN_WIDTH = 45;
    private static final int PING_COLUMN_WIDTH = 47;

    private final JPanel listContainer = new JPanel();
    private final ShootingStarsConfig config;

    private StarTableHeader worldHeader;
    private StarTableHeader timeHeader;
    private StarTableHeader locationHeader;
    private StarTableHeader tierHeader;

    private StarOrder orderIndex = StarOrder.WORLD;
    private boolean ascendingOrder = true;

    private final ArrayList<StarTableRow> rows = new ArrayList<>();
    private final ArrayList<StarInfo> info = new ArrayList<>();

    ShootingStarsPanel(ShootingStarsConfig config)
    {
        this.config = config;

        setBorder(null);
        setLayout(new DynamicGridLayout(0, 1));

        JPanel headerContainer = buildHeader();

        listContainer.setLayout(new GridLayout(0, 1));

        add(headerContainer);
        add(listContainer);
    }

    void updateList()
    {
        rows.sort((r1, r2) ->
        {
            switch (orderIndex)
            {
                case TIER:
                    return getCompareValue(r1, r2, row -> row.tier);
                case WORLD:
                    return getCompareValue(r1, r2, row -> row.world);
                case TIME:
                    return getCompareValue(r1, r2, row -> row.time);
                default:
                    return 0;
            }
        });

        listContainer.removeAll();

        for (StarTableRow row : rows)
        {
            row.setBackground(ColorScheme.DARK_GRAY_COLOR);
            listContainer.add(row);
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    @SuppressWarnings("rawtypes")
    private int getCompareValue(StarTableRow row1, StarTableRow row2, Function<StarTableRow, Comparable> compareByFn)
    {
        Ordering<Comparable> ordering = Ordering.natural();
        if (!ascendingOrder)
        {
            ordering = ordering.reverse();
        }
        ordering = ordering.nullsLast();
        return ordering.compare(compareByFn.apply(row1), compareByFn.apply(row2));
    }

    void clearData()
    {
        rows.clear();
        info.clear();
    }

    void draw()
    {
        rows.clear();

        for (StarInfo starInfo : info)
        {
            StarTableRow r = buildRow(starInfo);
            if (r != null)
                rows.add(r);
        }

        updateList();
    }

    public void addInfo(StarInfo r)
    {
        info.removeIf(i -> i.getWorld() == r.getWorld());
        info.add(r);
    }

    public void removeInfo(StarInfo r)
    {
        info.removeIf(i -> i.getWorld() == r.getWorld());
    }

    private void orderBy(StarOrder order)
    {
        tierHeader.highlight(false, ascendingOrder);
        worldHeader.highlight(false, ascendingOrder);
        timeHeader.highlight(false, ascendingOrder);

        switch (order)
        {
            case TIER:
                tierHeader.highlight(true, ascendingOrder);
                break;
            case WORLD:
                worldHeader.highlight(true, ascendingOrder);
                break;
            case TIME:
                timeHeader.highlight(true, ascendingOrder);
                break;
        }

        orderIndex = order;
        updateList();
    }

    private JPanel buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        JPanel leftSide = new JPanel(new BorderLayout());
        JPanel rightSide = new JPanel(new BorderLayout());

        tierHeader = new StarTableHeader("Tier", orderIndex == StarOrder.TIER, ascendingOrder, true);
        tierHeader.setPreferredSize(new Dimension(PING_COLUMN_WIDTH, 0));
        tierHeader.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isRightMouseButton(mouseEvent))
                {
                    return;
                }
                ascendingOrder = orderIndex != StarOrder.TIER || !ascendingOrder;
                orderBy(StarOrder.TIER);
            }
        });

        worldHeader = new StarTableHeader("World", orderIndex == StarOrder.WORLD, ascendingOrder, true);
        worldHeader.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
        worldHeader.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isRightMouseButton(mouseEvent))
                {
                    return;
                }
                ascendingOrder = orderIndex != StarOrder.WORLD || !ascendingOrder;
                orderBy(StarOrder.WORLD);
            }
        });

        timeHeader = new StarTableHeader("Time", orderIndex == StarOrder.TIME, ascendingOrder, true);
        timeHeader.setPreferredSize(new Dimension(PLAYERS_COLUMN_WIDTH, 0));
        timeHeader.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isRightMouseButton(mouseEvent))
                {
                    return;
                }
                ascendingOrder = orderIndex != StarOrder.TIME || !ascendingOrder;
                orderBy(StarOrder.TIME);
            }
        });

        locationHeader = new StarTableHeader("Location", false, ascendingOrder, false);

        leftSide.add(worldHeader, BorderLayout.WEST);
        leftSide.add(timeHeader, BorderLayout.CENTER);

        rightSide.add(locationHeader, BorderLayout.CENTER);
        rightSide.add(tierHeader, BorderLayout.EAST);

        header.add(leftSide, BorderLayout.WEST);
        header.add(rightSide, BorderLayout.CENTER);

        return header;
    }

    private StarTableRow buildRow(StarInfo info)
    {
        if (config.regionFilter() != StarLocation.UNKNOWN)
        {
            if (info.getArea() != config.regionFilter())
                return null;
        }

        LocalTime now = LocalTime.now(ZoneId.of("UTC"));
        LocalTime eta = info.getTime().minusHours(now.getHour()).minusMinutes(now.getMinute());

        if (eta.isAfter(now))
        {
            LocalTime expiry = now.minusHours(info.getTime().getHour()).minusMinutes(info.getTime().getMinute());

            if (expiry.getMinute() > config.expiryPeriod() && info.getTier() == 0)
                return null;
        }

        StarTableRow row = new StarTableRow(info.getWorld(), info.getTime(), info.getTier(), info.getArea(), info.getLocation());
        row.setBackground(ColorScheme.DARK_GRAY_COLOR);
        return row;
    }
}