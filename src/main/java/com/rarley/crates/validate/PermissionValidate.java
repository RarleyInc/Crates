/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.validate;

import com.rarley.crates.CratesPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@RequiredArgsConstructor(staticName = "of")
public class PermissionValidate implements Predicate<Player> {

    private final CratesPlugin instance;
    private final String permission, arg;

    @Override
    public boolean test(Player player) {
        if (!player.hasPermission("crates.admin." + permission)) {
            player.sendMessage(instance.getMessages().get("no-permission", null));

            return false;
        }

        return true;
    }

    public void accept(Player player, BiConsumer<Player, String> action) {
        if (!test(player)) return;

        action.accept(player, arg);
    }

}
