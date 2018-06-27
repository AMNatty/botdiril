package cz.tefek.botdiril.voice.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import cz.tefek.util.ColonTimeParser;
import net.dv8tion.jda.core.entities.Guild;

public class TrackHandler extends AudioEventAdapter
{
    private Guild guild;

    public TrackHandler(Guild guild)
    {
        this.guild = guild;
    }

    @Override
    public void onPlayerPause(AudioPlayer player)
    {
        var tc = ActiveChannelManager.getPrintChannel(guild);
        tc.sendMessage(":pause_button: **Paused.**").submit();
    }

    @Override
    public void onPlayerResume(AudioPlayer player)
    {
        var tc = ActiveChannelManager.getPrintChannel(guild);
        tc.sendMessage(":play_pause: **Unpaused.**").submit();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        var tc = ActiveChannelManager.getPrintChannel(guild);

        var ti = track.getInfo();

        if (ti.length == Long.MAX_VALUE)
        {
            tc.sendMessage(":arrow_forward: **Now Playing:** " + ti.title + " by " + ti.author + ".").submit();
        }
        else
        {
            tc.sendMessage(":arrow_forward: **Now Playing:** " + ti.title + " by " + ti.author + ". Length: " + ColonTimeParser.fromMillis(ti.length)).submit();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext)
        {
            var at = AudioQueueManager.pollQueue(this.guild.getIdLong());

            if (at != null)
            {
                player.playTrack(at);
            }
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext
        // = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not
        // finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you
        // can put a
        // clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        // An already playing track threw an exception (track end event will still be
        // received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        // Audio track has been unable to provide us any audio, might want to just start
        // a new track
    }
}
