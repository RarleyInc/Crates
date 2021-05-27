/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.listeners;

import com.rarley.crates.CratesPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final CratesPlugin instance;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onPlayerQuit(PlayerQuitEvent event) {
        instance.getUserCache().forceRemove(event.getPlayer().getName());
    }
}
