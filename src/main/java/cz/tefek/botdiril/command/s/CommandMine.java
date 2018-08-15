package cz.tefek.botdiril.command.s;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandMine implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("mine", "dig");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getMember().getUser().getIdLong());

        var t = ui.useTimer("mine", 120_000);

        var rand = new Random();

        int reward = rand.nextInt(10001);

        var kek = Item.getByID("kek");
        long keks = 0;

        var items = new ArrayList<Item>();
        long coins = 0;

        if (t == -1)
        {
            var crate = Item.getByID("crate");
            var uncrate = Item.getByID("uncommoncrate");

            if (reward > 9998)
            {
                coins += 1000;

                items.add(crate);
                items.add(crate);
                items.add(crate);
                items.add(uncrate);

                ui.addItem(kek, 512);
                message.getTextChannel().sendMessage("Holy $#&@, you found " + coins + " " + Item.COINDIRIL + "s, three " + crate.getIcon() + "s  and an " + uncrate.getIcon() + ". And some " + Item.KEK + "s of course (+" + keks + ").").submit();
            }
            else if (reward > 9990)
            {
                coins += 500;

                items.add(crate);
                items.add(crate);

                keks = 128;
                message.getTextChannel().sendMessage("Poggers! You found " + coins + " " + Item.COINDIRIL + "s and two " + crate.getIcon() + "s. And some " + Item.KEK + "s of course (+" + keks + ").").submit();
            }
            else if (reward > 9900)
            {
                coins += 200;

                items.add(crate);

                keks = 32;
                message.getTextChannel().sendMessage("Damn! You found " + coins + " " + Item.COINDIRIL + "s, one " + crate.getIcon() + " and " + keks + " " + Item.KEK + "s.").submit();
            }
            else if (reward > 9000)
            {
                coins += 50;

                keks = 16;
                message.getTextChannel().sendMessage("Nice! You found " + coins + " " + Item.COINDIRIL + "s and " + keks + " " + Item.KEK + "s.").submit();
            }
            else if (reward > 8000)
            {
                coins += 40;

                keks = 8;
                message.getTextChannel().sendMessage("Not bad. You found " + coins + " " + Item.COINDIRIL + "s and " + keks + " " + Item.KEK + "s.").submit();
            }
            else if (reward > 500)
            {
                coins += 20;

                keks = 4;
                message.getTextChannel().sendMessage("You found " + coins + " " + Item.COINDIRIL + "s and " + keks + " " + Item.KEK + "s.").submit();
            }
            else
            {
                keks = 10;
                message.getTextChannel().sendMessage("You found 10 " + Item.KEK + "s.").submit();
            }
        }
        else
        {
            t /= 1000;
            message.getTextChannel().sendMessage("You still need to wait " + (t % 3_600 / 60) + " minutes and " + (t % 60) + " seconds to mine.").submit();
        }

        ui.addCoins(coins);
        ui.addItem(kek, keks);
        items.forEach(ui::addItem);
    }

    @Override
    public String usage()
    {
        return "mine";
    }

    @Override
    public String description()
    {
        return "Mine for some Ethereum kappa";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GAMBLING;
    }
}
