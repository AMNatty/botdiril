package cz.tefek.botdiril.voice.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import cz.tefek.util.ColonTimeParser;
import net.dv8tion.jda.core.entities.Guild;

public class AudioQueueManager
{
    private static final HashMap<Long, Deque<AudioTrack>> queueMap = new HashMap<>();
    public static final int MAX_PAGES = 10;
    public static final int PAGE_SIZE = 12;
    public static final int queueSize = MAX_PAGES * PAGE_SIZE;

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
            throw new CouldNotEnqueueException("You exceeded the " + queueSize + " track limit.");
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

    static boolean clearQueue(long gid)
    {
        if (!queueMap.containsKey(gid))
        {
            queueMap.put(gid, new LinkedList<AudioTrack>());

            return false;
        }

        queueMap.get(gid).clear();

        return true;
    }

    public static int howManyPages(Guild g)
    {
        return (howManyTracks(g) + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    public static int howManyTracks(Guild g)
    {
        var q = queueMap.get(g.getIdLong());

        if (q == null)
            return 0;

        return q.size();
    }

    public static List<AudioTrack> getPaginatedView(Guild g, int page)
    {
        var va = new ArrayList<AudioTrack>();
        var q = queueMap.get(g.getIdLong());

        if (q == null)
            return va;

        // Minus one because humans don't like arrays and pages start with 1
        va.addAll(q.stream().skip((page - 1) * PAGE_SIZE).limit(PAGE_SIZE).collect(Collectors.toList()));

        return va;
    }

    // Returns true if there was something to shuffle
    public static boolean shuffle(Guild g)
    {
        var gid = g.getIdLong();
        var q = queueMap.get(gid);

        if (q == null)
        {
            queueMap.put(gid, new LinkedList<AudioTrack>());

            return false;
        }

        if (q.size() < 2)
        {
            return false;
        }

        var toShuffle = new ArrayList<>(q);

        Collections.shuffle(toShuffle);

        q.removeAll(q);

        q.addAll(toShuffle);

        return true;
    }
}
