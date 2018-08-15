package cz.tefek.botdiril.command;

import cz.tefek.botdiril.command.s.CommandBalance;
import cz.tefek.botdiril.command.s.CommandBuy;
import cz.tefek.botdiril.command.s.CommandChangelog;
import cz.tefek.botdiril.command.s.CommandCrate;
import cz.tefek.botdiril.command.s.CommandDaily;
import cz.tefek.botdiril.command.s.CommandDrawCard;
import cz.tefek.botdiril.command.s.CommandDupe;
import cz.tefek.botdiril.command.s.CommandEcho;
import cz.tefek.botdiril.command.s.CommandFullInventory;
import cz.tefek.botdiril.command.s.CommandGamble;
import cz.tefek.botdiril.command.s.CommandGive;
import cz.tefek.botdiril.command.s.CommandHelp;
import cz.tefek.botdiril.command.s.CommandInventory;
import cz.tefek.botdiril.command.s.CommandItemInfo;
import cz.tefek.botdiril.command.s.CommandJoinPos;
import cz.tefek.botdiril.command.s.CommandKek;
import cz.tefek.botdiril.command.s.CommandLoLItem;
import cz.tefek.botdiril.command.s.CommandLuckyStreak;
import cz.tefek.botdiril.command.s.CommandMine;
import cz.tefek.botdiril.command.s.CommandNameChange;
import cz.tefek.botdiril.command.s.CommandPayoutKeks;
import cz.tefek.botdiril.command.s.CommandRich;
import cz.tefek.botdiril.command.s.CommandSell;
import cz.tefek.botdiril.command.s.CommandSellCards;
import cz.tefek.botdiril.command.s.CommandShop;
import cz.tefek.botdiril.command.s.CommandUsage;
import cz.tefek.botdiril.command.s.music.CommandClearQueue;
import cz.tefek.botdiril.command.s.music.CommandLeave;
import cz.tefek.botdiril.command.s.music.CommandNowPlaying;
import cz.tefek.botdiril.command.s.music.CommandPause;
import cz.tefek.botdiril.command.s.music.CommandPlay;
import cz.tefek.botdiril.command.s.music.CommandQueue;
import cz.tefek.botdiril.command.s.music.CommandResume;
import cz.tefek.botdiril.command.s.music.CommandShuffle;
import cz.tefek.botdiril.command.s.music.CommandSkip;
import cz.tefek.botdiril.command.s.music.CommandStop;
import cz.tefek.botdiril.command.s.music.CommandVolume;
import cz.tefek.botdiril.command.s.superuser.CommandDisableHere;
import cz.tefek.botdiril.command.s.superuser.CommandListEmotes;
import cz.tefek.botdiril.command.s.superuser.CommandListSuperUsers;
import cz.tefek.botdiril.command.s.superuser.CommandMagic;

public class CommandInitializer
{
    public static void initialize()
    {
        // GENERAL
        CommandInterpreter.commands.add(new CommandEcho());
        CommandInterpreter.commands.add(new CommandUsage());
        CommandInterpreter.commands.add(new CommandHelp());
        CommandInterpreter.commands.add(new CommandChangelog());
        CommandInterpreter.commands.add(new CommandJoinPos());

        // INVENTORY, ECONOMY, GAMBLING
        CommandInterpreter.commands.add(new CommandDaily());
        CommandInterpreter.commands.add(new CommandShop());
        CommandInterpreter.commands.add(new CommandBuy());
        CommandInterpreter.commands.add(new CommandSell());
        CommandInterpreter.commands.add(new CommandBalance());
        CommandInterpreter.commands.add(new CommandInventory());
        CommandInterpreter.commands.add(new CommandFullInventory());
        CommandInterpreter.commands.add(new CommandGamble());
        CommandInterpreter.commands.add(new CommandMine());
        CommandInterpreter.commands.add(new CommandRich());
        CommandInterpreter.commands.add(new CommandGive());
        CommandInterpreter.commands.add(new CommandCrate());
        CommandInterpreter.commands.add(new CommandLuckyStreak());
        CommandInterpreter.commands.add(new CommandItemInfo());
        CommandInterpreter.commands.add(new CommandKek());
        CommandInterpreter.commands.add(new CommandDupe());
        CommandInterpreter.commands.add(new CommandPayoutKeks());
        CommandInterpreter.commands.add(new CommandSellCards());
        CommandInterpreter.commands.add(new CommandDrawCard());

        // LEAGUE
        CommandInterpreter.commands.add(new CommandLoLItem());

        // MUSIC
        CommandInterpreter.commands.add(new CommandPlay());
        CommandInterpreter.commands.add(new CommandPause());
        CommandInterpreter.commands.add(new CommandResume());
        CommandInterpreter.commands.add(new CommandLeave());
        CommandInterpreter.commands.add(new CommandSkip());
        CommandInterpreter.commands.add(new CommandVolume());
        CommandInterpreter.commands.add(new CommandNowPlaying());
        CommandInterpreter.commands.add(new CommandStop());
        CommandInterpreter.commands.add(new CommandClearQueue());
        CommandInterpreter.commands.add(new CommandQueue());
        CommandInterpreter.commands.add(new CommandShuffle());

        // ADMINISTRATION
        CommandInterpreter.commands.add(new CommandNameChange());

        // SUPERUSER
        CommandInterpreter.commands.add(new CommandListSuperUsers());
        CommandInterpreter.commands.add(new CommandDisableHere());
        CommandInterpreter.commands.add(new CommandMagic());
        CommandInterpreter.commands.add(new CommandListEmotes());

        CommandInterpreter.initialize();
    }
}
