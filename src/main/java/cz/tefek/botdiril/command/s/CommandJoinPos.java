package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCategory;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CommandJoinPos implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { Member.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("joinpos");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        Member m;

        if (params.length == 0)
        {
            m = message.getMember();
        }
        else
        {
            m = (Member) params[0];
        }

        var members = m.getGuild().getMembers().stream().sorted((a, b) -> {
            return a.getJoinDate().compareTo(b.getJoinDate());
        }).collect(Collectors.toList());

        var pos = IntStream.range(0, members.size()).filter(i -> members.get(i).equals(m)).findFirst().orElse(-1) + 1;

        message.getTextChannel().sendMessage("The join position of " + m.getEffectiveName() + " is: " + pos).submit();
    }

    @Override
    public String usage()
    {
        return "joinpos [member]";
    }

    @Override
    public String description()
    {
        return "Gets the join position of a member user.";
    }

    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.GENERAL;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

}
