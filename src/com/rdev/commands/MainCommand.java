package com.rdev.commands;

import com.rdev.ParlaClans;
import com.rdev.clans.Clan;
import com.rdev.configuration.Constants;
import com.rdev.menu.MenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Constants.Messages.NOT_PLAYER_ERROR);
            return true;
        }

        Player p = (Player) commandSender;

        String subCommand;

        if (args.length == 0) {
            if (ParlaClans.getInstance().getClansManager().isStarted()) {
                p.sendMessage(Constants.Messages.PARLACLANS_CLOSED);
                return true;
            }

            Clan playerClan = ParlaClans.getInstance().getClansManager().getPlayersClan(p);

            if (playerClan != null) {
                p.sendMessage(Constants.Messages.ALREADY_IN_A_CLAN);
                return true;
            }

            MenuGUI.openChooseClanMenu(p);

            return true;
        }

        subCommand = args[0];

        switch (subCommand) {
            case "forceStart":
                if(!p.hasPermission("parlaclans.admin")) {
                    p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }
                if(ParlaClans.getInstance().getClansManager().isStarted()) {
                    p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + " ParlaClans system is already activated!");
                    break;
                }

                ParlaClans.getInstance().getClansManager().startClanWars();

                p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + " ParlaClans system is now activated!");
                break;
            case "forceStop":

                if(!p.hasPermission("parlaclans.admin")) {
                    p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }

                if(!ParlaClans.getInstance().getClansManager().isStarted()) {
                    p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + " ParlaClans system isn't started yet!");
                    break;
                }

                ParlaClans.getInstance().getClansManager().finishClanWars();

                Bukkit.broadcastMessage(Constants.Messages.PARLACLANS_FORCE_FINISH);

                p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + "ParlaClans system is now finished!");
                break;

        }
        return false;
    }
}
