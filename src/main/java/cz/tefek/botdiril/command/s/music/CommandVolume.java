package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.persistent.Persistency;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

public class CommandVolume implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { int.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("volume", "vol");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var g = message.getGuild();

        var vol = (int) params[0];

        if (!g.getMember(message.getAuthor()).hasPermission(Permission.MANAGE_CHANNEL))
        {
            message.getTextChannel().sendMessage("You need to have the Manage Channel permission to change volume.").submit();
            return;
        }

        if (vol < 0 || vol > 100)
        {
            message.getTextChannel().sendMessage("Invalid volume value.").submit();
            return;
        }

        var sc = ServerPreferences.getServerByID(g.getIdLong());
        sc.setVolume(vol);
        Persistency.serializeServer(sc);

        ActiveChannelManager.setVolume(g, message.getTextChannel(), vol);
    }

    @Override
    public String usage()
    {
        return "volume <0-100>";
    }

    @Override
    public String description()
    {
        return "Sets the volume for this server.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.MUSIC;
    }
}
