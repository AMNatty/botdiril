package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandDaily implements Command
{
    private static final int DAILY_COINS = 200;

    @Override
    public String usage()
    {
        return "daily";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());

        var result = ui.useTimer("daily", 79_200_000);

        if (result == -1)
        {
            var coins = DAILY_COINS + DAILY_COINS * ui.incrAndGetProperty("daily") / 5;
            var keks = coins / 2;
            var kek = Item.getByID("kek");
            ui.addCoins(coins);
            ui.addItem(kek, keks);
            message.getTextChannel().sendMessage("Here is your daily stuff. Today you got " + coins + " " + Item.COINDIRIL + "s and " + keks + " " + Item.KEK + "s").submit();
        }
        else
        {
            result /= 1000;

            var h = result / 3600;
            var m = result / 60 % 60;
            var s = result % 60;

            message.getTextChannel().sendMessage(String.format("You need to wait %d hours %d minutes %d seconds before picking up your daily " + Item.COINDIRIL + "s!", h, m, s)).submit();
        }
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("daily");
    }

    @Override
    public String description()
    {
        return "Get your daily " + Item.COINDIRIL + "s!";
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
