package cz.tefek.botdiril.command.s;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cz.tefek.botdiril.command.Command;
import cz.tefek.botdiril.command.CommandCathegory;
import cz.tefek.botdiril.userdata.UserInventory;
import cz.tefek.botdiril.userdata.UserStorage;
import cz.tefek.botdiril.userdata.items.Item;
import net.dv8tion.jda.core.entities.Message;

public class CommandRich implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("rich", "richest", "sellouts");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var users = UserStorage.getAllUsers().values().stream().sorted(Comparator.comparing(UserInventory::getCoins).reversed()).collect(Collectors.toList());

        var sb = new StringBuilder("**Top 10 richest users (globally):**\n");

        for (int i = 0; i < 10; i++)
        {
            if (i + 1 > users.size())
                break;

            var uid = users.get(i).getUserID();
            var uname = message.getJDA().getUserById(uid);
            var rname = uname == null ? "[unknown]" : uname.getName();
            sb.append((i + 1) + ". **" + rname + "** with " + users.get(i).getCoins() + Item.COINDIRIL + "s\n");
        }

        message.getTextChannel().sendMessage(sb.toString()).submit();
    }

    @Override
    public String usage()
    {
        return "rich";
    }

    @Override
    public String description()
    {
        return "Prints the top 10 richest users.";
    }

    @Override
    public boolean canRunWithoutArguments()
    {
        return true;
    }

    @Override
    public CommandCathegory getCathegory()
    {
        return CommandCathegory.INVENTORY;
    }
}
