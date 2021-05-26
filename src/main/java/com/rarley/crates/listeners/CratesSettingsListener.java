/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.listeners;

import com.google.common.collect.Maps;
import com.rarley.crates.CratesPlugin;
import com.rarley.crates.animation.CrateOpenAnimation;
import com.rarley.crates.model.Crate;
import com.rarley.crates.model.ItemCrate;
import com.rarley.crates.model.UserCrate;
import com.rarley.crates.utils.inventory.InventoryBuilder;
import com.rarley.crates.utils.item.builder.ItemBuilder;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import com.rarley.crates.validate.CrateCreateValidate;
import com.rarley.crates.validate.CrateDeleteValidate;
import com.rarley.crates.validate.CrateEditValidate;
import com.rarley.crates.validate.CrateOpenValidate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class CratesSettingsListener implements Listener {

    private final CratesPlugin instance;
    private final Map<String, String> chance = Maps.newConcurrentMap();

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals("§8Drawing an item...")) {
            event.setCancelled(true);

            return;
        }

        if (!event.getInventory().getTitle().equals("§8Confirmation GUI")
                && !event.getInventory().getTitle().startsWith("§8Crate:")) return;

        event.setCancelled(true);

        final ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null || currentItem.getType() == Material.AIR) return;

        final ItemBuilder itemBuilder = ItemBuilder.of(currentItem);
        final UniversalTagCompound nbt = itemBuilder.getNbt();
        final Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().startsWith("§8Crate:")) {

            final String name = event.getInventory().getTitle().replace("§8Crate: ", "");

            editCrate(player,
                    name,
                    event.getClick(),
                    currentItem,
                    (int) (nbt.has("item-id") ? nbt.get("item-id", int.class) : 0)
            );

            return;
        }

        if (!nbt.has("crate-name")) return;

        final String name = (String) nbt.get("crate-name", String.class);

        if (nbt.has("crate-cancel")) {

            if (nbt.has("crate-create") || nbt.has("crate-delete"))
                player.sendMessage((nbt.has("crate-create")
                        ? instance.getMessages().get("cancelled", "create")
                        : instance.getMessages().get("cancelled", "delete")));


            player.closeInventory();

        } else if (nbt.has("crate-confirm")) {

            if (nbt.has("crate-open"))
                openCrate(player, name);

            else if (nbt.has("crate-create"))
                createCrate(player, name);

            else if (nbt.has("crate-delete"))
                deleteCrate(player, name);

        }
    }

    // actions

    private void openCrate(Player player, String name) {
        final CrateOpenValidate validate = new CrateOpenValidate(instance, name);

        validate.run(player, (consumer) -> {
            if(!consumer.hasPermission("crates.bypass")) {
                final UserCrate user = instance.getUserCache().getAndPut(consumer.getName());

                user.applyCooldown(instance.getSettings().getDelayToOpenOtherCrate());
            }

            removeItem(consumer);

            new CrateOpenAnimation(instance,
                    instance.getCrateCache().getCrate(name),
                    new InventoryBuilder("§8Drawing an item...", 5).build(),
                    consumer);
        });
    }

    private void deleteCrate(Player player, String name) {
        final CrateDeleteValidate validate = new CrateDeleteValidate(instance, name);

        validate.run(player, consumer -> {
            instance.getCrateCache().deleteCrate(name);

            consumer.sendMessage(instance.getMessages().get("success", "delete"));
        });
    }

    private void editCrate(Player player, String name, ClickType clickType, ItemStack item, int itemId) {
        final CrateEditValidate validate = new CrateEditValidate(instance, name);

        validate.run(player, consumer -> {
            final Crate crate = instance.getCrateCache().getCrate(name);

            switch (clickType) {
                case LEFT:
                    if (crate.containsItemById(itemId)) {
                        crate.removeItem(itemId);

                        crate.openEditInventory(consumer);
                    }
                    break;
                case RIGHT:
                    if (!crate.containsItemById(itemId)) {
                        final int id = (crate.getItems().size() + 1);

                        if (id > 54 || id > instance.getConfig().getInt("max-items-per-crate")) {
                            consumer.sendMessage(instance.getMessages().get("max-items-per-crate", null));
                            return;
                        }

                        crate.addItem(new ItemCrate(new ItemStack(item), 0.0d, id));

                        crate.openEditInventory(consumer);
                    }
                    break;
                case SHIFT_LEFT:
                case SHIFT_RIGHT:
                    if (crate.containsItemById(itemId)) {
                        consumer.closeInventory();

                        player.sendMessage(instance.getMessages().get("write-item-chance", null)
                                .replace("%id%", String.valueOf(itemId)));

                        chance.put(consumer.getName(), crate.getName() + ";" + itemId);
                    }
                    break;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void createCrate(Player player, String name) {
        final CrateCreateValidate validate = new CrateCreateValidate(instance, name);

        validate.run(player, consumer -> {
            final ItemStack icon = new ItemStack(consumer.getItemInHand());

            icon.setAmount(1);

            instance.getCrateCache().createCrate(new Crate(name, icon));

            consumer.sendMessage(instance.getMessages().get("success", "create"));
        });
    }



    // set chance

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!chance.containsKey(event.getPlayer().getName())) return;

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final String[] split = chance.get(player.getName()).split(";");

        chance.remove(player.getName());

        if (split.length != 2) return;

        final String crateName = split[0];

        if (!instance.getCrateCache().existsCrate(crateName)) return;

        final Crate crate = instance.getCrateCache().getCrate(crateName);

        final int itemId = NumberUtils.toInt(split[1], 0);
        final double itemChance = NumberUtils.toDouble(event.getMessage(), -1);

        if (itemChance == -1) {
            player.sendMessage(instance.getMessages().get("invalid-amount", null));
            return;
        }

        final ItemCrate item = crate.getItemById(itemId);

        if (item == null) return;

        final double oldChance = item.getChance();

        item.setChance(0);

        final double totalChance = itemChance + crate.getItemsTotalChances();

        if (totalChance >= 101) {
            item.setChance(oldChance);

            player.sendMessage(instance.getMessages().get("chance-is-greater", null));
            return;
        }

        item.setChance(itemChance);

        player.sendMessage(instance.getMessages().get("updated-item-chance", null)
                .replace("%id%", String.valueOf(itemId))
                .replace("%chance%", String.valueOf(itemChance)));

        crate.openEditInventory(player);
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        chance.remove(event.getPlayer().getName());
    }


    @SuppressWarnings("deprecation")
    private void removeItem(Player player) {
        final ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null) {
            if (itemInHand.getAmount() > 1)
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            else
                player.setItemInHand(null);
        }
    }


}
