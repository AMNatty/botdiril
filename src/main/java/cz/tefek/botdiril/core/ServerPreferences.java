package cz.tefek.botdiril.core;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import cz.tefek.botdiril.BotMain;
import cz.tefek.botdiril.command.ParseChannel;
import cz.tefek.botdiril.core.server.ServerConfig;
import cz.tefek.botdiril.persistent.Persistency;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
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

    public static void wipePreviousConfig(Guild g)
    {
        var scOld = getServerByID(g.getIdLong());

        var rc = scOld == null ? false : scOld.hasReportChannel();

        Persistency.deleteServer(g.getIdLong());

        if (!rc)
        {
            var tco = g.getController().createTextChannel("botdiril");

            var adminRoles = g.getRoles().stream().filter(r -> r.hasPermission(Permission.ADMINISTRATOR)).collect(Collectors.toList());

            for (var role : adminRoles)
            {
                tco = tco.addPermissionOverride(role, Permission.getRaw(EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)), 0);
            }

            tco = tco.addPermissionOverride(g.getPublicRole(), 0, Permission.getRaw(EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)));

            var tc = (TextChannel) tco.complete();

            if (guilds.remove(scOld))
            {
                tc.sendMessage("Hello once again! Before using any commands please initialize me via `botdiril <prefix>`. Don't worry, you can change the prefix later via `botdiril prefix <prefix>`.").complete();
            }
            else
            {
                tc.sendMessage("Hello! This is Botdiril! Before using any commands please initialize me via `botdiril <prefix>`. Don't worry, you can change the prefix later via `botdiril prefix <prefix>`.").complete();
            }

            var sc = new ServerConfig(g.getIdLong());
            sc.setReportChannel(tc.getIdLong());

            guilds.add(sc);
        }
        else
        {
            var sc = new ServerConfig(g.getIdLong());
            var tc = sc.getReportChannel(g);

            guilds.remove(scOld);

            tc.sendMessage("Hello! This is Botdiril! Before using any commands please initialize me via `botdiril <prefix>`. Don't worry, you can change the prefix later via `botdiril prefix <prefix>`.").complete();

            guilds.add(sc);
        }
    }

    public static ServerConfig getServerByID(long id)
    {
        var server = guilds.stream().filter(e -> e.getID() == id).findFirst();

        return server.isPresent() ? server.get() : null;
    }

    public static void addServer(Guild g)
    {
        long id = g.getIdLong();

        wipePreviousConfig(g);
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

    public static void setChannel(TextChannel channel, String chStr)
    {
        var g = channel.getGuild();
        var sc = getServerByID(g.getIdLong());

        var tc = ParseChannel.parse(channel, chStr);

        if (tc != null)
        {
            sc.setReportChannel(tc.getIdLong());
            Persistency.serializeServer(sc);
            channel.sendMessage("Print channel updated to " + tc.getAsMention() + "!").submit();
        }
    }
}
