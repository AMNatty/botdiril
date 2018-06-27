package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandUndo implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("undo");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());
        var tc = message.getTextChannel();

        if (!ui.canUndo())
        {
            tc.sendMessage("You can't undo right now.").submit();
            return;
        }

        var item = ui.getUndo().getItem();
        var amt = ui.getUndo().getAmount();

        if (amt < 0)
        {
            amt = -amt;

            var coins = item.getSellValue() * amt;

            ui.addItem(item, amt);
            ui.addCoins(-coins);

            if (item.hasIcon())
            {
                tc.sendMessage(String.format("You get your %d %s(s) back, but I will take the %d %ss you got for it.", amt, item.getIcon(), coins, Item.COINDIRIL)).submit();
            }
            else
            {
                tc.sendMessage(String.format("You get your %d %s(s) back, but I will take the %d %ss you got for it.", amt, item.getHumanName(), coins, Item.COINDIRIL)).submit();
            }
        }
        else
        {
            var coins = item.getBuyValue() * amt;
            ui.addItem(item, -amt);
            ui.addCoins(coins);

            if (item.hasIcon())
            {
                tc.sendMessage(String.format("You get your %d %ss back, but I will take the %d %s(s) you bought.", coins, Item.COINDIRIL, amt, item.getIcon())).submit();
            }
            else
            {
                tc.sendMessage(String.format("You get your %d %ss back, but I will take the %d %s(s) you bought.", coins, Item.COINDIRIL, amt, item.getHumanName())).submit();
            }
        }
    }

    @Override
    public String usage()
    {
        return "undo";
    }

    @Override
    public String description()
    {
        return "Refund your last purchase.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.INVENTORY;
    }
}
