package cz.tefek.botdiril.command.s.superuser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.core.ServerPreferences;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class CommandListSuperUsers implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("superuserlist", "superusers", "sus");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var guild = message.getGuild();
        var sc = ServerPreferences.getServerByID(guild.getIdLong());

        var exec = BotdirilConfig.SUPERUSERS.stream().map(guild::getMemberById).filter(m -> m != null).map(Member::getAsMention).collect(Collectors.joining(", "));
        var roles = sc.getAllSuperUseredRoles().stream().map(guild::getRoleById).filter(r -> r != null).map(Role::getAsMention).collect(Collectors.joining(", "));
        var owner = guild.getOwner();
        var adminRoles = guild.getRoles().stream().filter(r -> r.hasPermission(Permission.ADMINISTRATOR));
        var suMembers = adminRoles.map(guild::getMembersWithRoles).flatMap(List::stream).distinct().map(Member::getAsMention).collect(Collectors.joining(", "));

        var eb = new EmbedBuilder();
        eb.setAuthor("Botdiril SuperUser", "https://www.tefek.cz/", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setTitle("Current SuperUser List");
        eb.setDescription("The list of all SuperUsers on this Discord server.");
        eb.setColor(0x0088ff);
        eb.addField("Executive (Owner) SuperUsers", exec, false);
        eb.addField("SuperUser from roles", roles, false);
        eb.addField("SuperUsers by default (Administrator)", suMembers, false);
        eb.addField("Server owner has all of the above by default", owner.getAsMention(), false);

        message.getTextChannel().sendMessage(eb.build()).submit();
    }

    @Override
    public String usage()
    {
        return "superusers";
    }

    @Override
    public String description()
    {
        return "Prints the list of all superusers on this Discord server.";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.ADMINISTRATIVE;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

}
