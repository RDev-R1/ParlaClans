package com.rdev.clans;

import com.rdev.configuration.Constants;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarClanWinner {

    @Getter private BossBar bossBar;

    public BossBarClanWinner(String lastWinner) {
        this.bossBar = Bukkit.createBossBar(Constants.PluginSettings.BOSSBAR_MESSAGE.replace("%clan%", lastWinner), BarColor.GREEN, BarStyle.SEGMENTED_6);
        bossBar.setVisible(true);
    }

    public void sendToAll() {
        if(this.bossBar == null) return;

        Bukkit.getOnlinePlayers().forEach(p -> this.bossBar.addPlayer(p));
    }

    public void addPlayer(Player p) {
        this.bossBar.addPlayer(p);
    }
}
