package cz.tefek.botdiril.command.s;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.random.RandomDataGenerator;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.AmountParser;
import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.card.ItemCard;
import cz.tefek.botdiril.userdata.items.crate.CrateDrops;
import net.dv8tion.jda.core.entities.Message;

public class CommandGamble implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("gamble");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());

        if (ui.useTimer("gamble_timeout", 500) != -1)
        {
            message.delete().queue();
            return;
        }

        if (params.length == 0)
        {
            message.getTextChannel().sendMessage("Wrong number of arguments... Usage: `" + ServerPreferences.getServerByID(message.getGuild().getIdLong()).getPrefix() + this.usage() + "`").submit();
            return;
        }

        var strp = (String) params[0];

        var keks = Item.getByID("kek");

        long kekamt = ui.howManyOf(keks);

        if (kekamt == 0)
        {
            message.getTextChannel().sendMessage("You have no " + Item.KEK + "s, buy some in the shop.").submit();
            return;
        }

        long bet = AmountParser.parse(strp, message, kekamt);

        if (bet == 0)
        {
            message.getTextChannel().sendMessage("You gamble nothing... :thinking: What do you get? Nothing.").submit();
        }
        else if (bet == Long.MIN_VALUE)
        {
            message.getTextChannel().sendMessage("You need to specify a valid amount of " + Item.KEK + "s.").submit();
        }
        else
        {
            if (bet > kekamt)
            {
                message.getTextChannel().sendMessage("You can't gamble more " + Item.KEK + "s than you have... ").submit();
            }
            else
            {
                var random = new SecureRandom();
                var chest = random.nextInt(101) > 96 && bet > 2500;
                var ratio = random.nextDouble() * 1.8;
                var jackpot = random.nextDouble();

                var won = Math.round(ratio * bet) - bet;

                var perc = String.format(Locale.US, "%.2f%%", ratio * 100);

                if (jackpot > 0.95 && bet / (double) kekamt > 0.1)
                {
                    var jr = random.nextDouble();

                    Item card;
                    while (!((card = CrateDrops.rollItem((long) (5_000 + Math.pow(bet, 0.8)), new RandomDataGenerator())) instanceof ItemCard));

                    ui.addItem(card);

                    if (jr > 0.99)
                    {
                        won = bet * 16;
                        message.getTextChannel().sendMessage("GIGA POGGERS!! You win " + won + Item.KEK + " and " + card.getIcon() + card.getHumanName() + ". ヽ༼ຈل͜ຈ༽ﾉ").submit();
                    }
                    else if (jr > 0.95)
                    {
                        won = bet * 8;
                        message.getTextChannel().sendMessage("ULTRA POGGERS!! You win " + won + Item.KEK + " and " + card.getIcon() + card.getHumanName() + ".").submit();
                    }
                    else if (jr > 0.9)
                    {
                        won = bet * 6;
                        message.getTextChannel().sendMessage("POGGERS!! You win " + won + Item.KEK + " and " + card.getIcon() + card.getHumanName() + ".").submit();
                    }
                    else if (jr > 0.5)
                    {
                        won = bet * 4;
                        message.getTextChannel().sendMessage("<a:kekoverdrive:471056255734120458>! You win " + won + Item.KEK + " and " + card.getIcon() + card.getHumanName() + ".").submit();
                    }
                    else
                    {
                        won = (long) (bet * 2.5);
                        message.getTextChannel().sendMessage("Mega <:kek:472067325080633346>! You win " + won + Item.KEK + " and " + card.getIcon() + card.getHumanName() + ".").submit();
                    }
                }
                else
                {
                    if (won == 0)
                    {
                        message.getTextChannel().sendMessage("You get your bet back this time. Your percentage: " + perc).submit();
                    }
                    else if (won == -bet)
                    {
                        message.getTextChannel().sendMessage("You lost your entire bet - " + bet + Item.KEK + "s. Your percentage: " + perc).submit();
                    }
                    else if (ratio < 1)
                    {
                        message.getTextChannel().sendMessage("Unlucky... You lost " + Math.abs(won) + Item.KEK + "s. Your percentage: " + perc).submit();
                    }
                    else if (ratio < 1.5)
                    {
                        message.getTextChannel().sendMessage("You win " + Math.abs(won) + Item.KEK + "s! Your percentage: " + perc).submit();
                    }
                    else
                    {
                        message.getTextChannel().sendMessage("Nice! You win " + Math.abs(won) + Item.KEK + "s! Your percentage: " + perc).submit();
                    }
                }

                ui.addItem(keks, won);

                if (chest && won > 0)
                {
                    var ch = Item.getByID("crate");
                    message.getTextChannel().sendMessage("It also seems that you won a " + ch.getIcon() + "!").submit();
                    ui.addItem(ch);
                }
            }
        }
    }

    @Override
    public String usage()
    {
        return "gamble <amount>";
    }

    @Override
    public String description()
    {
        return "The fastest way to become broke.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public boolean hasCustomParser()
    {
        return true;
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GAMBLING;
    }
}
