package cz.tefek.botdiril.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import cz.tefek.botdiril.BotMain;
import cz.tefek.botdiril.core.server.ServerConfig;
import cz.tefek.botdiril.persistent.Persistency;
import net.dv8tion.jda.core.entities.TextChannel;

public class ServerPreferences
{
    private static ConcurrentLinkedQueue<ServerConfig> guilds = new ConcurrentLinkedQueue<>();

    public static void initialize()
    {
        var servers = Persistency.guildsFromFiles();
        guilds.addAll(servers);
        System.out.printf("%d guilds loaded.\n", servers.size());
    }

    public static void wipePreviousConfig(TextChannel textChannel)
    {
        long id = textChannel.getGuild().getIdLong();

        Persistency.deleteServer(id);
        if (guilds.remove(getServerByID(id)))
        {
            textChannel.sendMessage("Hello once again! Before using any commands please initialize me via `botdiril <prefix>`. Don't worry, you can change the prefix later via `botdiril prefix <prefix>`.").complete();
        }
        else
        {
            textChannel.sendMessage("Hello! This is Botdiril! Before using any commands please initialize me via `botdiril <prefix>`. Don't worry, you can change the prefix later via `botdiril prefix <prefix>`.").complete();
        }

        guilds.add(new ServerConfig(id));
    }

    public static ServerConfig getServerByID(long id)
    {
        var server = guilds.stream().filter(e -> e.getID() == id).findFirst();

        return server.isPresent() ? server.get() : null;
    }

    public static void addServer(TextChannel textChannel)
    {
        long id = textChannel.getGuild().getIdLong();

        wipePreviousConfig(textChannel);
        Persistency.serializeServer(getServerByID(id));
        System.out.printf("Joined guild: %d\n", id);
    }

    public static void tryInstallServer(TextChannel channel, String prefix)
    {
        var guild = getServerByID(channel.getGuild().getIdLong());

        if (guild == null)
        {
            channel.sendMessage("A server-side error has occured, please contact the developer.").complete();
            return;
        }

        if (guild.isInstalled())
        {
        }
        else
        {
            fixPrefix_internal(channel, prefix);

            channel.sendMessage("Botdiril has been initialized with the following prefix: " + prefix).complete();
        }
    }

    private static void fixPrefix_internal(TextChannel channel, String prefix)
    {
        var guildConfig = getServerByID(channel.getGuild().getIdLong());

        if (!prefix.isEmpty())
        {
            if (prefix.length() < 16)
            {
                guildConfig.updatePrefix(prefix);
                Persistency.serializeServer(guildConfig);
                channel.getGuild().getController().setNickname(channel.getGuild().getMemberById(BotMain.getBotdiril().getJDA().getSelfUser().getIdLong()), "[" + prefix + "] Botdiril").submit();
            }
            else
            {
                channel.sendMessage("Prefix longer than 16 characters is not allowed.").complete();
            }
        }
        else
        {
            channel.sendMessage("Empty prefix is not allowed.").complete();
        }
    }

    public static void fixPrefix(TextChannel channel, String prefix)
    {
        fixPrefix_internal(channel, prefix);

        channel.sendMessage("The following prefix has been set: " + prefix).complete();
    }
}
