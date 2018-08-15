package cz.tefek.botdiril.command.s;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.random.RandomDataGenerator;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.card.ItemCard;
import cz.tefek.botdiril.userdata.items.crate.CrateDrops;
import net.dv8tion.jda.core.entities.Message;

public class CommandDrawCard implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("drawacard", "pickacard", "draw");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());

        var result = ui.useTimer("draw", 300_000);
        var random = new Random();

        if (result == -1)
        {

            var drops = new ArrayList<Item>();

            Item kek;

            var offs = 0.2;
            var add = 0.0;

            do
            {
                while (!((kek = CrateDrops.rollItem(75_000, new RandomDataGenerator())) instanceof ItemCard));

                drops.add(kek);

                add = Math.log(((ItemCard) kek).getSellValue()) / 20;

                offs += add;
            }
            while (random.nextDouble() > offs);

            message.getTextChannel().sendMessage("You drew " + drops.stream().map(ia -> ia.hasIcon() ? ia.getIcon() + " " + ia.getHumanName() : ia.getHumanName()).collect(Collectors.joining(", ")) + ".").submit();

            drops.forEach(ui::addItem);
        }
        else
        {
            result /= 1000;

            var m = result / 60;
            var s = result % 60;

            message.getTextChannel().sendMessage(String.format("You need to wait %d minutes %d seconds before drawing a card again!", m, s)).submit();
        }
    }

    @Override
    public String usage()
    {
        return "draw";
    }

    @Override
    public String description()
    {
        return "Draw a card!";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GAMBLING;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

}
