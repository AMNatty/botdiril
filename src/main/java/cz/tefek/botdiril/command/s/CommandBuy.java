package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.ItemPair;
import net.dv8tion.jda.core.entities.Message;

public final class CommandBuy implements Command
{
    @Override
    public String usage()
    {
        return "buy <item> [amount]";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var channel = message.getTextChannel();

        if (params.length == 0)
        {
            channel.sendMessage("Please specify an item.").submit();
        }

        long amt = 1;

        if (params.length > 1)
        {
            try
            {
                amt = Integer.parseInt((String) params[1]);

                if (amt < 1 || amt > Long.MAX_VALUE)
                {
                    channel.sendMessage("You can't buy this many items.").submit();
                    return;
                }
            }
            catch (Exception e)
            {
                channel.sendMessage("You specified an invalid amount.").submit();
                return;
            }
        }

        var item = Item.getByID((String) params[0]);
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());

        if (item != null)
        {
            if (item.canBeBought())
            {
                if (amt > 1)
                {
                    if (ui.buyItems(item, item.getBuyValue(), amt))
                    {
                        ui.setUndo(new ItemPair(item, amt));
                        channel.sendMessage("You succesfully bought " + amt + " " + item.getHumanName() + "s for " + (item.getBuyValue() * amt) + Item.COINDIRIL + "s.").submit();
                    }
                    else
                    {
                        channel.sendMessage("You don't have enough money for this. :frowning:").submit();
                    }
                }
                else if (ui.buyItem(item, item.getBuyValue()))
                {
                    ui.setUndo(new ItemPair(item, 1));
                    channel.sendMessage("You succesfully bought a " + item.getHumanName() + " for " + item.getBuyValue() + Item.COINDIRIL + "s.").submit();
                }
                else
                {
                    channel.sendMessage("You don't have enough money for this. :frowning:").submit();
                }
            }
            else
            {
                channel.sendMessage("That item cannot be bought, sorry. :neutral_face:").submit();
            }
        }
        else
        {
            channel.sendMessage("No such item.").submit();
        }
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class, long.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("buy");
    }

    @Override
    public boolean hasCustomParser()
    {
        return true;
    }

    @Override
    public String description()
    {
        return "You can buy some stuff.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.ECONOMY;
    }
}
