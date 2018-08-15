package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.command.CommandInterpreter;
import cz.tefek.botdiril.core.ServerPreferences;
import net.dv8tion.jda.core.entities.Message;

public final class CommandUsage implements Command
{
    @Override
    public String usage()
    {
        return "usage <command name>";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var command = CommandInterpreter.getCommandByAlias((String) params[0]);

        if (command == null)
        {
            message.getTextChannel().sendMessage("No such command. :sleeping:").submit();
        }
        else
        {
            StringBuilder sb = new StringBuilder("**Command:** ");
            sb.append(command.getAliases().get(0));
            sb.append("\n**Aliases:** ");
            command.getAliases().forEach(alias -> {
                sb.append(alias);
                if (command.getAliases().indexOf(alias) != command.getAliases().size() - 1)
                {
                    sb.append(", ");
                }
            });
            sb.append("\n**Usage:** ");
            sb.append(ServerPreferences.getServerByID(message.getGuild().getIdLong()).getPrefix());
            sb.append(command.usage());
            sb.append("\n**Description:** ");
            sb.append(command.description());

            message.getTextChannel().sendMessage(sb).submit();
        }
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("usage", "man");
    }

    @Override
    public String description()
    {
        return "Prints important information about a command.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GENERAL;
    }
}
