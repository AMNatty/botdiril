package cz.tefek.botdiril.command.s;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.command.CommandInterpreter;
import cz.tefek.botdiril.core.BotdirilConfig;
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
        var user = message.getAuthor();
        var guild = message.getGuild();
        var prefix = ServerPreferences.getServerByID(guild.getIdLong()).getPrefix();

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
                var found = Arrays.stream(CommandCategory.values()).filter(c -> c.toString().equalsIgnoreCase(cath)).findAny().orElse(null);

                if (found != null)
                {
                    var eb = new EmbedBuilder();
                    eb.setColor(Color.CYAN.getRGB());
                    eb.setTitle("Help for the " + found.getName());

                    CommandInterpreter.commands.stream().filter(c -> c.getCategory() == found).forEach(comm -> {
                        eb.addField(new Field(prefix + comm.usage(), comm.description(), false));
                    });

                    eb.setDescription("Type ``" + prefix + "usage <command>`` to show more information for each command.");

                    message.getTextChannel().sendMessage(new MessageBuilder(eb).build()).submit();
                }
                else
                {
                    message.getTextChannel().sendMessage("Could not find help for that category.").submit();
                }
            }
        }
        else
        {
            var eb = new EmbedBuilder();
            eb.setColor(Color.CYAN.getRGB());
            eb.setTitle("Stuck? Here is your help:");

            Arrays.stream(CommandCategory.values()).forEach(cat -> {
                eb.addField(cat.getName(), "Type ``" + prefix + "help " + cat.toString().toLowerCase() + "``", false);
            });

            long cmdCnt;
            int catCnt = CommandCategory.values().length;

            if (BotdirilConfig.isSuperUser(guild, user))
            {
                cmdCnt = CommandInterpreter.commands.size();
            }
            else
            {
                cmdCnt = CommandInterpreter.commands.stream().filter(c -> c.getCategory() != CommandCategory.SUPERUSER).count();
                catCnt -= 1;
            }

            eb.setDescription("There are " + cmdCnt + " commands in " + catCnt + " categories total.");

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
    public CommandCategory getCategory()
    {
        return CommandCategory.GENERAL;
    }
}
