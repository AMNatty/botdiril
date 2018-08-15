package cz.tefek.botdiril;

import java.io.BufferedReader;
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
import cz.tefek.botdiril.core.server.ChannelCache;
import cz.tefek.botdiril.sql.DB;
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

        DB.open();

        try
        {
            DB.init();
            ChannelCache.syncFromSQL();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        var local = Arrays.stream(args).filter(c -> c.equalsIgnoreCase("local")).findAny().isPresent();

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
        ServerPreferences.initialize();
        CommandInitializer.initialize();

        botdiril = new Botdiril();
        botdiril.build();
    }

    public static Botdiril getBotdiril()
    {
        return botdiril;
    }
}
