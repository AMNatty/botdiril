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

public class CommandKek implements Command
{
    @Override
    public Class<?>[] getArgumentTypes()
    {
        return new Class<?>[0];
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("kek", "kekest", "richkeks");
    }

    @Override
    public void interpret(Message message, Object... params)
    {
        var comp = Comparator.comparingLong((UserInventory ui) -> ui.howManyOf(Item.getByID("kek"))).reversed();
        var users = UserStorage.getAllUsers().values().stream().sorted(comp).limit(10).collect(Collectors.toList());

        var sb = new StringBuilder("**Top 10 " + Item.KEK + "est users (globally):**\n");

        var i = 0;

        for (var u : users)
        {
            i++;
            var uid = u.getUserID();
            var uname = message.getJDA().getUserById(uid);
            var rname = uname == null ? "[unknown]" : uname.getName();
            sb.append(i + ". **" + rname + "** with " + u.howManyOf(Item.getByID("kek")) + Item.KEK + "s\n");
        }

        message.getTextChannel().sendMessage(sb.toString()).submit();
    }

    @Override
    public String usage()
    {
        return "kek";
    }

    @Override
    public String description()
    {
        return "Prints the top 10 kekest users.";
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
