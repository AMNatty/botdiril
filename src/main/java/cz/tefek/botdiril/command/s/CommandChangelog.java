package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class CommandChangelog implements Command
{
    private static final MessageEmbed changelog;

    static
    {
        var eb = new EmbedBuilder();
        eb.setTitle("Changelog");
        eb.setAuthor("Botdiril");
        eb.setDescription("Version 233");
        eb.setColor(0x0099ff);

        eb.addField("Important stuff!", "More fixes and a buff for `-draw`.", false);

        changelog = eb.build();
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("changelog");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        message.getTextChannel().sendMessage(changelog).submit();
    }

    @Override
    public String usage()
    {
        return "changelog";
    }

    @Override
    public String description()
    {
        return "Shows the latest changes.";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GENERAL;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

}
