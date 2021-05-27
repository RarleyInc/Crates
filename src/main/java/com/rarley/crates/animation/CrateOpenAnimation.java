/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.animation;

import com.rarley.crates.CratesPlugin;
import com.rarley.crates.model.Crate;
import com.rarley.crates.model.ItemCrate;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class CrateOpenAnimation extends BukkitRunnable {

    private final Inventory inventory;
    private final Player player;
    private final ItemCrate item;
    private final CratesPlugin instance;

    private int finishedCount, startCount, endCount = 44;

    private CrateOpenAnimation(CratesPlugin instance, Crate crate, Inventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
        this.item = randomItem(crate);
        this.instance = instance;

        if (player == null) return; // force stop animation.

        removeCrate();

        player.openInventory(inventory);

        runTaskTimer(instance, 5L, 5L);
    }

    public static void of(CratesPlugin instance, String crate, Inventory inventory, Player player) {
        new CrateOpenAnimation(instance, instance.getCrateCache().getCrate(crate), inventory, player);
    }

    @Override
    public void run() {
        if (player == null) {
            cancel();
            return;
        }

        if (!hasCrateInventoryOpen() || finishedCount >= 4) {

            if (item != null) {
                player.getInventory().addItem(item.getItem());

                final String itemName = (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName())
                        ? item.getItem().getItemMeta().getDisplayName()
                        : "Nameless";

                player.sendMessage(instance.getMessages().get("success", "open")
                        .replace("%item_name%", itemName)
                        .replace("%item_chance%", String.valueOf(item.getChance())));

            } else player.sendMessage("§cYou didn't get anything from the crate.");

            player.closeInventory();

            cancel();
            return;
        }

        if (startCount == endCount) {

            if (finishedCount == 0)
                inventory.setItem(22, (item == null ? new ItemStack(Material.BARRIER) : item.getItem()));

            finishedCount++;
            return;
        }

        final int data = ThreadLocalRandom.current().nextInt(14);

        final ItemStack startItem = new ItemStack(Material.STAINED_GLASS_PANE, 1,
                (short) (data == 8 ? data + 1 : data));
        final ItemStack endItem = new ItemStack(Material.STAINED_GLASS_PANE, 1,
                (short) ((data + 1) == 8 ? data + 2 : data + 1));

        final ItemMeta itemMeta = startItem.getItemMeta();
        itemMeta.setDisplayName(String.format("§%s...", Math.min(data + 1, 9)));

        startItem.setItemMeta(itemMeta);
        endItem.setItemMeta(itemMeta);

        inventory.setItem(startCount, startItem);
        inventory.setItem(endCount, endItem);

        startCount++;
        endCount--;
    }

    @SuppressWarnings("deprecation")
    private void removeCrate() {
        final ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null) {
            if (itemInHand.getAmount() > 1)
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            else
                player.setItemInHand(null);
        }
    }

    private boolean hasCrateInventoryOpen() {
        return player != null
                && player.getOpenInventory() != null
                && player.getOpenInventory().getTitle().equals("§8Drawing an item...");
    }

    private ItemCrate randomItem(@NonNull Crate crate) {
        AtomicReference<ItemCrate> found = new AtomicReference<>();

        final int random = ThreadLocalRandom.current().nextInt(100);

        crate.getItems()
                .stream()
                .filter(ItemCrate::hasChance)
                .sorted(Comparator.comparing(ItemCrate::getChance))
                .forEachOrdered(item -> {

                    if (found.get() != null) return;

                    if (random <= (int) item.getChance())
                        found.set(item);
                });

        if (found.get() == null && crate.getItemsTotalChances() >= 99)
            found.set(crate.getItems().stream().max(Comparator.comparing(ItemCrate::getChance)).orElse(null));

        return found.get();
    }
}
