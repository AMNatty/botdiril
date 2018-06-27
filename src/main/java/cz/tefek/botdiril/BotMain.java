package cz.tefek.botdiril;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

import cz.tefek.botdiril.command.CommandInitializer;
import cz.tefek.botdiril.core.Botdiril;
import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;

public class BotMain
{
    private static Botdiril botdiril;

    public static String IP;
    public static int PORT = 49302;

    public static void main(String[] args) throws LoginException, InterruptedException, IOException
    {
        if (!BotdirilConfig.load())
        {
            System.err.println("ERROR WHILE LOADING CONFIG. ABORTING.");
            return;
        }

        var local = Arrays.stream(args).filter(c -> c.equalsIgnoreCase("local")).findAny().isPresent();

        backup();

        if (!local)
        {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            IP = in.readLine();
            in.close();
        }
        else
        {
            IP = InetAddress.getLocalHost().getHostAddress();
        }

        System.out.println("Running at " + IP + ":" + PORT);

        Item.loadFirst();
        UserStorage.deserializeAll();
        ServerPreferences.initialize();
        CommandInitializer.initialize();

        botdiril = new Botdiril();
        botdiril.build();
    }

    private static void backup() throws IOException
    {
        var bf = new File("backup/" + System.currentTimeMillis());
        bf.mkdirs();

        var ud = new File("user");

        if (ud.isDirectory())
        {
            for (File f : ud.listFiles())
            {
                var fos = new FileOutputStream(new File(bf, f.getName()));
                var fis = new FileInputStream(f);
                fos.write(fis.readAllBytes());
                fis.close();
                fos.close();
            }
        }
    }

    public static Botdiril getBotdiril()
    {
        return botdiril;
    }
}
