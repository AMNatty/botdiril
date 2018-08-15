package cz.tefek.botdiril.persistent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import cz.tefek.botdiril.core.server.ServerConfig;

public class Persistency
{
    public static List<ServerConfig> guildsFromFiles()
    {
        var lsg = new ArrayList<ServerConfig>();

        var fldr = new File("guilds");

        if (!fldr.isDirectory())
        {
            fldr.mkdir();
        }
        else
        {
            for (File file : fldr.listFiles())
            {
                if (file.canRead() && !file.isDirectory())
                {
                    try
                    {
                        lsg.add(parseFile(new JSONTokener(new FileInputStream(file))));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return lsg;
    }

    private static ServerConfig parseFile(JSONTokener jsonTokener)
    {
        ServerConfig sc = null;

        JSONObject jo = new JSONObject(jsonTokener);

        long serverID = jo.getLong("gid");

        boolean isInstalled = jo.optBoolean("installed");

        if (isInstalled)
        {
            String prefix = jo.getString("prefix");
            long uuid_lsb = jo.getLong("uuid-lsb");
            long uuid_msb = jo.getLong("uuid-msb");
            int volume = jo.optInt("music-volume", 50);
            var sus = jo.optJSONArray("superuser-roles");
            var suc = jo.optLong("superuser-printchannel", -1);

            sc = new ServerConfig(serverID, new UUID(uuid_msb, uuid_lsb), prefix);
            sc.setVolume(volume);

            if (sus != null)
            {
                var arl = new ArrayList<Long>();

                for (var object : sus)
                {
                    if (object instanceof Long)
                    {
                        arl.add((Long) object);
                    }
                }

                sc.addAllSuperUserRoles(arl);
            }

            System.out.println(serverID + " :: " + suc);

            if (suc != -1)
            {
                sc.setReportChannel(suc);
            }
        }
        else
        {
            sc = new ServerConfig(serverID);
        }

        return sc;
    }

    public static void deleteServer(long id)
    {
        var fldr = new File("guilds");

        if (fldr.isDirectory())
        {
            new File("guilds/g_" + id + ".json").delete();
        }
    }

    public static void serializeServer(ServerConfig sc)
    {
        synchronized (sc)
        {
            try
            {
                PrintWriter pw = new PrintWriter("guilds/g_" + sc.getID() + ".json");

                var jo = new JSONObject();

                jo.put("gid", sc.getID());

                jo.put("installed", sc.isInstalled());

                if (sc.isInstalled())
                {
                    jo.put("uuid-msb", sc.getUUID().getMostSignificantBits());
                    jo.put("uuid-lsb", sc.getUUID().getLeastSignificantBits());
                    jo.put("prefix", sc.getPrefix());
                    jo.put("music-volume", sc.getVolume());
                    var jar = new JSONArray();
                    jar.put(sc.getAllSuperUseredRoles());
                    jo.put("superuser-roles", jar);
                    var rc = sc.getReportChannelID();
                    if (rc != -1)
                    {
                        jo.put("superuser-printchannel", rc);
                    }
                }

                pw.write(JSONWriter.valueToString(jo));

                pw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
