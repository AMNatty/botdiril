package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandLuckyStreak implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("luckystrike", "ls");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getMember().getUser().getIdLong());

        var prefix = ServerPreferences.getServerByID(message.getGuild().getIdLong()).getPrefix();

        var t = ui.checkTimer("strike");
        var keks = Item.getByID("kek");
        var kekamt = ui.howManyOf(keks);

        if (kekamt == 0)
        {
            message.getTextChannel().sendMessage(String.format("You have no %ss, get some in the store.", Item.KEK)).submit();
        }

        if (t == -1)
        {
            if (params.length == 0)
            {
                message.getTextChannel().sendMessage(String.format("You can now double your %s! (50%% chance to lose instead) Type `%sls confirm` to attempt to double your coins. (1 hour cooldown)", Item.KEK, prefix)).submit();
            }
            else if (((String) params[0]).equalsIgnoreCase("confirm"))
            {
                ui.useTimer("strike", 60 * 60 * 1000);

                boolean reward = new Random().nextBoolean();

                if (!reward)
                {
                    message.getTextChannel().sendMessage(String.format("%s. You lost everything! -%d%ss.", Item.KEK, kekamt, Item.KEK)).submit();
                    ui.addItem(keks, -kekamt);
                }
                else
                {
                    message.getTextChannel().sendMessage(String.format("You won! +%d%ss.", kekamt, Item.KEK)).submit();
                    ui.addItem(keks, kekamt);
                }
            }
        }
        else
        {
            t /= 1000;

            final int sph = 60 * 60;
            final int spm = 60;

            int thours = (int) (t / sph);
            int tminutes = (int) (t / 60 % spm);
            int tseconds = (int) (t % 60);

            message.getTextChannel().sendMessage(String.format("You still need to wait %d hours %d minutes %d seconds to use lucky strike.", thours, tminutes, tseconds)).submit();
        }
    }

    @Override
    public String usage()
    {
        return "ls [confirm]";
    }

    @Override
    public String description()
    {
        return "Another gambling command, you can either go double or go bankrupt.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.ECONOMY;
    }
}
