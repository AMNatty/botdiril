package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.entities.Message;

public class CommandNowPlaying implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("nowplaying", "np");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        ActiveChannelManager.nowPlaying(message.getTextChannel());
    }

    @Override
    public String usage()
    {
        return "nowplaying";
    }

    @Override
    public String description()
    {
        return "Prints info about the currently playing audio track.";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.MUSIC;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }
}
