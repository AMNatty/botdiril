package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.List;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserStorage;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.HierarchyException;

public class CommandNameChange implements Command
{

    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[] { String.class };
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("namechange", "changename", "nicknamechange", "changenickname", "changenick");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var ui = UserStorage.getByID(message.getAuthor().getIdLong());
        var name = (String) params[0];
        name = name.replaceAll("\\p{Cntrl}", "");

        if (name.length() == 0)
        {
            message.getTextChannel().sendMessage("Your name cannot be empty!").submit();
        }
        else if (name.length() > 32)
        {
            message.getTextChannel().sendMessage("Your name cannot be longer than 32 charaters!").submit();
        }
        else
        {
            var t = ui.useTimer("name-change", 1000L * 60 * 60 * 24 * 30);

            if (t == -1)
            {
                try
                {
                    message.getGuild().getController().setNickname(message.getGuild().getMember(message.getAuthor()), name).submit();
                    message.getTextChannel().sendMessage("Your nickname was successfully changed.").submit();
                }
                catch (HierarchyException e)
                {
                    message.getTextChannel().sendMessage("I can't change your nickname when your role is above me.").submit();
                    ui.resetTimer("name-change");
                }
            }
            else
            {
                t /= 1000;

                final int spd = 60 * 60 * 24;
                final int sph = 60 * 60;
                final int spm = 60;

                int tdays = (int) (t / spd);
                int thours = (int) (t % spd / sph);
                int tminutes = (int) (t % spm / 60);
                int tseconds = (int) (t % 60);

                message.getTextChannel().sendMessage(String.format("You still need to wait %d days %d hours %d minutes %d seconds to change your nickname.", tdays, thours, tminutes, tseconds)).submit();
            }
        }

    }

    @Override
    public String usage()
    {
        return "namechange <new name>";
    }

    @Override
    public String description()
    {
        return "Change **your** nickname. (1 month cooldown)";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return false;
    }

    @Override
    public boolean hasOpenEnd()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.ADMINISTRATIVE;
    }
}
