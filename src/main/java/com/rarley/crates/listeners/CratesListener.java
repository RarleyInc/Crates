/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.listeners;

import com.rarley.crates.CratesPlugin;
import com.rarley.crates.model.Crate;
import com.rarley.crates.utils.inventory.InventoryBuilder;
import com.rarley.crates.utils.item.builder.ItemBuilder;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import com.rarley.crates.validate.CrateOpenValidate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class CratesListener implements Listener {

    private final CratesPlugin instance;

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (!hasCrateItem(event.getPlayer().getItemInHand())) return;

            final Player player = event.getPlayer();
            final ItemBuilder item = ItemBuilder.of(player.getItemInHand());

            final String crateName = (String) item.getNbt().get("crate-name", String.class);

            new CrateOpenValidate(instance, crateName)
                    .run(player, (consumer) -> consumer.openInventory(confirmGUI(instance.getCrateCache().getCrate(crateName))));

            event.setCancelled(true);
        }
    }

    private Inventory confirmGUI(Crate crate) {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder("§8Confirmation GUI", 4);

        final UniversalTagCompound confirmNbt = new UniversalTagCompound()
                .set("crate-confirm", true)
                .set("crate-open", true)
                .set("crate-name", crate.getName());

        final ItemStack confirm = new ItemBuilder(Material.WOOL, 5, confirmNbt)
                .setLore("§7Click to open.")
                .setDisplayName("§aConfirm")
                .build();

        final UniversalTagCompound cancelNbt = new UniversalTagCompound()
                .set("crate-cancel", true)
                .set("crate-open", true)
                .set("crate-name", crate.getName());

        final ItemStack cancel = new ItemBuilder(Material.WOOL, 14, cancelNbt)
                .setLore("§7Click to cancel.")
                .setDisplayName("§cCancel")
                .build();

        return inventoryBuilder
                .addItem(crate.getIcon(), 13)
                .addItem(confirm, 20)
                .addItem(cancel, 24)
                .build();
    }

    private boolean hasCrateItem(ItemStack itemInHand) {
        return itemInHand != null
                && itemInHand.hasItemMeta()
                && ItemBuilder.of(itemInHand).getNbt().has("crate-name");
    }

}
