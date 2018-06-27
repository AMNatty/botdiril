package cz.tefek.botdiril.command.s;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.card.ItemCard;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public final class CommandInventory implements Command
{
    @Override
    public String usage()
    {
        return "inventory [player]";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var channel = message.getTextChannel();
        var pid = message.getGuild().getMember(message.getAuthor());

        if (params.length == 1)
        {
            pid = (Member) params[0];
        }

        var ui = UserStorage.getByID(pid.getUser().getIdLong());

        var eb = new EmbedBuilder();
        eb.setDescription("**Balance:** " + ui.getCoins() + " " + Item.COINDIRIL + "s");
        eb.setColor(new Color(0, 128, 255).getRGB());
        eb.setTitle("Inventory of " + pid.getEffectiveName() + ", showing max 12 results.");
        eb.setFooter("Please note that special items like cards do not appear here.\nFor a complete list of your items, please use the command `fullinventory`.", null);

        if (ui.getInventory().isEmpty())
        {
            eb.addField("This inventory is empty.", "¯\\_(ツ)_/¯", false);
        }
        else
        {
            ui.getInventory().stream().filter(i -> {
                var f = i.getItem() instanceof ItemCard;
                return !f;
            }).limit(12).forEach(c -> {
                var item = c.getItem();
                var amt = c.getAmount();

                var title = new StringBuilder();

                if (item.hasIcon())
                {
                    title.append(item.getIcon());
                }

                title.append(item.getHumanName());

                var sub = new StringBuilder();
                sub.append("**ID:** ");
                sub.append(item.getID());
                sub.append("\n**Amount:** ");
                sub.append(amt);
                sub.append("\n**One sells for:** ");
                sub.append(item.getSellValue());
                sub.append(" ");
                sub.append(Item.COINDIRIL);

                eb.addField(title.toString(), sub.toString(), true);
            });
        }

        channel.sendMessage(new MessageBuilder(eb).build()).submit();
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { Member.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("inventory", "inv", "i");
    }

    @Override
    public String description()
    {
        return "Lists the items in your / others' inventory.";
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
