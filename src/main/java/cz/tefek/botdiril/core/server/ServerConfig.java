package cz.tefek.botdiril.core.server;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import cz.tefek.botdiril.BotMain;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class ServerConfig
{
    private final long id;

    private long printChannelID_internal = -1;
    private String prefix;
    private UUID uuid;
    private int volume = 50;
    private Set<Long> superUseredRoles = new CopyOnWriteArraySet<>();

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

    public void addAllSuperUserRoles(List<Long> roles)
    {
        superUseredRoles.addAll(roles);
    }

    public boolean doesRoleHaveSuperUser(Role r)
    {
        return this.superUseredRoles.contains(r.getIdLong());
    }

    public boolean addSuperUserRole(Role r)
    {
        return this.superUseredRoles.add(r.getIdLong());
    }

    public Set<Long> getAllSuperUseredRoles()
    {
        return superUseredRoles;
    }

    public long isPunished(TextChannel channel, Member m)
    {
        return -1;
    }

    public void setReportChannel(long reportChannelID)
    {
        this.printChannelID_internal = reportChannelID;
    }

    public boolean hasReportChannel()
    {
        return printChannelID_internal != -1 && BotMain.getBotdiril().getJDA().getGuildById(this.id).getTextChannelById(printChannelID_internal) != null;

    }

    public TextChannel getReportChannel(Guild g)
    {
        return g.getTextChannelById(printChannelID_internal);
    }

    public long getReportChannelID()
    {
        return printChannelID_internal;
    }
}
