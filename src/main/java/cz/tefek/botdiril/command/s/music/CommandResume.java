package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.entities.Message;

public class CommandResume implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("resume", "unpause");
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

            ActiveChannelManager.resume(tc, vc);
        }
        else
        {
            tc.sendMessage("You are not in a voice channel to resume music");
        }
    }

    @Override
    public String usage()
    {
        return "resume";
    }

    @Override
    public String description()
    {
        return "Resumes the currently paused audio track.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.MUSIC;
    }
}
