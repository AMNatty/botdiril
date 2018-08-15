package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import net.dv8tion.jda.core.entities.Message;

public class CommandDisenchant implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("disenchant", "dis");
    }

    @Override
    public void interpret(Message message, Object... params)
    {

    }

    @Override
    public String usage()
    {
        return "disenchant";
    }

    @Override
    public String description()
    {
        return "Disenchant your cards into dust.";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.ITEMS;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }
}
