/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.model;

import com.google.common.collect.Sets;
import com.rarley.crates.utils.inventory.InventoryBuilder;
import com.rarley.crates.utils.item.builder.ItemBuilder;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Getter
public class Crate {

    private final String name;
    private final ItemStack icon;
    private final Set<ItemCrate> items = Sets.newConcurrentHashSet();

    public void addItem(@NonNull ItemCrate... item) {
        items.addAll(Arrays.asList(item));
    }

    public void removeItem(int id) {
        items.removeIf(item -> item.getId() == id);

        AtomicInteger counter = new AtomicInteger(id);

        // re-organize ids > id
        items.stream()
                .filter(item -> item.getId() > id)
                .sorted(Comparator.comparing(ItemCrate::getId))
                .forEach(item -> item.setId(counter.getAndIncrement()));
    }

    public void openEditInventory(Player player) {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder("§8Crate: " + name, fixSlots(items.size()));
        final Inventory inventory = inventoryBuilder.getInventory();

        items.stream()
                .sorted(Comparator.comparing(ItemCrate::getId))
                .forEachOrdered(crateItem -> {
                    if (inventory.firstEmpty() == -1) return;

                    final UniversalTagCompound nbt = new UniversalTagCompound();

                    nbt.set("item-id", crateItem.getId());

                    final ItemStack item = new ItemBuilder(crateItem.getItem(), nbt)
                            .setDisplayName("§aItem #" + crateItem.getId())
                            .setLore(
                                    "",
                                    "§eChance: §b" + crateItem.getChance() + "%",
                                    "",
                                    "§eRIGHT CLICK §7to §eREMOVE",
                                    "§eSHIFT+CLICK §7to §eSET CHANCE"
                            )
                            .build();

                    inventoryBuilder.addItem(item, inventory.firstEmpty());
                });

        player.openInventory(inventoryBuilder.build());
    }

    public void givePlayer(@NonNull Player player, int amount) {
        final ItemBuilder itemBuilder = ItemBuilder.of(icon);

        itemBuilder.getNbt().set("crate-name", name);

        final ItemStack item = itemBuilder.build();

        item.setAmount(amount);

        player.getInventory().addItem(new ItemStack(item));
    }

    public ItemCrate getItemById(int id) {
        return items.stream()
                .filter(itemCrate -> itemCrate.getId() == id)
                .findAny()
                .orElse(null);
    }

    public double getItemsTotalChances() {
        return items.stream()
                .mapToDouble(ItemCrate::getChance)
                .sum();
    }

    public boolean containsItemById(int id) {
        return id > 0 && items.stream().anyMatch(itemCrate -> itemCrate.getId() == id);
    }

    private int fixSlots(double items) {
        final double d = (items / 9);
        /*
         1 / 9 = 0.1
         9 / 9 = 9
         10 / 9 = 1.1
         18 / 9 = 2.0 + 0.9 = 3
         */

        final int i = (int) (d + 0.9);

        return i <= 0 ? 1 : i;
    }

}
