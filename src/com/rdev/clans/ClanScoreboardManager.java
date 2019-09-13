package com.rdev.clans;

import com.rdev.ParlaClans;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ClanScoreboardManager {

    @Getter private Scoreboard scoreboard;

    public void setupScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        ParlaClans.getInstance().getClansManager().getClans().forEach(clan -> {
            Team team = this.scoreboard.registerNewTeam(clan.getConfigurationID());
            ChatColor clanColor = clan.getClanChatColor();
            team.setColor(clanColor);
            team.setPrefix(clanColor + "[" + clan.getName() + "] ");
            team.setAllowFriendlyFire(false);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        });
    }

    public void addPlayer(Player p, Clan clan) {
        if (this.scoreboard != null) {
            p.setScoreboard(this.scoreboard);
            scoreboard.getTeam(clan.getConfigurationID()).addEntry(p.getName());
        }
    }

    public void removePlayer(Player p, Clan clan) {
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        scoreboard.getTeam(clan.getConfigurationID()).removeEntry(p.getName());
    }
}
