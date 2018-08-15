package cz.tefek.botdiril.command.s.superuser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.dv8tion.jda.core.entities.Message;

public class CommandListEmotes extends SuperUserCommandBase
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("listemote", "listemotes", "emotelist", "emotes");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var g = message.getGuild();
        var tc = message.getTextChannel();
        var member = g.getMember(message.getAuthor());

        final var pc = SuperUserCommandBase.getPrintChannel(message.getGuild());

        if (pc == null)
        {
            tc.sendMessage("Sorry but you need a logging channel for this.").submit();
            return;
        }

        var emotes = g.getEmotes();
        var emoteSplit = Lists.partition(emotes, 20);

        tc.sendMessage("Emotes for this server:\n").submit().thenAcceptAsync(c -> {
            var desc = member.getAsMention() + " listed all emotes.";
            var embed = SuperUserCommandBase.generateLoggedAction(EnumModerativeAction.EMOTE_LIST, message.getTextChannel(), member, c.getIdLong(), desc, desc);
            pc.sendMessage(embed).submit();
        });

        emoteSplit.forEach(c -> {
            var strEmote = c.stream().map(emote -> emote.getAsMention() + "`:" + emote.getName() + ":`").collect(Collectors.joining("\n"));
            tc.sendMessage(strEmote).submit();
        });
    }

    @Override
    public String usage()
    {
        return "listemote";
    }

    @Override
    public String description()
    {
        return "Lists the emotes on this server.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

}
