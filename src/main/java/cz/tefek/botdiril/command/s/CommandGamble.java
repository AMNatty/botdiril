package cz.tefek.botdiril.command.s;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.AmountParser;
import cz.tefek.botdiril.userdata.items.Item;
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
            message.getTextChannel().sendMessage("Hey don't spam me so hard! (8 seconds)").submit();
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
                var betRate = bet / (double) kekamt;

                var random = new SecureRandom();
                var chest = random.nextInt(101) > 96 && bet > 2000;
                var ratio = random.nextDouble() * 2 + betRate * 0.05;
                var jackpot = random.nextInt(1001) > 988 + (1 - betRate) * 10;
                var ultraJackpot = random.nextInt(5001) > 4985 + (1 - betRate) * 10;

                var won = Math.round(ratio * bet) - bet;

                if (ratio < 0.25)
                    won = -bet;

                if (jackpot)
                    won = bet * 8;

                if (ultraJackpot)
                    won = bet * 32;

                if (jackpot && ultraJackpot)
                    won = bet * 80;

                if (ultraJackpot && jackpot)
                {
                    message.getTextChannel().sendMessage("NANI?!?! How did you get both JACKPOT and ULTRA JACKPOT at once?!?! You win " + won + Item.KEK + "s.").submit();
                }
                else if (ultraJackpot)
                {
                    message.getTextChannel().sendMessage("ULTRA POGGERS!! You win " + won + Item.KEK + "s. ヽ༼ຈل͜ຈ༽ﾉ").submit();
                }
                else if (jackpot)
                {
                    message.getTextChannel().sendMessage("JACKPOT!! You win " + won + Item.KEK + "s.").submit();
                }
                else
                {
                    if (won == 0)
                    {
                        message.getTextChannel().sendMessage("You get your bet back this time. Your lucky number: ").submit();
                    }
                    else if (ratio < 0.25)
                    {
                        message.getTextChannel().sendMessage("You lost your entire bet - " + bet + Item.KEK + "s. Your lucky number: ").submit();
                    }
                    else if (ratio < 1)
                    {
                        message.getTextChannel().sendMessage("Unlucky... You lost " + Math.abs(won) + Item.KEK + "s. Your lucky number: ").submit();
                    }
                    else if (ratio < 1.5)
                    {
                        message.getTextChannel().sendMessage("You win " + Math.abs(won) + Item.KEK + "s! Your lucky number: ").submit();
                    }
                    else
                    {
                        message.getTextChannel().sendMessage("Nice! You win " + Math.abs(won) + Item.KEK + "s! Your lucky number: ").submit();
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
