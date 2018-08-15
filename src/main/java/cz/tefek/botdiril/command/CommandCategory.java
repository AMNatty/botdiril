package cz.tefek.botdiril.command;

public enum CommandCategory
{
    GENERAL("General commands"), ECONOMY("Currency commands"), ITEMS("Inventory commands"), GAMBLING("Gambling commands"), MUSIC("Music bot commands"), ADMINISTRATIVE("Administrative commands"), INTERACTIVE("Interactive commands"), SUPERUSER("Superuser commands"), LEAGUE("League of Legends based commands");

    private String name;

    private CommandCategory(String humanReadableName)
    {
        this.name = humanReadableName;
    }

    public String getName()
    {
        return name;
    }
}
