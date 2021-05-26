/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.database.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class DatabaseException extends RuntimeException {

    public DatabaseException(DatabaseStatus status) {
        super(status.getError());

        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.isPluginEnabled("RarleyCrates"))
            Bukkit.getPluginManager().disablePlugin(pluginManager.getPlugin("RarleyCrates"));
    }

    @RequiredArgsConstructor
    @Getter
    public enum DatabaseStatus {
        DEFAULT_CONFIG("You must configure the database."),
        CREATE_TABLE("Error creating database table."),
        CONNECT("Error connecting to database."),
        DISCONNECT("Error disconnecting from database."),
        CRATES_SAVE("Error saving crate on files.");

        final String error;
    }
}
