package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.entities.Message;

public class CommandPlay implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("play", "p");
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

            ActiveChannelManager.search(tc, vc, (String) params[0]);
        }
        else
        {
            tc.sendMessage("You are not in a voice channel to play music").submit();
        }
    }

    @Override
    public String usage()
    {
        return "play <what to play>";
    }

    @Override
    public String description()
    {
        return "Queues an audio track";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public boolean hasOpenEnd()
    {
        return true;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.MUSIC;
    }
}
