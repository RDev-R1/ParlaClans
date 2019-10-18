package com.rdev.clans;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Clan implements Comparable<Clan>{

    @Getter private String configurationID;
    @Getter private String name;
    @Getter private String clanColor;
    @Getter private ChatColor clanChatColor;
    @Getter @Setter private int points;
    @Getter @Setter private List<String> members; //player's UUID
    @Getter @Setter private Score scoreboardScore;

    public Clan(String configurationID, String name, String clanColor) {
        this.configurationID = configurationID;
        this.name = name;
        this.clanColor = clanColor;
        this.clanChatColor = ChatColor.valueOf(clanColor);
        this.points = 0;
    }


    @Override
    public int compareTo(Clan o) {
        return this.getMembers().size() - o.getMembers().size();
    }
}
