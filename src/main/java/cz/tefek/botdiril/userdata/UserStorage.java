package cz.tefek.botdiril.userdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.tefek.botdiril.userdata.items.Item;

public class UserStorage
{
    private static ConcurrentHashMap<Long, UserInventory> allUsers = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Long, UserInventory> getAllUsers()
    {
        return allUsers;
    }

    public static void serialize(UserInventory ui)
    {
        synchronized (ui)
        {
            try
            {
                if (!new File("user").isDirectory())
                {
                    new File("user").mkdir();
                }

                PrintWriter pw = new PrintWriter("user/u_" + ui.getUserID() + ".json");

                JSONObject jo = new JSONObject();

                jo.put("id", ui.getUserID());
                jo.put("uuid-msb", ui.getUUID().getMostSignificantBits());
                jo.put("uuid-lsb", ui.getUUID().getLeastSignificantBits());
                jo.put("streak", ui.getStreak());
                jo.put("coins", ui.getCoins());
                jo.put("timers", ui.getTimers());
                jo.put("properties", ui.getProperties());
                var jar = new JSONArray();
                ui.getInventoryRaw().forEach((k, v) -> {
                    jar.put(new JSONObject().put("id", k).put("amount", v));
                });
                jo.put("items", jar);

                pw.write(jo.toString());

                pw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void deserializeAll()
    {
        int i = 0;

        var fldr = new File("user");

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
                        parseFile(new JSONTokener(new FileInputStream(file)));
                        i++;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.printf("%d users successfully loaded.\n", i);
    }

    private static void parseFile(JSONTokener jsonTokener)
    {
        JSONObject jo = new JSONObject(jsonTokener);

        long uid = jo.getLong("id");

        long uuid_msb = jo.getLong("uuid-msb");
        long uuid_lsb = jo.getLong("uuid-lsb");

        int streak = jo.getInt("streak");

        long balance = jo.getLong("coins");

        var timers = (JSONObject) jo.opt("timers");
        var properties = (JSONObject) jo.opt("properties");

        var ui = new UserInventory(uid, new UUID(uuid_msb, uuid_lsb));
        ui.setCoins(balance);
        ui.setStreak(streak);
        jo.getJSONArray("items").forEach(ji -> {
            var i = (JSONObject) ji;
            ui.addItem(Item.getByID(i.getString("id")), i.getLong("amount"));
        });

        if (timers != null)
        {
            var tnames = timers.keySet();

            tnames.forEach(c -> {
                ui.setTimer(c, timers.getLong(c));
            });
        }

        if (properties != null)
        {
            var tnames = properties.keySet();

            tnames.forEach(c -> {
                ui.updateProperty(c, properties.getInt(c));
            });
        }

        allUsers.put(uid, ui);
    }

    public static UserInventory getByID(long uid)
    {
        UserInventory ret;

        if (!allUsers.containsKey(uid))
        {
            ret = new UserInventory(uid, UUID.randomUUID());
            allUsers.put(uid, ret);
        }
        else
        {
            ret = allUsers.get(uid);
        }

        return ret;
    }
}
