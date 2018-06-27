package cz.tefek.botdiril.core.server;

import java.util.UUID;

public class ServerConfig
{
    private final long id;
    private String prefix;
    private UUID uuid;
    private int volume = 50;

    public ServerConfig(long id)
    {
        this.id = id;
        this.uuid = UUID.randomUUID();
    }

    public ServerConfig(long id, UUID uuid, String prefix)
    {
        this.id = id;
        this.uuid = uuid;
        this.prefix = prefix;
    }

    public long getID()
    {
        return id;
    }

    public boolean isInstalled()
    {
        return prefix != null;
    }

    public void updatePrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setVolume(int vol)
    {
        this.volume = vol;
    }

    public int getVolume()
    {
        return this.volume;
    }
}
