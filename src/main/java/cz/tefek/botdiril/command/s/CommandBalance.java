package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public final class CommandBalance implements Command
{
    @Override
    public String usage()
    {
        return "balance";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        message.getTextChannel().sendMessage(message.getAuthor().getAsMention() + ", you have " + UserStorage.getByID(message.getAuthor().getIdLong()).getCoins() + " " + Item.COINDIRIL + "s.").submit();
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("balance", "money", "coins", "coindirils", "howpoorami", "cash");
    }

    @Override
    public String description()
    {
        return "Shows your current " + Item.COINDIRIL + " balance.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.ECONOMY;
    }
}
