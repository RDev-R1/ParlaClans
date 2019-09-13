package com.rdev.menu;

import com.rdev.clans.Clan;
import com.rdev.configuration.Constants;
import com.rdev.utils.ItemstackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class MenuGUI {

    public static void openChooseClanMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, Constants.GUI.GUI_TOP_TEXT);

        inv.setItem(10, ItemstackUtils.createItemStack(Material.BLUE_WOOL,1, ChatColor.valueOf(Constants.CLAN_A_COLOR) + Constants.CLAN_A));
        inv.setItem(16, ItemstackUtils.createItemStack(Material.RED_WOOL,1, ChatColor.valueOf(Constants.CLAN_B_COLOR) + Constants.CLAN_B));

        p.openInventory(inv);
    }

    public static void openSureMenu(Player p, Clan clan) {

        Inventory inv = Bukkit.createInventory(null, 27, Constants.GUI.GUI_SURE_TOP_TEXT);

        inv.setItem(13, ItemstackUtils.createItemStack(Material.WHITE_BANNER, 1, clan.getName()));

        inv.setItem(10, ItemstackUtils.createItemStack(Material.EMERALD_BLOCK,1, Constants.GUI.GUI_YES));
        inv.setItem(16, ItemstackUtils.createItemStack(Material.REDSTONE_BLOCK,1, Constants.GUI.GUI_NO));

        p.openInventory(inv);
    }
}
