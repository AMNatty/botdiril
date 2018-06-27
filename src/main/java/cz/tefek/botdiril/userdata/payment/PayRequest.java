package cz.tefek.botdiril.userdata.payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

// A large todo
@SuppressWarnings("unused")
public class PayRequest
{
    private static final List<AwaitingResponse> awaitingResponses = new CopyOnWriteArrayList<>();

    private static AtomicBoolean cachingNow = new AtomicBoolean(false);
    private static AtomicBoolean cacheAgain = new AtomicBoolean(false);

    private static AwaitingResponse getByMessage(long id)
    {
        return awaitingResponses.stream().filter(p -> p.getMessageID() == id).findFirst().orElse(null);
    }

    public static void addPayRequest(long cost, User user, TextChannel channel, String item, long timeStamp, String uniqueKey, PrivateChannel requesterPM)
    {
        var embedBuilder = new EmbedBuilder();
        var requester = requesterPM.getUser();

        embedBuilder.setAuthor("Botdiril Payment Notification", null, "attachment://coindiril.png");
        embedBuilder.setThumbnail(user.getJDA().getSelfUser().getEffectiveAvatarUrl());
        embedBuilder.setDescription(requester.getName() + " wants to charge you " + cost + " <:coindiril:446988933763563520>s for an item: " + item + ".\nTo confirm this request, please confirm it by pressing the :white_check_mark: emote.\n**If you do not trust this seller, press the :no_entry_sign: emote.**");
        embedBuilder.setColor(0xffff00);

        var embed = embedBuilder.build();

        var message = new MessageBuilder(user.getAsMention()).setEmbed(embed).build();

        if (user.getMutualGuilds().contains(channel.getGuild()))
        {
            channel.sendFile(new File("icons/coindiril.png"), message).queue(succ -> {
                succ.addReaction("🚫").queue(future -> {
                    succ.addReaction("🔄").queue(future2 -> {
                        succ.addReaction("✅").queue(future3 -> {
                            awaitingResponses.add(new AwaitingResponse(requester, user.getIdLong(), succ.getIdLong(), channel));
                            cacheResponses();

                            Gateway.sendOK(requesterPM, uniqueKey);
                        });
                    });
                }, fail -> {
                    fail.printStackTrace();
                });
            });
        }
        else
        {
            Gateway.sendError(requesterPM, Gateway.ERROR_GUILD_NOT_MUTUAL, "Botdiril is not present on that server.");
        }
    }

    private static void cacheResponses()
    {
        if (cachingNow.get())
        {
            cacheAgain.set(true);
            return;
        }

        cachingNow.set(true);

        do
        {
            cacheAgain.set(false);
            unsyncedCache();
        }
        while (cacheAgain.get());

        cachingNow.set(false);
    }

    private static void unsyncedCache()
    {
        try (PrintWriter writer = new PrintWriter("cache/unprocessed.txt"))
        {
            awaitingResponses.forEach(c -> {
                TextChannel channel = c.getTextChannel();

                writer.println(channel.getGuild().getIdLong() + "#" + channel.getIdLong() + "#" + c.getMessageID());
            });
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void parseGuild(GuildMessageReactionAddEvent event)
    {
        var messageID = event.getMessageIdLong();
        var reactionName = event.getReaction().getReactionEmote().getName();

        var resp = getByMessage(messageID);

        if (resp != null)
        {
            if (resp.getUserID() == event.getUser().getIdLong())
            {
                if (reactionName.equals("🚫"))
                {
                    resp.getTextChannel().getMessageById(messageID).queue(succ -> {
                        succ.delete().submit();

                        resp.getTextChannel().sendMessage("Transaction cancelled.").submit();
                    });
                }
                else if (reactionName.equals("🔄"))
                {
                    resp.getTextChannel().getMessageById(messageID).queue(succ -> {
                        succ.delete().submit();

                        resp.getTextChannel().sendMessage("Transaction and all further transactions for this user/bot are now pre-approved.\n*You can change this in your settings by sending Botdiril the a PM:* `preapproved`").submit();
                    });
                }
                else if (reactionName.equals("✅"))
                {
                    resp.getTextChannel().getMessageById(messageID).queue(succ -> {
                        succ.delete().submit();

                        resp.getTextChannel().sendMessage("Transaction approved.").submit();
                    });
                }
            }
        }
    }

    private static class AwaitingResponse
    {
        private final long messageID;
        private final long userID;

        private final User requester;

        private TextChannel tchannel;

        public AwaitingResponse(User requester, long uid, long messageID, TextChannel channel)
        {
            this.requester = requester;
            this.messageID = messageID;
            this.userID = uid;
            this.tchannel = channel;
        }

        public long getMessageID()
        {
            return messageID;
        }

        public long getUserID()
        {
            return userID;
        }

        public TextChannel getTextChannel()
        {
            return tchannel;
        }

        public User getRequester()
        {
            return requester;
        }
    }

    public static void clear(JDA jda)
    {
        var folder = new File("cache");

        if (!folder.isDirectory())
        {
            folder.mkdir();
            return;
        }

        var file = new File("cache/unprocessed.txt");

        if (!file.exists())
        {
            return;
        }

        System.out.println("Attempting to delete old payment requests.");

        try (var reader = new BufferedReader(new FileReader(file)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                var data = line.split("#");

                if (data.length != 3)
                    continue;

                long guild = Long.parseLong(data[0]);
                long channel = Long.parseLong(data[1]);
                long message = Long.parseLong(data[2]);

                var guildR = jda.getGuildById(guild);

                if (guildR != null)
                {
                    var channelR = guildR.getTextChannelById(channel);

                    if (channelR != null)
                    {
                        channelR.getMessageById(message).queue(c -> c.delete().submit());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        file.delete();
    }
}
