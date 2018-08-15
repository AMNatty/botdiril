package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandDupe implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("dupe", "duplicate", "replicate");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getMember().getUser().getIdLong());

        var i = Item.getByID((String) params[0]);
        var gem = Item.getByID("gemdiril");
        var gemamt = ui.howManyOf(gem);

        if (i == null)
        {
            message.getTextChannel().sendMessage("No such item.").submit();
            return;
        }

        if (gemamt == 0)
        {
            message.getTextChannel().sendMessage(String.format("You have no %ss.", Item.GEM)).submit();
            return;
        }

        if (gem.getID().equals(i.getID()))
        {
            message.getTextChannel().sendMessage(String.format("Hmmm is it useful to dupe %ss? Nope.", Item.GEM)).submit();
            return;
        }

        ui.addItem(gem, -1);
        ui.addItem(i, 1);
        var amt = ui.howManyOf(i);
        message.getTextChannel().sendMessage(String.format("You duplicated a %ss for a total of %d.", i.hasIcon() ? i.getIcon() : "", amt)).submit();
    }

    @Override
    public String usage()
    {
        return "dupe <item>";
    }

    @Override
    public String description()
    {
        return "Using one " + Item.GEM + " you can replicate a certain item.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.ITEMS;
    }
}
