package cz.tefek.botdiril.userdata.items;

import net.dv8tion.jda.core.entities.Message;

public class AmountParser
{
    public static long parse(String strp, Message message, long userHas)
    {
        long amount = Long.MIN_VALUE;

        try
        {
            amount = Long.parseUnsignedLong(strp);
        }
        catch (NumberFormatException e)
        {
            if (strp.endsWith("%"))
            {
                try
                {
                    double f = Float.parseFloat(strp.substring(0, strp.length() - 1));

                    if (f < 0 || f > 100)
                    {
                        message.getTextChannel().sendMessage("This is not a valid percentage.").submit();
                        return Long.MIN_VALUE;
                    }
                    else
                    {
                        amount = Math.round((f / 100.0) * userHas);
                    }
                }
                catch (NumberFormatException e1)
                {

                }
            }
            else
            {
                if (strp.equalsIgnoreCase("all") || strp.equalsIgnoreCase("everything"))
                {
                    amount = userHas;
                }
                else if (strp.equalsIgnoreCase("half"))
                {
                    amount = userHas / 2L;
                }
            }
        }

        if (amount == Long.MIN_VALUE)
            message.getTextChannel().sendMessage("Number could could be parsed, please make sure you entered it correctly.").submit();

        return amount;
    }
}
