package com.rarley.crates.utils.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryBuilder implements InventoryHolder, Listener {

    protected final Inventory inventory;

    public InventoryBuilder(String name, int rows) {
        inventory = Bukkit.createInventory(null,
                9 * rows, ChatColor.translateAlternateColorCodes('&', name));
    }

    public InventoryBuilder(Inventory inventory) {
        this.inventory = Bukkit.createInventory(null, inventory.getSize(), inventory.getName());

        this.inventory.setContents(inventory.getContents());
    }

    public InventoryBuilder addItem(ItemStack item, int... slots) {
        for (int slot : slots)
            inventory.setItem(slot, item);

        return this;
    }

    public InventoryBuilder disableInventoryClick(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        return this;
    }

    public Inventory build() {
        return inventory;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    private void handle(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null
                && event.getInventory().getHolder() == inventory.getHolder()) {

            event.setCancelled(true);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
