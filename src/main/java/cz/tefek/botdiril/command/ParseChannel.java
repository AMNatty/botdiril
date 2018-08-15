package cz.tefek.botdiril.command;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.TextChannel;

public class ParseChannel
{
    public static TextChannel parse(TextChannel req, String msg)
    {
        var g = req.getGuild();

        if (msg.isEmpty())
        {
            req.sendMessage("Text channel could not be parsed: The input channel cannot be empty.").submit();
            return null;
        }

        try
        {
            var m = Pattern.compile("[0-9]+").matcher(msg);

            if (m.find())
            {
                var id = Long.parseLong(m.group());

                var tc = g.getTextChannelById(id);

                if (tc != null)
                {
                    return tc;
                }
                else
                {
                    req.sendMessage("Text channel could not be parsed: Could find a channel with that snowflake ID.").submit();
                    return null;
                }
            }
        }
        catch (NumberFormatException e)
        {
            req.sendMessage("Text channel could not be parsed: Could not parse the snowflake ID.").submit();
            return null;
        }

        req.sendMessage("Text channel could not be parsed: Could not locate the snowflake ID.").submit();
        return null;
    }
}
