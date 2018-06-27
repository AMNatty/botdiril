package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.ItemPair;
import net.dv8tion.jda.core.entities.Message;

public final class CommandSell implements Command
{
    @Override
    public String usage()
    {
        return "sell <item> [amount]";
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

                if (amt < 1)
                {
                    channel.sendMessage("You can't sell this many items.").submit();
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
            if (amt > 1)
            {
                if (ui.sellItems(item, item.getSellValue(), amt))
                {
                    ui.setUndo(new ItemPair(item, -amt));
                    channel.sendMessage("You succesfully sold " + amt + " " + item.getHumanName() + "s for " + (item.getSellValue() * amt) + Item.COINDIRIL + "s.").submit();
                }
                else
                {
                    channel.sendMessage("You don't have this many items. :frowning:").submit();
                }

            }
            else if (ui.sellItem(item, item.getSellValue()))
            {
                ui.setUndo(new ItemPair(item, -1));
                channel.sendMessage("You succesfully sold a " + item.getHumanName() + " for " + item.getSellValue() + Item.COINDIRIL + "s.").submit();
            }
            else
            {
                channel.sendMessage("You don't have this many items. :frowning:").submit();
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
        return Arrays.asList("sell");
    }

    @Override
    public boolean hasCustomParser()
    {
        return true;
    }

    @Override
    public String description()
    {
        return "You can sell some stuff.";
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
