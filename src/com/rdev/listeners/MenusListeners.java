package com.rdev.listeners;

import com.rdev.ParlaClans;
import com.rdev.clans.Clan;
import com.rdev.configuration.Constants;
import com.rdev.exceptions.ClanFullException;
import com.rdev.menu.MenuGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenusListeners implements Listener {

    @EventHandler
    public void chooseClanMenu(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Constants.GUI.GUI_TOP_TEXT)) return;

        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();

        if (!(item.hasItemMeta() && item.getItemMeta().hasDisplayName())) return;

        Player p = (Player) e.getWhoClicked();

        Clan requestedClan = null;
        switch (e.getSlot()) {
            case 10:
                requestedClan = ParlaClans.getInstance().getClansManager().getClans().get(0);
                break;
            case 16:
                requestedClan = ParlaClans.getInstance().getClansManager().getClans().get(1);
                break;
            default:
                p.closeInventory();
                return;
        }//Add more checks if you want to add more clans.

        MenuGUI.openSureMenu(p, requestedClan);
    }

    @EventHandler
    public void sureMenu(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Constants.GUI.GUI_SURE_TOP_TEXT)) return;

        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();

        if (!(item.hasItemMeta() && item.getItemMeta().hasDisplayName())) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 10) {
            p.closeInventory();
            String clanName = e.getInventory().getItem(13).getItemMeta().getDisplayName();
            Clan selectedClan = ParlaClans.getInstance().getClansManager().getClanByName(ChatColor.stripColor(clanName));

            Clan oldClan = ParlaClans.getInstance().getClansManager().getPlayersClan(p);

            try {
                if(oldClan == null) {
                    ParlaClans.getInstance().getClansManager().addMember(p, selectedClan);
                } else {
                    ParlaClans.getInstance().getClansManager().movePlayerClan(p, oldClan, selectedClan);
                }
            } catch (ClanFullException ex) {
                p.sendMessage(ex.getMessage());
                return;
            }

            p.sendMessage(Constants.Messages.JOIN_CLAN_MESSAGE.replaceAll("%clan%", selectedClan.getClanChatColor() + clanName));
        } else if (e.getSlot() == 16) {
            p.closeInventory();
        }

    }
}
