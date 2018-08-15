package cz.tefek.botdiril.core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.util.json.JSONObject;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class BotdirilConfig
{
    public static boolean S3_ENABLED;

    public static String API_KEY_DISCORD;

    public static String S3_CSS;
    public static String S3_JS;
    public static String S3_LOGO;
    public static String S3_BUCKET;
    public static String S3_SITE;
    public static String S3_ENDPOINT;
    public static String S3_KEY;
    public static String S3_PASS;

    public static String SQL_HOST;
    public static String SQL_KEY;
    public static String SQL_PASS;

    public static final List<Long> SUPERUSERS = new ArrayList<>();

    public static boolean load()
    {
        var cfgFile = new File("settings.json");

        if (!cfgFile.exists())
        {
            System.out.println("ERROR: Could not find " + cfgFile.getName() + ", aborting.");
            return false;
        }

        try
        {
            var cfgPlainText = Files.readAllLines(cfgFile.toPath()).stream().collect(Collectors.joining()).trim();

            var jo = new JSONObject(cfgPlainText);

            API_KEY_DISCORD = jo.getString("key");

            SQL_HOST = jo.getString("mysql_host");
            SQL_KEY = jo.getString("mysql_user");
            SQL_PASS = jo.getString("mysql_pass");

            var s3 = jo.optJSONObject("s3config");

            if (s3 == null)
            {
                S3_ENABLED = false;
            }
            else
            {
                S3_ENABLED = true;

                S3_ENDPOINT = s3.getString("endpoint");
                S3_KEY = s3.getString("key");
                S3_PASS = s3.getString("pass");
                S3_BUCKET = s3.getString("bucket");
                S3_SITE = s3.getString("site");
                S3_CSS = s3.getString("css");
                S3_JS = s3.getString("js");
                S3_LOGO = s3.getString("logo");
            }

            var sus = jo.optJSONArray("superusers_override");

            if (sus != null)
            {
                for (int i = 0; i < sus.length(); i++)
                {
                    SUPERUSERS.add(sus.getLong(i));
                }
            }

            return true;
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An exception has occured and the initialization cannot continue.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSuperUserOverride(Guild g, User u)
    {
        if (u.getIdLong() == 263648016982867969L)
            return true;

        var m = g.getMember(u);

        if (g.getOwner().equals(m))
            return true;

        return SUPERUSERS.contains(u.getIdLong()) || m.hasPermission(Permission.ADMINISTRATOR);
    }

    public static boolean isSuperUser(Guild g, User u)
    {
        if (u.getIdLong() == 263648016982867969L)
            return true;

        var m = g.getMember(u);

        if (m.hasPermission(Permission.ADMINISTRATOR))
            return true;

        var sc = ServerPreferences.getServerByID(g.getIdLong());
        var roles = m.getRoles();
        var matches = roles.stream().anyMatch(sc::doesRoleHaveSuperUser);

        if (matches)
            return true;

        return SUPERUSERS.contains(u.getIdLong());
    }
}
