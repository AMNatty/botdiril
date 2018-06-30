package cz.tefek.botdiril.command.s.music;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import cz.tefek.botdiril.voice.music.AudioQueueManager;
import net.dv8tion.jda.core.entities.Message;

public class CommandQueue implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("queue", "musicqueue", "aq", "mq", "q");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var tc = message.getTextChannel();

        ActiveChannelManager.queue(tc, 1);
    }

    @Override
    public String usage()
    {
        return "queue";
    }

    @Override
    public String description()
    {
        return "Shows the upcoming " + AudioQueueManager.PAGE_SIZE + " audio tracks in the queue.";
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.MUSIC;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }
}
