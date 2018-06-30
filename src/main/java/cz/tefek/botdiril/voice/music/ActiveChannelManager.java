package cz.tefek.botdiril.voice.music;

import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.voice.AudioHandler;
import cz.tefek.util.ColonTimeParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ActiveChannelManager
{
    private static final ConcurrentHashMap<Long, TextChannel> printChannel = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, VoiceChannel> vcs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, AudioPlayer> aps = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, AudioPlayerManager> apms = new ConcurrentHashMap<>();

    public static AudioPlayer getPlayer(Guild g)
    {
        return aps.get(g.getIdLong());
    }

    public static AudioPlayerManager getAudioPlayerManager(Guild g)
    {
        return apms.get(g.getIdLong());
    }

    public static TextChannel getPrintChannel(Guild guild)
    {
        return printChannel.get(guild.getIdLong());
    }

    public static VoiceChannel getVoiceChannel(Guild guild)
    {
        return vcs.get(guild.getIdLong());
    }

    // Let's hope all of the maps are in sync when this is called
    // This could end up in a disaster otherwise
    private static boolean isPlaying(Guild g)
    {
        return aps.containsKey(g.getIdLong());
    }

    public static void movedTo(VoiceChannel vc)
    {
        if (vc == null)
            return;

        var pc = getPrintChannel(vc.getGuild());

        if (pc == null)
            return;

        pc.sendMessage("I was moved to " + vc.getName() + ".").submit();
        vcs.put(vc.getGuild().getIdLong(), vc);
    }

    private static void startPlaying(TextChannel tc, VoiceChannel vc)
    {
        var g = vc.getGuild();
        var gid = g.getIdLong();

        var am = g.getAudioManager();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        player.addListener(new TrackHandler(g));
        player.setVolume(ServerPreferences.getServerByID(gid).getVolume());

        am.setSendingHandler(new AudioHandler(player));

        am.openAudioConnection(vc);

        printChannel.put(gid, tc);
        vcs.put(gid, vc);
        apms.put(gid, playerManager);
        aps.put(gid, player);
    }

    public static void leave(Guild guild, TextChannel tc)
    {
        if (!isPlaying(guild))
        {
            tc.sendMessage("Error: The music bot is not in a voice channel.").submit();
            return;
        }

        var gid = guild.getIdLong();

        aps.get(gid).destroy();
        vcs.remove(gid);
        apms.remove(gid);
        aps.remove(gid);
        printChannel.remove(gid);

        AudioQueueManager.clearQueue(gid);

        tc.sendMessage("**Disconnected.**").submit();

        guild.getAudioManager().closeAudioConnection();
    }

    public static void pause(TextChannel tc, VoiceChannel vc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: The music bot is not playing.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }

            var player = getPlayer(g);

            if (player.getPlayingTrack() != null)
            {
                if (player.getPlayingTrack().getState() == AudioTrackState.PLAYING)
                {
                    if (!player.isPaused())
                    {
                        player.setPaused(true);
                    }
                    else
                    {
                        tc.sendMessage("I am already paused.").submit();
                    }
                }
                else
                {
                    tc.sendMessage("I am not playing right now.").submit();
                }
            }
            else
            {
                tc.sendMessage("I am not playing right now.").submit();
            }
        }
    }

    public static void setVolume(Guild g, TextChannel tc, int vol)
    {
        if (isPlaying(g))
        {
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            var player = getPlayer(g);

            player.setVolume(vol);
        }

        tc.sendMessage(String.format(":loud_sound: **Volume updated to %d%%.**", vol)).submit();
    }

    public static void resume(TextChannel tc, VoiceChannel vc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: There is no music to resume.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }

            var player = getPlayer(g);

            if (player.getPlayingTrack() != null)
            {
                if (player.getPlayingTrack().getState() == AudioTrackState.PLAYING)
                {
                    if (player.isPaused())
                    {
                        player.setPaused(false);
                    }
                    else
                    {
                        tc.sendMessage("I am already playing.").submit();
                    }
                }
                else
                {
                    tc.sendMessage("I am already playing right now.").submit();
                }
            }
            else
            {
                tc.sendMessage("I am already playing right now.").submit();
            }
        }
    }

    public static void search(TextChannel tc, VoiceChannel vc, String input)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            startPlaying(tc, vc);
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }
        }

        apms.get(g.getIdLong()).loadItem(input, new BAudioLoadResultHandler(g, input));
    }

    public static void nowPlaying(TextChannel tc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("I am not currently playing anything.").submit();
            return;
        }

        var player = getPlayer(g);
        var track = player.getPlayingTrack();

        if (track == null)
        {
            tc.sendMessage("I am not currently playing anything.").submit();
            return;
        }

        if (track.getState() != AudioTrackState.PLAYING && track.getState() != AudioTrackState.SEEKING)
        {
            tc.sendMessage("I am not currently playing anything.").submit();
            return;
        }

        var ti = track.getInfo();

        if (ti.length == Long.MAX_VALUE)
        {
            tc.sendMessage("**Playing:** **" + ti.title + "** by **" + ti.author + "**.").submit();
        }
        else
        {
            tc.sendMessage("**Playing:** **" + ti.title + "** by **" + ti.author + "**. Length: " + ColonTimeParser.fromMillis(ti.length)).submit();
        }
    }

    public static void clear(TextChannel tc, VoiceChannel vc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: The music bot is not playing.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }

            if (AudioQueueManager.clearQueue(g.getIdLong()))
            {
                tc.sendMessage(":asterisk: **Queue cleared.**").submit();
            }
            else
            {
                tc.sendMessage(":asterisk: **There is no queue to clear.**").submit();
            }
        }
    }

    public static void stop(TextChannel tc, VoiceChannel vc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: The music bot is not playing.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }

            AudioQueueManager.clearQueue(g.getIdLong());
            var ap = getPlayer(g);
            ap.stopTrack();
            tc.sendMessage(":stop_button: **Stopped.**").submit();
        }
    }

    public static void skip(Guild g, TextChannel tc, VoiceChannel vc)
    {
        var player = getPlayer(g);

        if (player == null)
        {
            tc.sendMessage("You can't skip right now, I am not playing.").submit();
            return;
        }

        var tcIn = getPrintChannel(g);

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

        var qm = AudioQueueManager.pollQueue(g.getIdLong());

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

    public static void queue(TextChannel tc, int page)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: The music bot is not playing.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            var player = getPlayer(g);
            var cp = player.getPlayingTrack();

            if (cp == null)
            {
                tc.sendMessage("I am not currently playing anything.").submit();
                return;
            }

            if (cp.getState() != AudioTrackState.PLAYING && cp.getState() != AudioTrackState.SEEKING)
            {
                tc.sendMessage("I am not currently playing anything.").submit();
                return;
            }

            var ti = cp.getInfo();

            if (ti.length == Long.MAX_VALUE)
            {
                tc.sendMessage(":arrow_forward: **Playing:** **" + ti.title + "** by **" + ti.author + "** in " + playingIn.getName() + ".").submit();
            }
            else
            {
                tc.sendMessage(":arrow_forward: **Playing:** **" + ti.title + "** by **" + ti.author + "** in " + playingIn.getName() + ". Length: " + ColonTimeParser.fromMillis(ti.length)).submit();
            }

            var songs = AudioQueueManager.howManyTracks(g);
            var pages = AudioQueueManager.howManyPages(g);

            if (songs == 0)
            {
                tc.sendMessage(String.format("There are no songs in the queue right now.", pages)).submit();
                return;
            }

            if (page > pages)
            {
                tc.sendMessage(String.format("Error: There are not that many pages at the moment, currently there are %d pages.", pages)).submit();
                return;
            }

            var view = AudioQueueManager.getPaginatedView(g, page);

            var eb = new EmbedBuilder();
            eb.setTitle("Queue, page " + page + "/" + pages);
            eb.setAuthor("Botdiril Music Player", "https://github.com/493msi/botdiril", tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
            eb.setDescription(String.format("There are %d track(s) in %d page(s).", songs, pages));
            eb.setColor(Color.decode("0x0099ff"));

            var i = 0;

            for (AudioTrack track : view)
            {
                ++i;
                var trackInfo = track.getInfo();

                if (trackInfo.length == Long.MAX_VALUE)
                {
                    eb.addField(i + ". " + trackInfo.title, "by **" + trackInfo.author + "**", false);
                }
                else
                {
                    eb.addField(i + ". " + trackInfo.title, "by **" + trackInfo.author + "**\nlength **" + ColonTimeParser.fromMillis(trackInfo.length) + "**", false);
                }
            }

            tc.sendMessage(eb.build()).submit();
        }
    }

    public static void shuffle(TextChannel tc, VoiceChannel vc)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            tc.sendMessage("Error: The music bot is not playing.").submit();
        }
        else
        {
            var playingIn = getVoiceChannel(g);
            var printC = getPrintChannel(g);

            if (!tc.equals(printC))
            {
                tc.sendMessage(String.format("Error: The music bot is currently being controlled from %s.", printC.getAsMention())).submit();
                return;
            }

            if (!playingIn.equals(vc))
            {
                tc.sendMessage(String.format("Error: The music bot is currenctly playing in %s.", playingIn.getName())).submit();
                return;
            }

            if (AudioQueueManager.shuffle(g))
            {
                tc.sendMessage(":arrows_counterclockwise: **Shuffled the queue.**").submit();
            }
            else
            {
                tc.sendMessage(":asterisk: **I can't shuffle the queue right now.**").submit();
            }
        }
    }
}
