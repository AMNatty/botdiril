package cz.tefek.botdiril.command.s.superuser;

import java.util.List;

import net.dv8tion.jda.core.entities.Message;

public class CommandGiveaway extends SuperUserCommandBase
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return null;
    }

    @Override
    public List<String> getAliases()
    {
        return null;
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String usage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String description()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
