package cz.tefek.botdiril.command.s;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public final class CommandShop implements Command
{
    @Override
    public String usage()
    {
        return "shop";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var channel = message.getTextChannel();
        var prefix = ServerPreferences.getServerByID(message.getGuild().getIdLong()).getPrefix();
        var eb = new EmbedBuilder();
        eb.setTitle("Botdiril's shop list.");
        eb.setColor(Color.YELLOW.getRGB());

        Item.items.forEach(c -> {
            if (c.canBeBought())
            {
                StringBuilder title = new StringBuilder();

                if (c.hasIcon())
                {
                    title.append(c.getIcon());
                    title.append(" ");
                }

                title.append(c.getHumanName());

                StringBuilder sub = new StringBuilder();

                sub.append("**ID:** ");
                sub.append(c.getID());
                sub.append("\n**Price:** ");
                sub.append(c.getBuyValue());
                sub.append(" ");
                sub.append(Item.COINDIRIL);
                sub.append("\n");
                sub.append(c.canBeSold() ? "**Sells back for:** " + c.getSellValue() + " " + Item.COINDIRIL : "*Cannot be sold.*");

                eb.addField(title.toString(), sub.toString(), true);
            }
        });
        eb.setFooter("Tip: Use `%sbuy <item> [amount]`, `%ssell <item> [amount]` or `%siteminfo <item>`.".replace("%s", prefix), null);

        channel.sendMessage(new MessageBuilder(eb).build()).submit();
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("shop", "store", "s", "market");
    }

    @Override
    public String description()
    {
        return "Shop, buy some items here.";
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
