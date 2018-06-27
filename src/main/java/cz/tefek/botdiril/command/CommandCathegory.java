package cz.tefek.botdiril.command;

public enum CommandCathegory
{
    GENERAL("General commands"),
    ECONOMY("Currency and gambling commands"),
    MUSIC("Music bot commands"),
    ADMINISTRATIVE("Administrative commands"),
    INTERACTIVE("Interactive commands"),
    LEAGUE("League of Legends based commands");

    private String name;

    private CommandCathegory(String humanReadableName)
    {
        this.name = humanReadableName;
    }

    public String getName()
    {
        return name;
    }
}
