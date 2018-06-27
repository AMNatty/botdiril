package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.crate.Loot;
import net.dv8tion.jda.core.entities.Message;

public class CommandPayoutKeks implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("payout", "payoutkeks");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());
        var kek = Item.getByID("kek");
        var keks = ui.howManyOf(kek);

        var common = Item.getByID("commoncrate");
        var uncommon = Item.getByID("uncommoncrate");
        var epic = Item.getByID("epiccrate");
        var legendary = Item.getByID("legendarycrate");

        var pokeks = 0L;
        var extrakeks = 0L;
        var loot = new Loot();

        pokeks += keks * 19 / 10;

        if (keks == 0)
        {
            message.getTextChannel().sendMessage("You have no " + Item.KEK + "s.").submit();
            return;
        }

        if (keks < 500)
        {
            extrakeks = 0;
        }
        else if (keks < 3_000)
        {
            extrakeks = 300;
        }
        else if (keks < 10_000)
        {
            extrakeks = 1500;
        }
        else if (keks < 25_000)
        {
            extrakeks = 2000;
            loot.incrementItem(common, 1);
        }
        else if (keks < 50_000)
        {
            extrakeks = 5000;
            loot.incrementItem(common, 3);
        }
        else if (keks < 100_000)
        {
            extrakeks = 8000;
            loot.incrementItem(uncommon, 1);
        }
        else if (keks < 260_000)
        {
            loot.incrementItem(uncommon, 2);
            loot.incrementItem(common, 3);
        }
        else if (keks < 530_000)
        {
            loot.incrementItem(epic, 1);
        }
        else if (keks < 800_000)
        {
            extrakeks = 20000;
            loot.incrementItem(epic, 2);
        }
        else if (keks < 1_250_000)
        {
            extrakeks = 40000;
            loot.incrementItem(epic, 3);
        }
        else if (keks < 2_500_000)
        {
            loot.incrementItem(legendary);
        }
        else if (keks < 5_000_000)
        {
            extrakeks = 20000;
            loot.incrementItem(legendary, 2);
        }
        else if (keks < 10_000_000)
        {
            loot.incrementItem(legendary, 2);
            loot.incrementItem(epic, 6);
            loot.incrementItem(uncommon, 18);
            loot.incrementItem(common, 54);
        }
        else if (keks < 20_000_000)
        {
            loot.incrementItem(legendary, 4);
            loot.incrementItem(epic, 12);
            loot.incrementItem(uncommon, 36);
            loot.incrementItem(common, 108);
        }
        else if (keks < 40_000_000)
        {
            extrakeks = 800_000;
            loot.incrementItem(legendary, 8);
            loot.incrementItem(epic, 24);
            loot.incrementItem(uncommon, 72);
            loot.incrementItem(common, 100);
        }
        else if (keks < 80_000_000)
        {
            loot.incrementItem(legendary, 20);
            loot.incrementItem(epic, 40);
            loot.incrementItem(uncommon, 120);
        }
        else if (keks < 160_000_000)
        {
            loot.incrementItem(legendary, 40);
            loot.incrementItem(epic, 80);
            loot.incrementItem(uncommon, 240);
        }
        else if (keks < 320_000_000)
        {
            loot.incrementItem(legendary, 128);
        }
        else
        {
            loot.incrementItem(legendary, keks / legendary.getBuyValue());
        }

        ui.addCoins(pokeks + extrakeks);
        ui.addItem(kek, -keks);

        var sb = new StringBuilder();

        loot.forEach(c -> {
            ui.addItem(c.getItem(), c.getAmount());
            sb.append(c.getAmount() + "x " + c.getItem().getIcon());
            sb.append("\n");
        });

        message.getTextChannel().sendMessage(String.format("You cashed out %d %ss for %d %ss and the following items:\n%s", keks, Item.KEK, pokeks + extrakeks, Item.COINDIRIL, sb.toString().trim())).submit();
    }

    @Override
    public String usage()
    {
        return "payout";
    }

    @Override
    public String description()
    {
        return "Pay out all your " + Item.KEK + "s for some cool stuff! Note that this command consumes all your " + Item.KEK + "s.";
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
