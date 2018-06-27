package cz.tefek.botdiril.command.s;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.ItemPair;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CommandFullInventory implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { Member.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("fullinventory", "fullinv", "finv", "fi");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var channel = message.getTextChannel();
        var pid = message.getGuild().getMember(message.getAuthor());
        var uid = message.getAuthor().getIdLong();

        if (params.length == 1)
        {
            pid = (Member) params[0];
        }

        if (!BotdirilConfig.S3_ENABLED)
        {
            channel.sendMessage("Sorry but this server does not support FullInventory.").submit();
            return;
        }

        channel.sendMessage("Processing...").submit();

        var ui = UserStorage.getByID(uid);

        var sanitizedName = pid.getEffectiveName().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        var html = new StringBuilder("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"utf-8\" />");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + BotdirilConfig.S3_CSS + "\" />");
        html.append("<script src=\"" + BotdirilConfig.S3_JS + "\"></script>");
        html.append("<title>" + sanitizedName + "'s inventory</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<img src=\"" + BotdirilConfig.S3_LOGO + "\" style=\"height: 128px\">");
        html.append("<h1>");
        html.append(sanitizedName);
        html.append("'s inventory</h1>");
        html.append("<h2>Balance: ");
        html.append(ui.getCoins());
        html.append("</h2>");

        if (ui.getInventory().isEmpty())
        {
            html.append("<div class=\"empty\">This inventory is empty.<br>¯\\_(ツ)_/¯</div>");
        }
        else
        {
            html.append("<table><tr><th></th><th id=\"hname\">Item name</th><th id=\"hamount\">Amount</th><th id=\"hid\">Item ID</th><th id=\"hdescription\">Item Description</th><th id=\"hsell\">Sell Value</th><th id=\"hsellall\">Total Sell Value</th>");

            for (ItemPair entry : ui.getInventory())
            {
                var item = entry.getItem();
                var amt = entry.getAmount();

                html.append("<tr><td>");

                if (item.hasIcon())
                {
                    var matcher = Pattern.compile("[0-9]+").matcher(item.getIcon());

                    if (matcher.find())
                    {
                        var em = message.getJDA().getEmoteById(matcher.group());

                        if (em != null)
                        {
                            html.append("<img width=\"32\" height=\"32\" src=\"");
                            html.append(em.getImageUrl());
                            html.append("\">");
                        }
                    }
                }

                html.append("</td><td class=\"name\">");
                html.append(item.getHumanName());
                html.append("</td><td class=\"amount\">");
                html.append(amt);
                html.append("</td><td class=\"id\">");
                html.append(item.getID());
                html.append("</td><td class=\"description\">");
                html.append(item.getDescription());
                html.append("</td><td class=\"sell\">");
                html.append(item.getSellValue());
                html.append("</td><td class=\"sellall\">");
                html.append(item.getSellValue() * amt);
                html.append("</td>");
            }

            html.append("</table>");
        }

        html.append("</body>");
        html.append("</html>");

        var credentials = new BasicAWSCredentials(BotdirilConfig.S3_KEY, BotdirilConfig.S3_PASS);
        var s3client = new AmazonS3Client(credentials);
        s3client.setEndpoint(BotdirilConfig.S3_ENDPOINT);

        var data = html.toString().getBytes(StandardCharsets.UTF_8);

        InputStream stream = new ByteArrayInputStream(data);

        var meta = new ObjectMetadata();
        meta.setContentLength(data.length);
        meta.setContentType("text/html; charset=utf-8");

        var por = new PutObjectRequest(BotdirilConfig.S3_BUCKET, "users/" + uid + ".html", stream, meta);
        por.withCannedAcl(CannedAccessControlList.PublicRead);

        por.withGeneralProgressListener(pe -> {
            if (pe.getEventType() == ProgressEventType.TRANSFER_COMPLETED_EVENT)
            {
                channel.sendMessage(BotdirilConfig.S3_SITE + "/users/" + uid + ".html?ts=" + System.currentTimeMillis()).submit();
            }
            else if (pe.getEventType() == ProgressEventType.TRANSFER_FAILED_EVENT)
            {
                channel.sendMessage("Your inventory could not be uploaded to the server. Sorry for the inconvenience.").submit();
            }
        });

        s3client.putObject(por);
    }

    @Override
    public String usage()
    {
        return "fullinventory";
    }

    @Override
    public String description()
    {
        return "Generates a table for your entire inventory.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.ECONOMY;
    }
}
