package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.AmountParser;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.crate.ItemCrate;
import cz.tefek.botdiril.userdata.items.crate.Loot;
import net.dv8tion.jda.core.entities.Message;

public class CommandCrate implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class, int.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("opencrate", "crate", "chest", "openchest", "openbox", "box", "open");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());
        Item chest = null;

        if (params.length > 0)
        {
            var par0 = (String) params[0];

            if (par0.equalsIgnoreCase("basic") || par0.equalsIgnoreCase("crate") || par0.equalsIgnoreCase("common"))
            {
                chest = Item.getByID("crate");
            }
            else if (par0.equalsIgnoreCase("uncommon") || par0.equalsIgnoreCase("uncommoncrate"))
            {
                chest = Item.getByID("uncommoncrate");
            }
            else if (par0.equalsIgnoreCase("golden") || par0.equalsIgnoreCase("goldencrate"))
            {
                chest = Item.getByID("goldencrate");
            }
            else if (par0.equalsIgnoreCase("epic") || par0.equalsIgnoreCase("epiccrate"))
            {
                chest = Item.getByID("epiccrate");
            }
            else if (par0.equalsIgnoreCase("legendary") || par0.equalsIgnoreCase("legendarycrate"))
            {
                chest = Item.getByID("legendarycrate");
            }

            if (chest == null)
            {
                message.getTextChannel().sendMessage("Unrecognized chest type.").submit();
                return;
            }
        }
        else
        {
            var crates = new String[] { "crate", "uncommoncrate", "goldencrate", "epiccrate", "legendarycrate" };

            for (String c : crates)
            {
                var cr = Item.getByID(c);

                if (!ui.hasItem(cr))
                {
                    chest = null;
                }
                else
                {
                    chest = cr;
                    break;
                }
            }

            if (chest == null)
            {
                message.getTextChannel().sendMessage("You have no crates to open. :(").submit();
                return;
            }
        }

        long userhas = ui.howManyOf(chest);

        if (userhas == 0)
        {
            message.getTextChannel().sendMessage("You have no " + chest.getIcon() + "s. :(").submit();
            return;
        }

        long amt = 1;

        if (params.length > 1)
        {
            amt = AmountParser.parse((String) params[1], message, userhas);
        }

        if (amt == Long.MIN_VALUE)
            return;

        if (amt > userhas)
        {
            message.getTextChannel().sendMessage("You can't open more " + chest.getIcon() + "s than you have.").submit();
            return;
        }

        if (amt > 64)
            message.getTextChannel().sendMessage("Note: You can only open 64 crates at once.").submit();

        amt = Math.min(64, amt);

        var loot = new Loot();
        long lootCoins = 0;
        var rdg = new RandomDataGenerator();

        var ic = (ItemCrate) chest;

        for (long i = 0; i < amt; i++)
        {
            lootCoins += ic.generateCoins(rdg);
            ui.addItemUnsafe(chest, -1);
        }

        ic.generateLoots(rdg, loot, amt);

        loot.sortedByItemValue();

        loot.forEach(c -> {
            ui.addItemUnsafe(c.getItem(), c.getAmount());
        });

        ui.addCoins(lootCoins);

        // Just in case
        ui.serialize();

        var sb = new StringBuilder();
        sb.append("**You open the crate(s) and find the following stuff (showing max 24 different items):**\n");
        sb.append(lootCoins);
        sb.append(" ");
        sb.append(Item.COINDIRIL);
        sb.append("s\n");

        loot.stream().limit(24).forEach(c -> {
            sb.append(c.getAmount());
            sb.append("x ");
            sb.append(c.getItem().getIcon());
            sb.append(c.getItem().getHumanName());
            sb.append("\n");
        });

        message.getTextChannel().sendMessage(sb.toString()).submit();
    }

    public static <T> T getRandomListEntry(List<T> list, Random rand)
    {
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public String usage()
    {
        return "crate [crate_type] [how_many]";
    }

    @Override
    public String description()
    {
        return "Open your crates for some sweet loot!";
    }

    @Override
    public boolean hasCustomParser()
    {
        return true;
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
