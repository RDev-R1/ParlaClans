package com.rdev.listeners;

import com.rdev.ParlaClans;
import com.rdev.clans.BossBarClanWinner;
import com.rdev.clans.Clan;
import com.rdev.configuration.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ClansListeners implements Listener {

    @EventHandler
    public void playerKill(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if (p.getKiller() == null || p.getKiller().getUniqueId().equals(p.getUniqueId())) return;

        Map<String, Integer> deaths = ParlaClans.getInstance().getClansManager().getPlayerDeaths();

        if (deaths.containsKey(p.getUniqueId().toString())) {

            if (deaths.get(p.getUniqueId().toString()) < Constants.PluginSettings.MAX_PLAYERS_DEFERENCE) {
                Clan killerClan = ParlaClans.getInstance().getClansManager().getPlayersClan(p.getKiller());
                killerClan.setPoints(killerClan.getPoints() + Constants.PluginSettings.KILL_POINTS);
            }

            ParlaClans.getInstance().getClansManager().getPlayerDeaths().put(p.getUniqueId().toString(), deaths.get(p.getUniqueId().toString()) + 1);
        }
        else {
            ParlaClans.getInstance().getClansManager().getPlayerDeaths().put(p.getUniqueId().toString(), 1);
        }
    }

    @EventHandler
    public void damageBetweenMembers(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;

        Player damaged = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        Clan damagerClan = ParlaClans.getInstance().getClansManager().getPlayersClan(damager);
        Clan damagedClan = ParlaClans.getInstance().getClansManager().getPlayersClan(damaged);
        if(damagerClan == null || damagedClan == null) return;

        if(damagedClan.getName().equals(damagerClan.getName())) e.setCancelled(true);
    }

    @EventHandler
    public void giveRewards(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) return;

        File[] files = new File(ParlaClans.getInstance().getDataFolder() + "/seasons").listFiles();
        if (files == null) return;

        for(File file : files) {
            YamlConfiguration con = YamlConfiguration.loadConfiguration(file);
            List<String> members = con.getStringList("RewardedMembers");

            if(members.size() == 0) continue;

            if (members.contains(p.getUniqueId().toString())) {
                con.getStringList("Rewards").forEach(command ->
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                                command.replaceAll("%player%", p.getName())));

                members.remove(p.getUniqueId().toString());
                con.set("RewardedMembers", members);
                try { con.save(file); } catch (IOException ignore) {}

                break;
            }
        }
    }

    @EventHandler
    public void joinSystemLoading(PlayerJoinEvent e) {
        BossBarClanWinner bossBarClanWinner = ParlaClans.getInstance().getWinnerBossBar();

        if (bossBarClanWinner != null) {
            bossBarClanWinner.addPlayer(e.getPlayer());
        }

        Clan clan = ParlaClans.getInstance().getClansManager().getPlayersClan(e.getPlayer());
        if(clan != null) {
            ParlaClans.getInstance().getClanScoreboardManager().addPlayer(e.getPlayer(), clan);
        }
    }

    @EventHandler
    public void playerQuitSystem(PlayerQuitEvent e) {
        Clan playerClan = ParlaClans.getInstance().getClansManager().getPlayersClan(e.getPlayer());

        if(playerClan == null) {
            ParlaClans.getInstance().getClansManager().getPlayerDeaths().remove(e.getPlayer().getUniqueId().toString());
            ParlaClans.getInstance().getClanScoreboardManager().removePlayer(e.getPlayer(), playerClan);
        }
    }

    @EventHandler
    public void playerChatPrefix(AsyncPlayerChatEvent e) {
        if (!ParlaClans.getInstance().clansManager.isStarted()) return;

        Clan clan = ParlaClans.getInstance().getClansManager().getPlayersClan(e.getPlayer());

        if (clan == null) return;

        e.setFormat(clan.getClanChatColor() + "[" + clan.getName() + "] " + ChatColor.RESET + e.getFormat());
    }
}
