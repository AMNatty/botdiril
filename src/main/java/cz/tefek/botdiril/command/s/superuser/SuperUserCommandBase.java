package cz.tefek.botdiril.command.s.superuser;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.core.ServerPreferences;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class SuperUserCommandBase implements Command
{
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.SUPERUSER;
    }

    public static boolean validateCaller(Message message)
    {
        return BotdirilConfig.isSuperUser(message.getGuild(), message.getAuthor());
    }

    public static TextChannel getPrintChannel(Guild g)
    {
        return ServerPreferences.getServerByID(g.getIdLong()).getReportChannel(g);
    }

    public static MessageEmbed generateLoggedAction(EnumModerativeAction action, TextChannel perfChannel, Member member, Long messageID, String note, String fullDesc)
    {
        var g = perfChannel.getGuild();

        var eb = new EmbedBuilder();
        eb.setAuthor("Botdiril's SuperUser Tools", null, g.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(0x0B738E);
        eb.setTitle("Action: " + action.getDescription());
        eb.setDescription(note);
        eb.addField("Channel: ", perfChannel.getAsMention(), true);
        eb.addField("Member: ", member.getAsMention(), true);
        eb.setFooter("User's ID: " + member.getUser().getIdLong() + " · Response ID: " + messageID, null);

        return eb.build();
    }
}
