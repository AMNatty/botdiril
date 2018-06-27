package cz.tefek.botdiril.voice.music;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import cz.tefek.util.ColonTimeParser;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class AudioQueueManager
{
    private static final HashMap<Long, Deque<AudioTrack>> queueMap = new HashMap<>();
    public static final int queueSize = 128;

    public static void enqueue(Guild guild, AudioTrack track) throws CouldNotEnqueueException
    {
        var gid = guild.getIdLong();

        if (!queueMap.containsKey(gid))
        {
            queueMap.put(gid, new LinkedList<AudioTrack>());
        }

        var que = queueMap.get(gid);

        if (que.size() > queueSize)
        {
            throw new CouldNotEnqueueException("You exceeded the 128 track limit.");
        }

        que.add(track);

        var tc = ActiveChannelManager.getPrintChannel(guild);

        var player = ActiveChannelManager.getPlayer(guild);

        if (player.getPlayingTrack() == null)
        {
            player.playTrack(que.poll());
        }
        else
        {
            var state = player.getPlayingTrack().getState();

            if (state == AudioTrackState.FINISHED || state == AudioTrackState.INACTIVE)
            {
                player.playTrack(que.poll());
            }
            else
            {
                var ti = track.getInfo();

                if (ti.length == Long.MAX_VALUE)
                {
                    tc.sendMessage("**Queued:** **" + ti.title + "** by **" + ti.author + "**.").submit();
                }
                else
                {
                    tc.sendMessage("**Queued:** **" + ti.title + "** by **" + ti.author + "**. Length: " + ColonTimeParser.fromMillis(ti.length)).submit();
                }
            }
        }
    }

    public static AudioTrack pollQueue(long gid)
    {
        if (!queueMap.containsKey(gid))
        {
            queueMap.put(gid, new LinkedList<AudioTrack>());
        }

        return queueMap.get(gid).poll();
    }

    public static void skip(Guild g, TextChannel tc)
    {
        var player = ActiveChannelManager.getPlayer(g);

        if (player == null)
        {
            tc.sendMessage("You can't skip right now, I am not playing.").submit();
            return;
        }

        var tcIn = ActiveChannelManager.getPrintChannel(g);

        if (!tc.equals(tcIn))
        {
            tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", tcIn.getAsMention())).submit();
            return;
        }

        if (player.getPlayingTrack() == null)
        {
            tc.sendMessage("You can't skip right now, I am not playing.").submit();
            return;
        }

        var state = player.getPlayingTrack().getState();

        if (state != AudioTrackState.PLAYING && state != AudioTrackState.SEEKING)
        {
            tc.sendMessage("You can't skip right now, nothing is playing.").submit();
            return;
        }

        player.stopTrack();

        var qm = pollQueue(g.getIdLong());

        tc.sendMessage(":fast_forward: **Skipping...**").submit();

        if (qm != null)
        {
            player.playTrack(qm);
        }

        if (player.isPaused())
        {
            player.setPaused(false);
        }
    }
}
