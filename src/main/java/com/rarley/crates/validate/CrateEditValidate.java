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
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CrateEditValidate implements Predicate<Player> {

    private final CratesPlugin instance;
    private final String name;

    @Override
    public boolean test(Player player) {
        final CrateCache cache = instance.getCrateCache();

        if (!cache.existsCrate(name)) {
            player.sendMessage(instance.getMessages().get("crate-not-exists", null));
            return false;
        }

        return true;
    }

    public void run(Player player, Consumer<Player> action) {
        if (!test(player)) return;

        action.accept(player);
    }

}
