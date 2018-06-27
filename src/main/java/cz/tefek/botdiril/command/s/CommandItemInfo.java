package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandItemInfo implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("iteminfo", "ii");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var item = Item.getByID((String) params[0]);

        if (item == null)
        {
            message.getTextChannel().sendMessage("No such item.").submit();
        }
        else
        {
            var eb = new EmbedBuilder();
            eb.setTitle(item.getHumanName());

            if (item.hasIcon())
            {
                var m = Pattern.compile("[0-9]+").matcher(item.getIcon());

                if (m.find())
                    eb.setThumbnail(message.getJDA().getEmoteById(m.group()).getImageUrl());
            }

            eb.setDescription(item.getDescription());

            if (item.canBeBought())
                eb.addField("Buys for:", item.getBuyValue() + Item.COINDIRIL + "s", true);
            else
                eb.addField("Buys for:", "Cannot be bought", true);

            eb.setColor(0xff00ff);
            eb.addField("Sells for:", item.getSellValue() + Item.COINDIRIL + "s", true);
            eb.addField("ID:", item.getID(), true);

            message.getTextChannel().sendMessage(eb.build()).submit();
        }
    }

    @Override
    public String usage()
    {
        return "iteminfo <item_id>";
    }

    @Override
    public String description()
    {
        return "Prints info about an item.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.INVENTORY;
    }
}
