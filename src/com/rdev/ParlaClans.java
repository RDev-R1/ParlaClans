package com.rdev;

import com.rdev.clans.BossBarClanWinner;
import com.rdev.clans.Clan;
import com.rdev.clans.ClanScoreboardManager;
import com.rdev.clans.ClansManager;
import com.rdev.commands.MainCommand;
import com.rdev.configuration.ConfigurationManager;
import com.rdev.configuration.Constants;
import com.rdev.listeners.ClansListeners;
import com.rdev.listeners.MenusListeners;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public class ParlaClans extends JavaPlugin {

    @Getter private static ParlaClans instance;
    @Getter public ClansManager clansManager;
    @Getter public ConfigurationManager configurationManager;
    @Getter public ClanScoreboardManager clanScoreboardManager;
    @Getter @Setter public BossBarClanWinner winnerBossBar;

    @Override
    public void onEnable() {
        instance = this;

        saveConfig();
        getConfig().options().copyDefaults(true);

        this.loadClansManager();
        this.loadConfigurationManager();
        this.loadConstants();
        this.loadClanScoreboardManager();

        this.configurationManager.loadClans();

        if (this.clansManager.isStarted()) {
            this.clanScoreboardManager.setupScoreboard();
            Bukkit.getOnlinePlayers().forEach(player -> {
                Clan clan = clansManager.getPlayersClan(player);
                if (clan != null) clanScoreboardManager.addPlayer(player, clan);
            });
        }

        this.clansManager.timeCheckerSetup();

        this.registerListeners();

        getCommand("parlaclans").setExecutor(new MainCommand());

        int season = getConfig().getInt("Season.number");
        if (season > 0) {
            File seasonFile = new File(ParlaClans.getInstance().getDataFolder() + "/seasons", "Season-"+ season + "-summary.yml");
            YamlConfiguration con = YamlConfiguration.loadConfiguration(seasonFile);
            BossBarClanWinner bossBarClanWinner = new BossBarClanWinner(con.getString("Winner.name"));
            bossBarClanWinner.sendToAll();
            this.winnerBossBar = bossBarClanWinner;
        }
    }

    @Override
    public void onDisable() {
        //Used in order to load config file to avoid config reset
        reloadConfig();

        this.clansManager.getClans().forEach(clan -> {
            getConfig().set("Clans." + clan.getConfigurationID() + ".points", clan.getPoints());
            saveConfig();
        });

        if (winnerBossBar != null) {
            winnerBossBar.getBossBar().setVisible(false);
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new MenusListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ClansListeners(), this);
    }

    private void loadClansManager() {
        this.clansManager = new ClansManager();
    }

    private void loadConfigurationManager() {
        this.configurationManager = new ConfigurationManager();
    }

    private void loadClanScoreboardManager() {
        this.clanScoreboardManager = new ClanScoreboardManager();
    }

    private void loadConstants() {
        FileConfiguration con = getConfig();

        Arrays.asList(Constants.Messages.class.getFields()).forEach(field -> {
            try {
                field.set(String.class, ChatColor.translateAlternateColorCodes('&', con.getString("Messages." + field.getName())));
            } catch (IllegalAccessException ignore) {}
        });
        Arrays.asList(Constants.GUI.class.getFields()).forEach(field -> {
            try {
            field.set(String.class, ChatColor.translateAlternateColorCodes('&', con.getString("Messages." + field.getName())));
            } catch (IllegalAccessException ignore) {}
        });

        Constants.CLAN_A = ChatColor.translateAlternateColorCodes('&', con.getString("Clans.ClanA.name"));
        Constants.CLAN_A_COLOR = ChatColor.translateAlternateColorCodes('&', con.getString("Clans.ClanA.color"));
        Constants.CLAN_A_MATERIAL = Material.getMaterial(con.getString("Clans.ClanA.material"));

        Constants.CLAN_B = ChatColor.translateAlternateColorCodes('&', con.getString("Clans.ClanB.name"));
        Constants.CLAN_B_COLOR = ChatColor.translateAlternateColorCodes('&', con.getString("Clans.ClanB.color"));
        Constants.CLAN_B_MATERIAL = Material.getMaterial(con.getString("Clans.ClanB.material"));

        Constants.PluginSettings.KILL_POINTS = con.getInt("Points.perKill");
        Constants.PluginSettings.HOUR_POINTS = con.getInt("Points.perHour");
        Constants.PluginSettings.KILLS_PER_PLAYER = con.getInt("KillsPerPlayer");
        Constants.PluginSettings.MAX_PLAYERS_DIFFERENCE = con.getInt("MaxPlayersDifference");

        Constants.PluginSettings.BOSSBAR_MESSAGE = ChatColor.translateAlternateColorCodes('&', con.getString("BossBar.bossBarMessage"));
    }

}
