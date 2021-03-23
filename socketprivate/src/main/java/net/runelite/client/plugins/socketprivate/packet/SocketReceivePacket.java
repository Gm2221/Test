package net.runelite.client.plugins.socketprivate.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.plugins.socketprivate.org.json.JSONObject;

/**
 * Event triggered by Socket, notifying plugins that a packet has been received.
 * This event is triggered on the client thread.
 */
@AllArgsConstructor
public class SocketReceivePacket
{

	@Getter(AccessLevel.PUBLIC)
	private JSONObject payload;

}
