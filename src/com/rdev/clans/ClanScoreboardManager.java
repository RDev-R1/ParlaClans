package com.rdev.clans;

import com.rdev.ParlaClans;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public class ClanScoreboardManager {

    private final String SCOREBOARD_NAME = "ParlaClans";

    @Getter private Scoreboard scoreboard;
    private Objective objective;

    public void setupScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective(SCOREBOARD_NAME, "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.objective = obj;

        updateScoreboard();

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
            if (clan != null) scoreboard.getTeam(clan.getConfigurationID()).addEntry(p.getName());
        }
    }

    public void removePlayer(Player p, Clan clan) {
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        if (this.scoreboard == null) return;

        if (scoreboard.getTeam(clan.getConfigurationID()).hasEntry(p.getName()))
            scoreboard.getTeam(clan.getConfigurationID()).removeEntry(p.getName());
    }

    public void updateScoreboard() {
        if(this.scoreboard == null || objective == null) return;

        int pos = ParlaClans.getInstance().getClansManager().getClans().size();

        for (Clan clan : ParlaClans.getInstance().getClansManager().getClans()) {
            Score score = null;
            if (clan.getScoreboardScore() != null) {
                scoreboard.resetScores(clan.getScoreboardScore().getEntry());
            }
            score = objective.getScore(clan.getClanChatColor() + "" + clan.getName() + ": " + clan.getPoints());

            clan.setScoreboardScore(score);
            score.setScore(pos);
            pos--;
        }
    }
}
