package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import net.dv8tion.jda.core.entities.Message;

public class CommandEcho implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("echo");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var yr = Calendar.getInstance().get(Calendar.YEAR);
        var author = message.getAuthor().getAsMention();
        var msg = (String) params[0];
        msg = msg.replaceAll("@here", "(@)here");
        msg = msg.replaceAll("@everyone", "(@)everyone");

        message.getTextChannel().sendMessage(String.format("*%s* - %s %d", msg, author, yr)).queue();
        message.delete().submit();
    }

    @Override
    public String usage()
    {
        return "echo <message>";
    }

    @Override
    public String description()
    {
        return "Prints a message, good for testing.";
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.GENERAL;
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

}
