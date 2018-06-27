package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CommandDonate implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { Member.class, long.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("give", "donate", "share");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var rcv = (Member) params[0];
        long amt = (long) params[1];

        if (message.getMember().getUser().getIdLong() == rcv.getUser().getIdLong())
        {
            message.getTextChannel().sendMessage("Nice try! You gave yourself your own " + Item.COINDIRIL + "s. :thinking:").submit();
        }

        var ui = UserStorage.getByID(message.getMember().getUser().getIdLong());
        var uircv = UserStorage.getByID(rcv.getUser().getIdLong());

        if (amt < 1)
        {
            message.getTextChannel().sendMessage("You can't give negative or zero " + Item.COINDIRIL + "s.").submit();
            return;
        }

        if (amt > ui.getCoins())
        {
            message.getTextChannel().sendMessage("You don't have enough " + Item.COINDIRIL + "s for that.").submit();
        }
        else
        {
            message.getTextChannel().sendMessage("You gave " + rcv.getEffectiveName() + " " + amt + " " + Item.COINDIRIL + "s.").submit();

            ui.addCoins(-amt);
            uircv.addCoins(amt);
        }
    }

    @Override
    public String usage()
    {
        return "give <user> <coins>";
    }

    @Override
    public String description()
    {
        return "Give all your coins to someone who gambled it all";
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
