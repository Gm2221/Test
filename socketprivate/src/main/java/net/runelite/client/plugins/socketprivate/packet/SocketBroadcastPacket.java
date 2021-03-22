package net.runelite.client.plugins.socketprivate.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.plugins.socketprivate.org.json.JSONObject;

/**
 * Event triggered by alternative plugins, broadcasting a packet to the server.
 */
@AllArgsConstructor
public class SocketBroadcastPacket
{

	@Getter(AccessLevel.PUBLIC)
	private JSONObject payload;

}
