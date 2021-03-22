/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import net.runelite.client.plugins.socketprivate.org.json.JSONArray;
import net.runelite.client.plugins.socketprivate.org.json.JSONObject;
import net.runelite.client.plugins.socketprivate.packet.SocketBroadcastPacket;

public class SocketUtils
{
    public static SocketBroadcastPacket sendFlag(String msg, String type, String username)
    {
        JSONArray data = new JSONArray();
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", msg);
        jsonmsg.put("sender", username);
        jsonmsg.put("type", type);
        data.put(jsonmsg);
        JSONObject send = new JSONObject();
        send.put("shootingstars", data);
        return new SocketBroadcastPacket(send);
    }

    public static void handlePacket(JSONObject payload, String username, ShootingStarsPanel panel)
    {
        try
        {
            JSONArray data;
            JSONObject jsonObject;
            String msg;
            String sender;
            String type;

            if (payload.has("shootingstars"))
            {
                data = payload.getJSONArray("shootingstars");
                jsonObject = data.getJSONObject(0);
                msg = jsonObject.getString("msg");
                sender = jsonObject.getString("sender");
                type = jsonObject.getString("type");

                if (sender.equals(username))
                    return;

                StarInfo i = new StarInfo(msg);

                if (type.equals("add"))
                    panel.addInfo(i);
                else
                    panel.removeInfo(i);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
