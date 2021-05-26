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

    public CrateOpenAnimation(CratesPlugin instance, Crate crate, Inventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
        this.item = randomItem(crate);
        this.instance = instance;

        runTaskTimer(instance, 5L, 5L);
    }

    @Override
    public void run() {
        if (player == null || finishedCount >= 4) {

            if (player != null) {

                if(item != null) {
                    final String itemName = (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName())
                            ? item.getItem().getItemMeta().getDisplayName()
                            : "Nameless";

                    player.sendMessage(instance.getMessages().get("success", "open")
                            .replace("%item_name%", itemName)
                            .replace("%item_chance%", String.valueOf(item.getChance())));
                }

                player.closeInventory();
            }

            cancel();
            return;
        }

        player.openInventory(inventory);

        if (startCount == endCount) {
            if (finishedCount == 0) {

                if (item == null) {
                    inventory.setItem(22, new ItemStack(Material.BARRIER));

                    player.sendMessage("§cYou didn't get anything from the crate.");
                } else {
                    inventory.setItem(22, item.getItem());

                    player.getInventory().addItem(item.getItem());
                }
            }

            finishedCount++;
            return;
        }

        final int data = ThreadLocalRandom.current().nextInt(14);

        inventory.setItem(startCount,
                new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (data == 8 ? data + 1 : data)));

        inventory.setItem(endCount,
                new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) ((data + 1) == 8 ? data + 2 : data + 1)));

        startCount++;
        endCount--;
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
