package com.rdev.configuration;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class Constants  {

    public static String CLAN_A = "Red";
    public static String CLAN_B = "Blue";
    public static String CLAN_A_COLOR = ChatColor.RED + "";
    public static String CLAN_B_COLOR = ChatColor.BLUE + "";
    public static Material CLAN_A_MATERIAL = Material.RED_WOOL;
    public static Material CLAN_B_MATERIAL = Material.BLUE_WOOL;

    public static class PluginSettings {
        public static int KILL_POINTS = 1;
        public static int HOUR_POINTS = 4;
        public static int MAX_PLAYERS_DIFFERENCE = 15;
        public static int KILLS_PER_PLAYER = 10;
        public static String BOSSBAR_MESSAGE = "The last winner is %clan%";
    }

    public static class Messages {
        public static String PREFIX = ChatColor.RED + "[ParlaClans]" + ChatColor.WHITE;
        public static String JOIN_CLAN_MESSAGE = "You've joined the %clan% clan!";
        public static String NOT_PLAYER_ERROR = "You are not a player!";
        public static String PARLACLANS_FORCE_FINISH = "ParlaClans system is now closed!";
        public static String PARLACLANS_CLOSED = "ParlaClans system is now closed!";
        public static String CLAN_FULL = "This clan is full, please choose another clan!";
    }

    public static class GUI {
        public static String GUI_TOP_TEXT = "Choose your team!";
        public static String GUI_SURE_TOP_TEXT = "Are you surely want to join this guild?";
        public static String GUI_YES = "Yes";
        public static String GUI_NO = "No";
    }

}
