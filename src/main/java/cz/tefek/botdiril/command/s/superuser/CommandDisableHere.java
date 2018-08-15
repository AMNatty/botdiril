package cz.tefek.botdiril.command.s.superuser;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.core.server.ChannelCache;
import cz.tefek.botdiril.sql.DB;
import net.dv8tion.jda.core.entities.Message;

public class CommandDisableHere extends SuperUserCommandBase
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("toggledisable");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var tc = message.getTextChannel();
        var member = message.getGuild().getMember(message.getAuthor());

        final var pc = SuperUserCommandBase.getPrintChannel(message.getGuild());

        if (pc == null)
        {
            tc.sendMessage("Sorry but you need a logging channel for this.").submit();
            return;
        }

        try
        {
            var c = DB.getConnection();

            synchronized (c)
            {
                var cid = tc.getIdLong();

                if (ChannelCache.disabledChannels.contains(cid))
                {
                    var ust = c.prepareStatement("DELETE FROM turnedoff WHERE channelid=(?)");
                    ust.setLong(1, cid);
                    ust.executeUpdate();

                    ChannelCache.disabledChannels.remove(cid);

                    tc.sendMessage("Removed " + tc.getAsMention() + " from the list of disabled channels.").queue(succ -> {
                        var desc = message.getTextChannel().getAsMention() + " can be used for bot commands by non-superuser members again.";
                        var embed = SuperUserCommandBase.generateLoggedAction(EnumModerativeAction.ENABLE_ROOM, message.getTextChannel(), member, succ.getIdLong(), desc, desc);
                        pc.sendMessage(embed).submit();
                    });
                }
                else
                {

                    var ust = c.prepareStatement("INSERT INTO turnedoff(channelid) VALUES(?)");
                    ust.setLong(1, cid);
                    ust.executeUpdate();

                    ChannelCache.disabledChannels.add(cid);

                    tc.sendMessage("Added " + tc.getAsMention() + " to the list of disabled channels.").queue(succ -> {
                        var desc = message.getTextChannel().getAsMention() + " can no longer be used for bot commands by non-superuser members.";
                        var embed = SuperUserCommandBase.generateLoggedAction(EnumModerativeAction.DISABLE_ROOM, message.getTextChannel(), member, succ.getIdLong(), desc, desc);
                        pc.sendMessage(embed).submit();
                    });
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String usage()
    {
        return "toggledisable";
    }

    @Override
    public String description()
    {
        return "Makes the bot commands superuser only in this channel and vice versa.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }
}
