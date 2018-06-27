package cz.tefek.botdiril.command.s;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.command.CommandInterpreter;
import cz.tefek.botdiril.core.ServerPreferences;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public final class CommandHelp implements Command
{
    @Override
    public String usage()
    {
        return "help [command]";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var prefix = ServerPreferences.getServerByID(message.getGuild().getIdLong()).getPrefix();

        if (params.length == 1)
        {
            var cath = (String) params[0];

            var command = CommandInterpreter.getCommandByAlias(cath);

            if (command != null)
            {
                CommandInterpreter.getCommandByAlias("usage").interpret(message, cath);
            }
            else
            {
                var found = Arrays.stream(CommandCathegory.values()).filter(c -> c.toString().equalsIgnoreCase(cath)).findAny().orElse(null);

                if (found != null)
                {
                    var eb = new EmbedBuilder();
                    eb.setColor(Color.CYAN.getRGB());
                    eb.setTitle("Help for the " + found.getName());

                    CommandInterpreter.commands.stream().filter(c -> c.getCathegory() == found).forEach(comm -> {
                        eb.addField(new Field(prefix + comm.usage(), comm.description(), false));
                    });

                    eb.setDescription("Type ``" + prefix + "usage <command>`` to show more information for each command.");

                    message.getTextChannel().sendMessage(new MessageBuilder(eb).build()).submit();
                }
                else
                {
                    message.getTextChannel().sendMessage("Could not find help for that cathegory.").submit();
                }
            }
        }
        else
        {
            var eb = new EmbedBuilder();
            eb.setColor(Color.CYAN.getRGB());
            eb.setTitle("Stuck? Here is your help:");

            Arrays.stream(CommandCathegory.values()).forEach(cat -> {
                eb.addField(cat.getName(), "Type ``" + prefix + "help " + cat.toString().toLowerCase() + "``", false);
            });

            eb.setDescription("There are " + CommandInterpreter.commands.size() + " commands in " + CommandCathegory.values().length + " cathegories total.");

            message.getTextChannel().sendMessage(new MessageBuilder(eb).build()).submit();
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
        return Arrays.asList("help");
    }

    @Override
    public String description()
    {
        return "Displays info for a specific command. The `[command]` parameter is optional. Use `help` without paramters to get the list of all commands.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.GENERAL;
    }
}
