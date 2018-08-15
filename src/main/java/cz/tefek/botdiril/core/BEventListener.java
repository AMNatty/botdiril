package cz.tefek.botdiril.core;

import java.util.Timer;
import java.util.TimerTask;

import cz.tefek.botdiril.command.CommandInterpreter;
import cz.tefek.botdiril.core.server.ChannelCache;
import cz.tefek.botdiril.userdata.payment.Gateway;
import cz.tefek.botdiril.userdata.payment.PayRequest;
import cz.tefek.botdiril.voice.music.ActiveChannelManager;
import cz.tefek.util.MiniTime;
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
    private static final String PLAYING = "www.tefek.cz";

    @Override
    public void onReady(ReadyEvent event)
    {
        event.getJDA().getPresence().setGame(Game.listening(PLAYING));

        event.getJDA().getGuilds().forEach(g -> {
            var sc = ServerPreferences.getServerByID(g.getIdLong());
            if (sc == null)
            {
                ServerPreferences.addServer(g);
            }
        });

        PayRequest.clear(event.getJDA());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        var g = event.getGuild();

        System.out.println("Joining guild " + g);

        var sc = ServerPreferences.getServerByID(g.getIdLong());
        if (sc == null)
        {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run()
                {
                    ServerPreferences.addServer(g);
                }
            }, 5000);
        }
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

            var hasPower = BotdirilConfig.isSuperUserOverride(guild, user);

            if (content.toLowerCase().startsWith("botdiril ") && hasPower)
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

                            ServerPreferences.wipePreviousConfig(guild);
                        }
                        else if (content.toLowerCase().startsWith("botdiril prefix "))
                        {
                            var prefix = content.substring("botdiril prefix ".length());

                            ServerPreferences.fixPrefix(channel, prefix);
                        }
                        else if (content.toLowerCase().startsWith("botdiril channel "))
                        {
                            var prefix = content.substring("botdiril channel ".length());

                            ServerPreferences.setChannel(channel, prefix);
                        }
                    }
                    else
                    {
                        var prefix = content.substring("botdiril ".length());
                        ServerPreferences.tryInstallServer(channel, prefix);
                    }
                }
            }
            else if (sc != null)
            {
                if (sc.isInstalled())
                {
                    if (content.startsWith(sc.getPrefix()))
                    {
                        if (ChannelCache.disabledChannels.contains(channel.getIdLong()) && !hasPower)
                        {
                            channel.sendMessage("You are not allowed to use my commands here.").submit();
                            return;
                        }

                        var punished = sc.isPunished(channel, member);

                        if (punished == -1)
                        {
                            CommandInterpreter.interpretCommand(event.getMessage(), content.substring(sc.getPrefix().length()));
                        }
                        else
                        {
                            channel.sendMessage(String.format("You are punished and therefore you cannot use my commands. You need to wait %s before using my commands again.", MiniTime.fromMillisDiffNow(punished)));
                        }
                    }
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
