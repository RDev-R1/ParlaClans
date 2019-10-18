package com.rdev.commands;

import com.rdev.ParlaClans;
import com.rdev.clans.Clan;
import com.rdev.clans.ClansManager;
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

            MenuGUI.openChooseClanMenu(p);

            return true;
        }

        subCommand = args[0];

        if (!p.hasPermission("parlaclans.admin")) {
            p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (subCommand.equalsIgnoreCase("forceStart")) {
            if (ParlaClans.getInstance().getClansManager().isStarted()) {
                p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + " ParlaClans system is already activated!");
                return true;
            }

            ParlaClans.getInstance().getClansManager().startClanWars();

            p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + " ParlaClans system is now activated!");
        }

        else if (subCommand.equalsIgnoreCase("forceStop")) {

            if (!ParlaClans.getInstance().getClansManager().isStarted()) {
                p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + " ParlaClans system isn't started yet!");
                return true;
            }

            ParlaClans.getInstance().getClansManager().finishClanWars();

            Bukkit.broadcastMessage(Constants.Messages.PARLACLANS_FORCE_FINISH);

            p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + "ParlaClans system is now finished!");
        }

        else if (subCommand.equalsIgnoreCase("stats")) {
            if (!p.hasPermission("parlaclans.admin")) {
                p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            ParlaClans.getInstance().getClansManager().getClans().forEach(clan -> {
                p.sendMessage(clan.getName() + " members: " + clan.getMembers().toString());
                p.sendMessage(clan.getName() + " points: " + clan.getPoints());
            });
        }

        else if (subCommand.equalsIgnoreCase("removeplayer")) {

            if (args.length != 2) return true;

            Player t = Bukkit.getPlayer(args[1]);

            if (t == null) {
                p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED +"There's no online player with that name");
                return true;
            }

            Clan clan = ParlaClans.getInstance().getClansManager().getPlayersClan(p);

            if (clan == null) {
                p.sendMessage(Constants.Messages.PREFIX + ChatColor.RED + "This player isn't in a clan!");
                return true;
            }

            ParlaClans.getInstance().getClansManager().removeMember(p.getUniqueId().toString(), clan);
            p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + "The player has been removed from his clan!");
        }
        else if (subCommand.equalsIgnoreCase("test")) {

            if (args.length != 1) return true;

            Clan clan = ParlaClans.getInstance().getClansManager().getPlayersClan(p);

            if (clan == null) {
                p.sendMessage("You are not in a clan!");
                return true;
            }

            clan.setPoints(clan.getPoints() + Constants.PluginSettings.KILL_POINTS);
            ParlaClans.getInstance().getClanScoreboardManager().updateScoreboard();
            p.sendMessage(Constants.Messages.PREFIX + ChatColor.GREEN + "Add Points" + Constants.PluginSettings.KILL_POINTS);
        }

        else if (subCommand.equalsIgnoreCase("test2")) {

            if (args.length != 1) return true;

            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }

        return false;
    }
}
