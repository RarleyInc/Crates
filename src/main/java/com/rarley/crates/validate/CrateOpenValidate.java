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
import com.rarley.crates.model.UserCrate;
import com.rarley.crates.utils.item.builder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CrateOpenValidate implements Predicate<Player> {

    private final CratesPlugin instance;
    private final String crateName;

    @Override
    public boolean test(Player player) {
        final CrateCache cache = instance.getCrateCache();

        if (player.getItemInHand() == null || !ItemBuilder.of(player.getItemInHand()).getNbt().has("crate-name")) {
            player.sendMessage(instance.getMessages().get("crate-not-exists", null));
            player.closeInventory();

            return false;
        }

        if (!cache.existsCrate(crateName)) {
            player.sendMessage(instance.getMessages().get("crate-not-exists", null));
            return false;
        }

        if (!player.hasPermission("crates.bypass")) {

            final UserCrate user = instance.getUserCache().getAndPut(player.getName());

            if (user.hasCooldown()) {

                if (user.getCooldown() == 0L)
                    player.sendMessage("§cWait to open the crate.");
                else
                    player.sendMessage(instance.getMessages().get("wait-open-crate", null)
                            .replace("%seconds%", String.valueOf(user.getCooldown())));

                return false;
            } else if (user.maxCratesToday(instance.getSettings().getCratesPerDay())) {
                player.sendMessage(instance.getMessages().get("max-crates-per-day", null));

                return false;
            }
        }

        return true;
    }

    public void run(Player player, Consumer<Player> action) {
        if (test(player))
            action.accept(player);
    }

}
