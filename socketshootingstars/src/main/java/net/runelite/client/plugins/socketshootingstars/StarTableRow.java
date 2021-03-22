/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalTime;
import java.time.ZoneId;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.socketshootingstars.types.StarExactLocation;
import net.runelite.client.plugins.socketshootingstars.types.StarLocation;
import net.runelite.client.ui.FontManager;

import static net.runelite.client.plugins.socketshootingstars.ShootingStarsPlugin.timeFormat;

class StarTableRow extends JPanel
{
	private static final int WORLD_COLUMN_WIDTH = 55;
	private static final int TIME_COLUMN_WIDTH = 45;
	private static final int TIER_COLUMN_WIDTH = 35;

	public StarExactLocation location;
	public StarLocation area;
	public LocalTime time;
	public int world;
	public int tier;

	StarTableRow(int world, LocalTime time, Integer tier, StarLocation area, StarExactLocation location)
	{
		this.world = world;
		this.time = time;
		this.tier = tier;

		this.location = location;
		this.area = area;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(2, 0, 2, 0));

		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

		setComponentPopupMenu(popupMenu);

		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());
		leftSide.setOpaque(false);
		rightSide.setOpaque(false);

		JPanel worldField = buildWorldField();
		worldField.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
		worldField.setOpaque(false);

		JPanel tierField = buildTierField();
		tierField.setPreferredSize(new Dimension(TIER_COLUMN_WIDTH, 0));
		tierField.setOpaque(false);

		JPanel timeField = buildTimeField();
		timeField.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		timeField.setOpaque(false);

		JPanel locationField = buildLocationField();
		locationField.setBorder(new EmptyBorder(5, 5, 5, 5));
		locationField.setOpaque(false);

		leftSide.add(worldField, BorderLayout.WEST);
		leftSide.add(timeField, BorderLayout.CENTER);
		rightSide.add(locationField, BorderLayout.CENTER);
		rightSide.add(tierField, BorderLayout.EAST);

		add(leftSide, BorderLayout.WEST);
		add(rightSide, BorderLayout.CENTER);
	}

	private JPanel buildTimeField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		LocalTime now = LocalTime.now(ZoneId.of("UTC"));
		LocalTime eta = time.minusHours(now.getHour()).minusMinutes(now.getMinute());

		String diff = "00:00";
		if (eta.isBefore(now))
			diff = eta.format(timeFormat);
		else
			diff = "-" + now.minusHours(time.getHour()).minusMinutes(time.getMinute()).format(timeFormat);

		JLabel timeField = new JLabel(diff);
		timeField.setFont(FontManager.getRunescapeSmallFont());

		column.add(timeField, BorderLayout.WEST);

		return column;
	}

	private JPanel buildTierField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		String text = "-";
		if (tier != 0)
			text = Integer.toString(tier);

		JLabel tierField = new JLabel(text);

		tierField.setFont(FontManager.getRunescapeSmallFont());

		column.add(tierField, BorderLayout.EAST);

		return column;
	}

	private JPanel buildLocationField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		JLabel locationField;
		if (!location.toString().toLowerCase().contains("unknown"))
		{
			locationField = new JLabel(location.toString());
			locationField.setForeground(Color.GREEN);
		}
		else if (!area.toString().toLowerCase().contains("unknown"))
		{
			LocalTime now = LocalTime.now(ZoneId.of("UTC"));
			LocalTime eta = time.minusHours(now.getHour()).minusMinutes(now.getMinute());

			locationField = new JLabel(area.toString());
			if (eta.isAfter(now))
				locationField.setForeground(Color.RED);
			else
				locationField.setForeground(Color.YELLOW);
		}
		else
		{
			locationField = new JLabel("-");
			locationField.setForeground(Color.RED);
		}

		locationField.setFont(FontManager.getRunescapeSmallFont());

		column.add(locationField, BorderLayout.WEST);

		return column;
	}

	private JPanel buildWorldField()
	{
		JPanel column = new JPanel(new BorderLayout(7, 0));
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		JLabel worldField = new JLabel(Integer.toString(world));
		column.add(worldField, BorderLayout.CENTER);

		return column;
	}
}
