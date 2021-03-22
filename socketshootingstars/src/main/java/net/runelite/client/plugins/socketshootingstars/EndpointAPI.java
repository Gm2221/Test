/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.socketshootingstars;

import net.runelite.client.plugins.socketprivate.org.json.JSONArray;
import net.runelite.client.plugins.socketprivate.org.json.JSONException;
import net.runelite.client.plugins.socketprivate.org.json.JSONObject;
import net.runelite.client.plugins.socketshootingstars.types.StarExactLocation;
import net.runelite.client.plugins.socketshootingstars.types.StarLocation;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class EndpointAPI
{
    public static String getJSON(String url)
    {
        HttpsURLConnection con = null;
        try
        {
            URL u = new URL(url);
            con = (HttpsURLConnection)u.openConnection();

            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");

            br.close();
            return sb.toString();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.disconnect();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void getData(ShootingStarsPanel panel)
    {
        String json = EndpointAPI.getJSON("https://sek.ai/stars/get.php");
        JSONArray array = new JSONArray(json);
        int i = 0;

        if (json != null)
        {
            while (true)
            {
                try
                {
                    JSONObject obj = array.getJSONObject(i);

                    long epoch = (Long.parseLong(obj.get("maxTime").toString()) + Long.parseLong(obj.get("minTime").toString())) / 2;
                    Instant inst = Instant.ofEpochSecond(epoch);

                    if (inst.isBefore(Instant.now()))
                    {
                        i++;
                        continue;
                    }

                    LocalTime time = inst.atZone(ZoneOffset.UTC).toLocalTime();
                    StarLocation area = StarLocation.UNKNOWN;

                    int world = Integer.parseInt(obj.get("world").toString());

                    for (StarLocation e : StarLocation.values())
                    {
                        if (e.areaID == Integer.parseInt(obj.get("location").toString()))
                            area = e;
                    }

                    String text = StarExactLocation.UNKNOWN.regionID + "," +
                            area.areaID + "," +
                            time.toString() + "," +
                            world + "," +
                            0;

                    StarInfo info = new StarInfo(text);
                    panel.addInfo(info);
                }
                catch (JSONException ignore)
                {
                    break;
                }
                i++;
            }
        }
    }
}
