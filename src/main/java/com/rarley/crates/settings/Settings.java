/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.settings;

import com.rarley.crates.loader.messages.MessagesLoader;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Settings {

    private final Messages messages;
    private final int cratesPerDay, delayToOpenOtherCrate;

    public Settings(MessagesLoader loader, FileConfiguration config) {
        this.messages = new Messages(loader);
        this.cratesPerDay = config.getInt("player.crates-per-day");
        this.delayToOpenOtherCrate = config.getInt("player.delay-to-open-other-crate");
    }

}
