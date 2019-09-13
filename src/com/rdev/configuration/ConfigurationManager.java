package com.rdev.configuration;

import com.rdev.ParlaClans;
import com.rdev.clans.Clan;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigurationManager {

    public void loadClans() {
        ParlaClans.getInstance().getClansManager().setStarted(ParlaClans.getInstance().getConfig().getBoolean("Season.started"));
        ParlaClans.getInstance().getClansManager().setTimestamp(ParlaClans.getInstance().getConfig().getLong("Season.timestamp"));

        ConfigurationSection cs = ParlaClans.getInstance().getConfig().getConfigurationSection("Clans");

        cs.getKeys(false).forEach(key -> {
            String clanName = cs.getString(key + ".name");
            String clanColor = cs.getString(key + ".color");
            int clanPoints =  cs.getInt(key + ".points");

            if (clanName == null || clanColor == null) return;
            Clan clan = new Clan(key, clanName, ChatColor.translateAlternateColorCodes('&', clanColor));
            clan.setPoints(clanPoints);

            File clanFile = new File(ParlaClans.getInstance().getDataFolder(), key + ".yml");
            YamlConfiguration con = YamlConfiguration.loadConfiguration(clanFile);

            clan.setMembers(con.getStringList("Members"));

            if (!clanFile.exists()) {
                con.set("Members", null);
                try {
                    con.save(clanFile);
                } catch (IOException ignore) { }
            }

            ParlaClans.getInstance().getClansManager().getClans().add(clan);
        });

    }

    public void addMemberToConfiguration(String uuid, Clan clan) {
        File clanFile = new File(ParlaClans.getInstance().getDataFolder(), clan.getConfigurationID() + ".yml");
        YamlConfiguration con = YamlConfiguration.loadConfiguration(clanFile);

        List<String> members = con.getStringList("Members");
        members.add(uuid);

        con.set("Members", members);

        try {
            con.save(clanFile);
        } catch (IOException ignore) {}
    }

    public void removeMemberFromConfiguration(String uuid, Clan clan) {
        File clanFile = new File(ParlaClans.getInstance().getDataFolder(), clan.getConfigurationID() + ".yml");
        YamlConfiguration con = YamlConfiguration.loadConfiguration(clanFile);

        List<String> members = con.getStringList("Members");
        members.remove(uuid);

        con.set("Members", members);

        try {
            con.save(clanFile);
        } catch (IOException ignore) {}
    }

    public void createSeasonRewardsFile(Clan winnerClan) {
        int season = 0;
        File folder = new File(ParlaClans.getInstance().getDataFolder() + "/seasons");
        if (folder.listFiles() != null)
            season = (int) Arrays.stream(folder.listFiles()).filter(file -> file.getName().endsWith("-summary.yml")).count();

        File newSeasonFile = new File(ParlaClans.getInstance().getDataFolder() + "/seasons", "Season-"+ season + "-summary.yml");
        YamlConfiguration con = YamlConfiguration.loadConfiguration(newSeasonFile);

        con.set("Winner.name", winnerClan.getName());
        con.set("Winner.color", winnerClan.getClanColor());
        con.set("Winner.points", winnerClan.getPoints());
        con.set("Winner.timestamp", System.currentTimeMillis());
        con.set("RewardedMembers", winnerClan.getMembers());
        con.set("Rewards", ParlaClans.getInstance().getConfig().getStringList("Rewards.WinnerClanCommands"));

        ParlaClans.getInstance().getConfig().set("Season.number", season);

        try {
            con.save(newSeasonFile);
        } catch (IOException ignore) {}

        ParlaClans.getInstance().saveConfig();
    }

}
