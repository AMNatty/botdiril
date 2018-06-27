package cz.tefek.botdiril.core;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.amazonaws.util.json.JSONObject;

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

            var s3 = jo.optJSONObject("s3config");

            if (s3 == null)
            {
                S3_ENABLED = false;
                return true;
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
                return true;
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An exception has occured and the initialization cannot continue.");
            e.printStackTrace();
            return false;
        }
    }
}
