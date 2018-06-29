package cz.tefek.botdiril.core;

import cz.tefek.botdiril.command.CommandInterpreter;
import cz.tefek.botdiril.userdata.payment.Gateway;
import cz.tefek.botdiril.userdata.payment.PayRequest;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BEventListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
        event.getJDA().getPresence().setGame(Game.listening("www.tefek.cz"));

        event.getJDA().getGuilds().forEach(g -> {
            if (ServerPreferences.getServerByID(g.getIdLong()) == null)
            {
                ServerPreferences.addServer(g.getDefaultChannel());
            }
        });

        PayRequest.clear(event.getJDA());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        ServerPreferences.addServer(event.getGuild().getDefaultChannel());
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
        PayRequest.parseGuild(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        var user = event.getAuthor();

        if (event.isFromType(ChannelType.PRIVATE))
        {
            var channel = event.getPrivateChannel();
            var content = event.getMessage().getContentRaw();
            final var pr = "PaymentRequest:";

            if (content.toLowerCase().startsWith(pr.toLowerCase()))
            {
                // It is a payment request.
                System.out.println("Inbound payment from: " + user.getIdLong());

                Gateway.processPayment(channel, content.substring(pr.length()).trim(), user);
            }
        }

        if (event.isFromType(ChannelType.TEXT) && !user.isBot())
        {
            var guild = event.getGuild();
            var sc = ServerPreferences.getServerByID(guild.getIdLong());
            var member = guild.getMember(user);

            var bot = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
            var channel = event.getTextChannel();
            var content = event.getMessage().getContentRaw();

            if (content.toLowerCase().startsWith("botdiril ") && (member.hasPermission(Permission.ADMINISTRATOR) || member.getUser().getIdLong() == 263648016982867969L))
            {
                if (content.equalsIgnoreCase("botdiril go away"))
                {
                    channel.sendMessage("Alright... :worried:").complete();
                    guild.leave().complete();
                }
                else
                {
                    if (sc.isInstalled())
                    {
                        if (content.equalsIgnoreCase("botdiril delete everything pls"))
                        {
                            guild.getController().setNickname(bot, "Botdiril").complete();
                            channel.sendMessage("Alright, I'll reset it all.").complete();

                            ServerPreferences.wipePreviousConfig(channel);
                        }
                        else if (content.toLowerCase().startsWith("botdiril prefix "))
                        {
                            var prefix = content.substring("botdiril prefix ".length());

                            ServerPreferences.fixPrefix(channel, prefix);
                        }
                    }
                    else
                    {
                        var prefix = content.substring("botdiril ".length());
                        ServerPreferences.tryInstallServer(channel, prefix);
                    }
                }
            }

            if (sc != null)
                if (sc.isInstalled())
                {
                    if (content.startsWith(sc.getPrefix()))
                    {
                        CommandInterpreter.interpretCommand(event.getMessage(), content.substring(sc.getPrefix().length()));
                    }
                }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event)
    {
        if (event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong())
            ActiveChannelManager.movedTo(event.getChannelJoined());
    }
}
