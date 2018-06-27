package cz.tefek.botdiril.command.s;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONTokener;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public final class CommandLoLItem implements Command
{
    @Override
    public String usage()
    {
        return "lolitem [item]";
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var channel = message.getTextChannel();

        var metaURL = ("https://ddragon.leagueoflegends.com/realms/na.json");

        String patch;
        String cdn;

        try
        {
            var metaStream = new URL(metaURL).openStream();
            var tok = new JSONObject(new JSONTokener(metaStream));

            patch = tok.getJSONObject("n").getString("item");
            cdn = tok.getString("cdn") + "/";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            patch = null;
            cdn = null;
        }

        var jsonURL = cdn + patch + "/data/en_US/item.json";

        try
        {
            var stream = new URL(jsonURL).openStream();
            var tok = new JSONTokener(stream);
            var obj = new JSONObject(tok);

            var iar = obj.getJSONObject("data");

            JSONObject item = null;

            if (params.length == 0)
            {
                var iarr = iar.keySet().toArray(new String[0]);
                item = iar.getJSONObject(iarr[new Random().nextInt(iarr.length)]);
            }
            else
            {
                for (var iterator = iar.keys(); iterator.hasNext();)
                {
                    var it = iar.getJSONObject(iterator.next());

                    if (it.getString("name").equalsIgnoreCase((String) params[0]))
                    {
                        item = it;
                        break;
                    }
                }
            }

            if (item == null)
            {
                channel.sendMessage("Item not found!").submit();
            }
            else
            {
                var eb = new EmbedBuilder();
                eb.setColor(0xff0055);
                eb.setThumbnail(cdn + patch + "/img/item/" + item.getJSONObject("image").getString("full"));
                eb.setTitle(item.getString("name"));
                if (item.getJSONObject("gold").getBoolean("purchasable"))
                {
                    eb.setDescription(item.getJSONObject("gold").getInt("total") + " gold");
                }
                else
                {
                    eb.setDescription("Cannot be purchased.");
                }
                var descRaw = item.getString("description");
                descRaw = descRaw.replaceAll("<br>|<br />|<hr>", "\n");
                descRaw = descRaw.replaceAll("<active>|</active>|<passive>|</passive>|<unique>|</unique>", "**");
                descRaw = descRaw.replaceAll("<rules>|</rules>|<groupLimit>|</groupLimit>", "*");
                descRaw = descRaw.replaceAll("<.+?>", "");

                var matcher = Pattern.compile("@(.+?)@").matcher(descRaw);

                while (matcher.find())
                {
                    var full = matcher.group();
                    var eff = matcher.group(1);

                    double base = Double.NaN;

                    if (eff.contains("*"))
                    {
                        var effMul = eff.split("\\*");

                        if (effMul[0].matches("Effect[0-9]+Amount"))
                        {
                            base = item.getJSONObject("effect").getDouble(effMul[0]) * Double.parseDouble(effMul[1]);
                        }
                        else if (effMul[1].matches("Effect[0-9]+Amount"))
                        {
                            base = item.getJSONObject("effect").getDouble(effMul[1]) * Double.parseDouble(effMul[0]);
                        }
                    }
                    else
                    {
                        base = item.getJSONObject("effect").getDouble(eff);
                    }

                    NumberFormat formatter = new DecimalFormat("##.###");

                    descRaw = descRaw.replaceAll(full.replace("*", "\\*"), formatter.format(base));
                }

                eb.addField(item.getString("plaintext"), descRaw, false);

                channel.sendMessage(new MessageBuilder(eb).build()).submit();
            }

            stream.close();
        }
        catch (IOException e)
        {
            channel.sendMessage("It seems that an API error has occurred. This might be due to structural changes in Riot's DataDragon service.").submit();

            e.printStackTrace();
        }
    }

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("lolitem");
    }

    @Override
    public String description()
    {
        return "Prints info about a League of Legends item. Use without arguments get a random item. This command takes into account only current patch data.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public boolean hasOpenEnd()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.LEAGUE;
    }
}
