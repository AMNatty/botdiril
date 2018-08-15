package cz.tefek.botdiril.command.s.superuser;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CommandMagic extends SuperUserCommandBase
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { Member.class, long.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("magic");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var user = (Member) params[0];
        var coins = (long) params[1];

        if (message.getAuthor().getIdLong() == 198780988959096832L && user.getUser().getIdLong() == 198780988959096832L)
        {
            message.addReaction("🇳").queue(succ -> message.addReaction("🇴").submit());

            message.getTextChannel().sendMessage("<:usure:413343085892861962>").submit();
            return;
        }

        UserStorage.getByID(user.getUser().getIdLong()).addCoins(coins);

        if (coins < 0)
        {
            message.getTextChannel().sendMessage(String.format("You say a magic charm and voilà! %s has %d less %ss.", user.getEffectiveName(), -coins, Item.COINDIRIL)).submit();
            return;
        }

        message.getTextChannel().sendMessage(String.format("You say a magic charm and voilà! %s has %d more %ss.", user.getEffectiveName(), coins, Item.COINDIRIL)).submit();
    }

    @Override
    public String usage()
    {
        return "magic <member> <coins>";
    }

    @Override
    public String description()
    {
        return "Performs a happy little magic trick.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

}
