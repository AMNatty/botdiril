package cz.tefek.botdiril.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cz.tefek.botdiril.command.s.superuser.SuperUserCommandBase;
import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.core.ServerPreferences;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CommandInterpreter
{
    public static List<Command> commands = new ArrayList<>();
    private static Map<String, Command> commandAliases_internal = new HashMap<>();

    public static void initialize()
    {
        var cathegories = Arrays.stream(CommandCategory.values()).map(cat -> cat.toString().toLowerCase()).collect(Collectors.toList());

        // Lock the command list
        commands = Collections.unmodifiableList(commands);

        commands.forEach(c -> {
            c.getAliases().forEach(alias -> {
                if (cathegories.contains(alias))
                {
                    System.err.println("Cathegory conflict! Yes, cathegories can conflict with commands. Alias: " + alias);
                }
                else if (commandAliases_internal.containsKey(alias))
                {
                    System.err.println("Command conflict! Alias: " + alias);
                }
                else
                {
                    commandAliases_internal.put(alias, c);
                    System.out.println("Initialized command: " + alias);
                }
            });
        });
    }

    public static Command getCommandByAlias(String alias)
    {
        return commandAliases_internal.get(alias);
    }

    public static void interpretCommand(Message message, String commandRaw)
    {
        var channel = message.getTextChannel();
        var guild = channel.getGuild();
        var sc = ServerPreferences.getServerByID(guild.getIdLong());
        var serverPrefix = sc.getPrefix();

        if (!commandRaw.isEmpty())
        {
            commandRaw = commandRaw.trim();

            if (!commandRaw.contains(" "))
            {
                var command = commandAliases_internal.get(commandRaw.toLowerCase());

                if (command != null)
                {
                    if (!BotdirilConfig.isSuperUser(guild, message.getAuthor()) && command instanceof SuperUserCommandBase)
                    {
                        channel.sendMessage("You are not allowed to use superuser commands.").submit();
                        return;
                    }

                    if (command.canRunWithoutArguments())
                    {
                        command.interpret(message, new Object[0]);
                    }
                    else
                    {
                        channel.sendMessage("Not enough arguments! Usage: `" + serverPrefix + command.usage() + "`").submit();
                    }
                }

                return;
            }

            var uncutArgs = commandRaw.substring(commandRaw.indexOf(" ")).trim();

            var commandName = commandRaw.substring(0, commandRaw.indexOf(" ")).toLowerCase();

            var command = commandAliases_internal.get(commandName);

            if (command != null)
            {
                if (!BotdirilConfig.isSuperUser(guild, message.getAuthor()) && command instanceof SuperUserCommandBase)
                {
                    channel.sendMessage("You are not allowed to use superuser commands.").submit();
                    return;
                }

                if (command.hasOpenEnd())
                {
                    var cutArgs = uncutArgs;

                    int parCounter = 0;

                    Object[] arguments = new Object[command.getArgumentTypes().length];

                    while (parCounter < command.getArgumentTypes().length)
                    {
                        if (parCounter == command.getArgumentTypes().length - 1)
                        {
                            var val = getValue(cutArgs, command.getArgumentTypes()[parCounter]);

                            if (val == null)
                            {
                                channel.sendMessage("Invalid arguments! Usage: `" + serverPrefix + command.usage() + "`");
                                break;
                            }
                            else
                            {
                                arguments[parCounter] = val;
                                command.interpret(message, arguments);
                                break;
                            }
                        }
                        else
                        {
                            var arg = nextArg(cutArgs);
                            cutArgs = cutArgs.substring(arg.length() + 1);
                            var val = getValue(arg, command.getArgumentTypes()[parCounter++]);

                            if (val == null)
                            {
                                channel.sendMessage("Invalid arguments! Usage: `" + serverPrefix + command.usage() + "`");
                                break;
                            }
                            else
                            {
                                arguments[parCounter] = val;
                            }
                        }

                        parCounter++;
                    }
                }
                else if (!command.hasCustomParser())
                {
                    var inputArgs = Arrays.asList(uncutArgs.split("\\s+"));
                    var argReq = Arrays.asList(command.getArgumentTypes());
                    var outArgs = new Object[command.getArgumentTypes().length];

                    if (argReq.size() != inputArgs.size())
                    {
                        channel.sendMessage("Wrong number of arguments! Usage: `" + serverPrefix + command.usage() + "`").submit();
                    }
                    else
                    {
                        for (var iterator = argReq.listIterator(); iterator.hasNext();)
                        {
                            int rqIndex = iterator.nextIndex();
                            var clazz = iterator.next();
                            var inputArg = inputArgs.get(rqIndex);

                            if ((clazz == Long.class || clazz == long.class) && isLong(inputArg))
                            {
                                outArgs[rqIndex] = Long.parseLong(inputArg);
                            }
                            else if ((clazz == Integer.class || clazz == int.class) && isInt(inputArg))
                            {
                                outArgs[rqIndex] = Integer.parseInt(inputArg);
                            }
                            else if ((clazz == Double.class || clazz == double.class) && isDouble(inputArg))
                            {
                                outArgs[rqIndex] = Double.parseDouble(inputArg);
                            }
                            else if (clazz == Member.class)
                            {
                                try
                                {
                                    var matcher = Pattern.compile("[0-9]+").matcher(inputArg);

                                    if (matcher.find())
                                    {
                                        var id = Long.parseLong(matcher.group());
                                        var mtndMember = message.getGuild().getMemberById(id);

                                        if (mtndMember != null)
                                        {
                                            outArgs[rqIndex] = mtndMember;
                                        }
                                        else
                                        {
                                            throw new IllegalStateException();
                                        }
                                    }
                                    else
                                    {
                                        throw new IllegalStateException();
                                    }
                                }
                                catch (Exception e)
                                {
                                    channel.sendMessage("Mention/ID could not be parsed! Note that [@]everyone and [@]role don't work here.").submit();
                                    break;
                                }
                            }
                            else if (clazz == String.class)
                            {
                                outArgs[rqIndex] = inputArg;
                            }
                            else
                            {
                                channel.sendMessage("Illegal arguments! Usage: `" + serverPrefix + command.usage() + "`").submit();
                                break;
                            }

                            if (!iterator.hasNext())
                            {
                                command.interpret(message, outArgs);
                            }
                        }
                    }
                }
                else
                {
                    command.interpret(message, (Object[]) uncutArgs.split("\\s+"));
                }
            }
        }
    }

    private static String nextArg(String argRaw)
    {
        return argRaw.substring(0, argRaw.indexOf(" "));
    }

    private static Object getValue(String arg, Class<?> desiredType)
    {
        try
        {
            if (desiredType == long.class || desiredType == Long.class)
            {
                return Long.parseLong(arg);
            }
            else if (desiredType == int.class || desiredType == Integer.class)
            {
                return Integer.parseInt(arg);
            }
            else if (desiredType == double.class || desiredType == Double.class)
            {
                return Double.parseDouble(arg);
            }
            else if (desiredType == String.class)
            {
                return arg;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        return null;
    }

    private static boolean isDouble(String arg)
    {
        try
        {
            Double.parseDouble(arg);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean isInt(String arg)
    {
        try
        {
            Integer.parseInt(arg);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean isLong(String arg)
    {
        try
        {
            Long.parseLong(arg);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
