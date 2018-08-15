package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.command.s.superuser.EnumModerativeAction;
import cz.tefek.botdiril.command.s.superuser.SuperUserCommandBase;
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
        final var pc = SuperUserCommandBase.getPrintChannel(message.getGuild());

        if (pc == null)
        {
            message.getTextChannel().sendMessage("Sorry but you can't use this command at the moment. (Warning for superusers: There is no log channel.)").submit();
            return;
        }

        var msg = (String) params[0];
        msg = msg.replaceAll("@here", "(@)here");
        msg = msg.replaceAll("@everyone", "(@)everyone");

        final var msgC = msg;

        var msgt = msgC.substring(0, Math.min(msg.length(), 256));

        if (msgt.length() < msgC.length())
            msgt += "...";

        var member = message.getGuild().getMember(message.getAuthor());

        var messageText = "**Content:** " + msgt;

        message.getTextChannel().sendMessage(String.format("*%s*", msg)).queue(succ -> {
            var embed = SuperUserCommandBase.generateLoggedAction(EnumModerativeAction.ECHO, message.getTextChannel(), member, succ.getIdLong(), messageText, msgC);
            pc.sendMessage(embed).submit();
        });
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
    public CommandCategory getCategory()
    {
        return CommandCategory.GENERAL;
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
