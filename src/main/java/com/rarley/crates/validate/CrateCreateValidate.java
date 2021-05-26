/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.validate;

import com.rarley.crates.CratesPlugin;
import com.rarley.crates.cache.crate.CrateCache;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CrateCreateValidate implements Predicate<Player> {

    private final CratesPlugin instance;
    private final String name;

    @Override
    public boolean test(Player player) {
        final ItemStack itemInHand = player.getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(instance.getMessages().get("without-item", "create"));
            return false;
        }

        final CrateCache cache = instance.getCrateCache();

        if (cache.existsCrate(name)) {
            player.sendMessage(instance.getMessages().get("crate-already-exists", null));
            return false;
        }

        return true;
    }

    public void run(Player player, Consumer<Player> action) {
        player.closeInventory();

        if (test(player))
            action.accept(player);
    }

}
