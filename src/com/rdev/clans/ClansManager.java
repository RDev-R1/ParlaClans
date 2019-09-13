package com.rdev.clans;

import com.mysql.jdbc.TimeUtil;
import com.rdev.ParlaClans;
import com.rdev.configuration.Constants;
import com.rdev.exceptions.ClanFullException;
import com.rdev.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClansManager {

    private int date = 0;
    @Getter private final List<Clan> clans = new ArrayList<>();
    @Getter private final Map<String, Integer> playerDeaths = new HashMap<String, Integer>();
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
        clans.forEach(clan ->
                Bukkit.getOnlinePlayers().forEach(player -> ParlaClans.getInstance().getClanScoreboardManager().addPlayer(player, clan))
        );
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

        this.clearClansMembers();
    }

    private void clearClansMembers() {
        clans.forEach(clan -> {
            clan.getMembers().clear();
            new File(ParlaClans.getInstance().getDataFolder(), clan.getConfigurationID() + ".yml").delete();
        });
    }

    public boolean addMember(Player p, Clan clan) throws ClanFullException {
        if (clan.getMembers().contains(p.getUniqueId().toString())) {
            return false;
        } else {
            if (!canJoin(clan)) throw new ClanFullException(Constants.Messages.CLAN_FULL);
            clan.getMembers().add(p.getUniqueId().toString());
            ParlaClans.getInstance().getClanScoreboardManager().addPlayer(p, clan);
            ParlaClans.getInstance().getConfigurationManager().addMemberToConfiguration(p.getUniqueId().toString(), clan);
            return true;
        }
    }

    public boolean removeMember(String uuid, Clan clan, boolean force) {
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
                        p.resetPlayerTime();
                    }
                });
            }
        }.runTaskTimer(ParlaClans.getInstance(), 200, 200);
    }

    private boolean canJoin(Clan clan) {
        List<Clan> ordered = new ArrayList<>(clans);
        Collections.sort(ordered); //, Collections.reverseOrder()

        if (!clan.getName().equals(ordered.get(0).getName())) {
            return ((clan.getMembers().size() - ordered.get(0).getMembers().size()) <= Constants.PluginSettings.MAX_PLAYERS_DEFERENCE);
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
