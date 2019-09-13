package com.rdev.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class ItemstackUtils {

    public static ItemStack createItemStack(Material mat, int amount, String name) {
        ItemStack is = new ItemStack(mat, amount);
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta(m);
        return is;
    }

    public static ItemStack createItemStack(Material mat, int amount, String name, String[] lore) {
        ItemStack is = new ItemStack(mat, amount);
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        m.setLore(Arrays.asList(lore));
        is.setItemMeta(m);
        return is;
    }

}
