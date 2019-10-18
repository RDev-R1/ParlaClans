package com.rdev.clans;

import com.rdev.ParlaClans;
import com.rdev.configuration.Constants;
import com.rdev.exceptions.ClanFullException;
import com.rdev.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClansManager {

    private int date = 0;
    @Getter private final List<Clan> clans = new ArrayList<>();
    @Getter private final Map<String, Integer> playerDeaths = new HashMap<>();
    @Getter @Setter private boolean started = false;
    @Getter @Setter private long timestamp = 0;

    public Clan getClanByName(String clanName) {
        return clans.stream().filter(clan -> clan.getName().equals(clanName)).findFirst().orElse(null);
    }

    public Clan getPlayersClan(Player p) {
        Clan playerClan = null;
        for (Clan clan : this.clans) {
            boolean inThisClan = clan.getMembers().stream().anyMatch(uuid -> uuid.equals(p.getUniqueId().toString()));
            if (inThisClan) {
                playerClan = clan;
                break;
            }
        }
        return playerClan;
    }

    public void startClanWars() {
        this.started = true;
        ParlaClans.getInstance().getConfig().set("Season.timestamp", System.currentTimeMillis());
        ParlaClans.getInstance().getConfig().set("Season.started", true);
        ParlaClans.getInstance().saveConfig();

        ParlaClans.getInstance().getClanScoreboardManager().setupScoreboard();
        Bukkit.getOnlinePlayers().forEach(player -> {
            Clan clan = getPlayersClan(player);
            ParlaClans.getInstance().getClanScoreboardManager().addPlayer(player, clan);
        });

    }

    public void finishClanWars() {
        this.started = false;
        ParlaClans.getInstance().getConfigurationManager().createSeasonRewardsFile(selectWinner());
        ParlaClans.getInstance().getConfig().set("Season.timestamp", 0);
        ParlaClans.getInstance().getConfig().set("Season.started", false);
        ParlaClans.getInstance().saveConfig();

        clans.forEach(clan ->
                Bukkit.getOnlinePlayers().forEach(player -> ParlaClans.getInstance().getClanScoreboardManager().removePlayer(player, clan))
        );
        ParlaClans.getInstance().getClanScoreboardManager().setupScoreboard();

        this.clearClans();

        int season = ParlaClans.getInstance().getConfig().getInt("Season.number");

        giveRewards(season);

        File seasonFile = new File(ParlaClans.getInstance().getDataFolder() + "/seasons", "Season-"+ season + "-summary.yml");
        YamlConfiguration con = YamlConfiguration.loadConfiguration(seasonFile);

        String winner = con.getString("Winner.name");

        if(ParlaClans.getInstance().getWinnerBossBar() == null) {
            BossBarClanWinner bossBarClanWinner = new BossBarClanWinner(con.getString("Winner.name"));
            bossBarClanWinner.sendToAll();
            ParlaClans.getInstance().setWinnerBossBar(bossBarClanWinner);
        } else {
            ParlaClans.getInstance().getWinnerBossBar().getBossBar().setTitle(Constants.PluginSettings.BOSSBAR_MESSAGE.replace("%clan%", winner));
        }


    }

    private void clearClans() {
        clans.forEach(clan -> {
            clan.getMembers().clear();
            clan.setPoints(0);
            ParlaClans.getInstance().getClanScoreboardManager().getScoreboard().resetScores(clan.getScoreboardScore().getEntry());
            clan.setScoreboardScore(null);
            new File(ParlaClans.getInstance().getDataFolder(), clan.getConfigurationID() + ".yml").delete();
        });
    }

    public void movePlayerClan(Player p, Clan oldClan, Clan newClan) throws ClanFullException {
        removeMember(p.getUniqueId().toString(), oldClan);
        addMember(p, newClan);
    }

    public boolean addMember(Player p, Clan clan) throws ClanFullException {
        if (clan.getMembers().contains(p.getUniqueId().toString())) {
            return false;
        } else {
            if (!canJoin(clan)) throw new ClanFullException(Constants.Messages.CLAN_FULL);
            clan.getMembers().add(p.getUniqueId().toString());
            ParlaClans.getInstance().getConfigurationManager().addMemberToConfiguration(p.getUniqueId().toString(), clan);

            if (this.started) ParlaClans.getInstance().getClanScoreboardManager().addPlayer(p, clan);
            return true;
        }
    }

    public boolean removeMember(String uuid, Clan clan) {
        if (clan.getMembers().contains(uuid)) {
            clan.getMembers().remove(uuid);
            ParlaClans.getInstance().getConfigurationManager().removeMemberFromConfiguration(uuid, clan);
            return true;
        } else return false;  //should no happen
    }

    public void timeCheckerSetup() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (date != TimeUtils.getDateDay()) {
                    playerDeaths.clear();
                }
                date = TimeUtils.getDateDay();

                Bukkit.getOnlinePlayers().forEach(p -> {
                    Clan clan = getPlayersClan(p);
                    if(clan == null) return;

                    if (p.getPlayerTime() + TimeUtils.HOUR > System.currentTimeMillis()) {
                        clan.setPoints(clan.getPoints() + Constants.PluginSettings.HOUR_POINTS);
                        ParlaClans.getInstance().getClanScoreboardManager().updateScoreboard();
                        p.resetPlayerTime();
                    }
                });

                ParlaClans.getInstance().getClanScoreboardManager().updateScoreboard();
            }
        }.runTaskTimer(ParlaClans.getInstance(), 200, 200);
    }

    private void giveRewards(int season) {
        File[] files = new File(ParlaClans.getInstance().getDataFolder() + "/seasons").listFiles();
        if (files == null) return;

        File file = new File(ParlaClans.getInstance().getDataFolder() + "/seasons", "Season-"+ season + "-summary.yml");

        YamlConfiguration con = YamlConfiguration.loadConfiguration(file);
        List<String> members = con.getStringList("RewardedMembers");

        if(members.size() == 0) return;

        Bukkit.getOnlinePlayers().stream().filter(p -> members.contains(p.getUniqueId().toString()))
                .forEach(p -> {
                    con.getStringList("Rewards").forEach(command ->
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                                    command.replaceAll("%player%", p.getName())));

                    members.remove(p.getUniqueId().toString());
                    con.set("RewardedMembers", members);
        });

        try { con.save(file); } catch (IOException ignore) {}
    }

    private boolean canJoin(Clan clan) {
        List<Clan> ordered = new ArrayList<>(clans);
        Collections.sort(ordered); //, Collections.reverseOrder()

        if (!clan.getName().equals(ordered.get(0).getName())) {
            return ((clan.getMembers().size() - ordered.get(0).getMembers().size()) < Constants.PluginSettings.MAX_PLAYERS_DIFFERENCE);
        }
        return true;

    }
    
    private Clan selectWinner() {
        AtomicReference<Clan> winner = new AtomicReference<>(clans.get(0));
        clans.forEach(clan -> {
            if (clan.getPoints() > winner.get().getPoints()) winner.set(clan);
        });
        return winner.get();
    }

}
