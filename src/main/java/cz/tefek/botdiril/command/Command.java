package cz.tefek.botdiril.command;

import java.util.List;

import net.dv8tion.jda.core.entities.Message;

public interface Command
{
    public Class<?>[] getArgumentTypes();

    public List<String> getAliases();

    public void interpret(Message message, Object... params);

    public String usage();

    public String description();

    public CommandCathegory getCathegory();

    public default boolean hasOpenEnd()
    {
        return false;
    }

    public default boolean hasCustomParser()
    {
        return false;
    }

    public boolean canRunWithoutArguments();
}
