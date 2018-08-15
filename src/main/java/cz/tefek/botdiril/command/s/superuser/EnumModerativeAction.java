package cz.tefek.botdiril.command.s.superuser;

public enum EnumModerativeAction
{
    BAN(1, "User Banned"),
    KICK(2, "User Kicked"),
    MUTE(3, "User Muted"),
    MESSAGE_DELETE(4, "Message Deleted"),
    DISABLE_ROOM(5, "Room Interaction Disabled"),
    ENABLE_ROOM(6, "Room Interaction Enabled"),
    ECHO(7, "Echo Command"),
    RENAME(8, "User Renamed"),
    ROLE_ADD(9, "User's Role Added"),
    ROLE_REMOVE(10, "User's Role Removed"),
    EMOTE_LIST(11, "Emotes Listed");

    private int ID;
    private String desc;

    private EnumModerativeAction(int id, String description)
    {
        this.ID = id;
        this.desc = description;
    }

    public int getID()
    {
        return ID;
    }

    public String getDescription()
    {
        return desc;
    }
}
