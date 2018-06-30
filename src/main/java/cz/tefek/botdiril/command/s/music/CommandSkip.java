package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.entities.Message;

public class CommandSkip implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("skip");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var g = message.getGuild();
        var tc = message.getTextChannel();
        var vcs = g.getMember(message.getAuthor()).getVoiceState();

        if (vcs.inVoiceChannel())
        {
            var vc = vcs.getChannel();

            ActiveChannelManager.skip(g, tc, vc);
        }
        else
        {
            tc.sendMessage("You are not in a voice channel to use music commands.").submit();
        }
    }

    @Override
    public String usage()
    {
        return "skip";
    }

    @Override
    public String description()
    {
        return "Skips the currently playing audio track.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.MUSIC;
    }
}
