package cz.tefek.botdiril.voice.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;

public class BAudioLoadResultHandler implements AudioLoadResultHandler
{
    private Guild gid;
    private String search;

    public BAudioLoadResultHandler(Guild guild, String input)
    {
        this.gid = guild;
        this.search = input;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        AudioQueueManager.enqueue(gid, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        if (playlist.isSearchResult())
        {
            AudioQueueManager.enqueue(gid, playlist.getTracks().get(0));
        }
    }

    @Override
    public void noMatches()
    {
        if (!search.startsWith("ytsearch:"))
        {
            ActiveChannelManager.getAudioPlayerManager(gid).loadItem("ytsearch:" + search, this);
        }
        else
        {
            ActiveChannelManager.getPrintChannel(gid).sendMessage("I couldn't find anything like that!").submit();
        }
    }

    @Override
    public void loadFailed(FriendlyException exception)
    {
        exception.printStackTrace();
        ActiveChannelManager.getPrintChannel(gid).sendMessage("Error: " + exception.getMessage()).submit();
    }
}
