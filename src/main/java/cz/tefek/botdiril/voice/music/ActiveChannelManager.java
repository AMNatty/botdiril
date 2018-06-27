package cz.tefek.botdiril.voice.music;

import java.util.concurrent.ConcurrentHashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import cz.tefek.botdiril.core.ServerPreferences;
import cz.tefek.botdiril.voice.AudioHandler;
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

    public static void search(TextChannel tc, VoiceChannel vc, String input)
    {
        var g = tc.getGuild();

        if (!isPlaying(g))
        {
            startPlaying(tc, vc);
        }
        else
        {
            var playingIn = vcs.get(g.getIdLong());
            var printC = printChannel.get(g.getIdLong());

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

    public static void stop(Guild guild, TextChannel tc)
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
            var playingIn = vcs.get(g.getIdLong());
            var printC = printChannel.get(g.getIdLong());

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
            var printC = printChannel.get(g.getIdLong());

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
            var playingIn = vcs.get(g.getIdLong());
            var printC = printChannel.get(g.getIdLong());

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
}
