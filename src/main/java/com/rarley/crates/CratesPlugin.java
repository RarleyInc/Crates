/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates;

import com.rarley.crates.cache.crate.CrateCache;
import com.rarley.crates.cache.crate.CrateFlatFile;
import com.rarley.crates.cache.user.UserCache;
import com.rarley.crates.cache.user.UserCacheLoader;
import com.rarley.crates.command.CratesCommand;
import com.rarley.crates.database.impl.MySQLServiceImpl;
import com.rarley.crates.database.services.CratesDatabaseService;
import com.rarley.crates.listeners.CratesListener;
import com.rarley.crates.listeners.CratesSettingsListener;
import com.rarley.crates.listeners.PlayerListener;
import com.rarley.crates.loader.crates.CratesLoader;
import com.rarley.crates.loader.messages.MessagesLoader;
import com.rarley.crates.loader.plugin.PluginInfo;
import com.rarley.crates.loader.plugin.PluginLoader;
import com.rarley.crates.settings.Messages;
import com.rarley.crates.settings.Settings;
import com.rarley.crates.utils.inventory.InventoryBuilder;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
@PluginInfo(
        name = "Crates",
        description = "A simple plugin of crates",
        useConfig = true
)

@Getter
public final class CratesPlugin extends PluginLoader {

    private final CratesDatabaseService database;

    private final UserCache userCache;
    private final CrateCache crateCache;

    private final CrateFlatFile flatFile;
    private final Settings settings;

    public CratesPlugin() {
        saveDefaultConfig();

        final FileConfiguration config = getConfig();
        final String path = "database.";

        this.database = new MySQLServiceImpl(
                config.getString(path + "host"),
                config.getString(path + "user"),
                config.getString(path + "password"),
                config.getString(path + "database"),
                config.getString(path + "table")
        );

        this.userCache = new UserCache(this, new UserCacheLoader(database.getUserService()));
        this.crateCache = new CrateCache(this);

        this.flatFile = new CrateFlatFile(this);
        this.settings = new Settings(new MessagesLoader(this), config);
    }

    @Override
    public boolean enable() {
        return database != null && database.getUserService() != null;
    }

    @Override
    public void disable() {
        userCache.shutdown();
        crateCache.shutdown();

        database.disconnect();
    }

    @Override
    public void initializer() {
        registerCommands(this,
                new CratesCommand(this, new InventoryBuilder("§8Confirmation GUI", 3).build())
        );

        registerListeners(
                new CratesListener(this),
                new CratesSettingsListener(this),
                new PlayerListener(this)
        );
    }

    @Override
    public void initConfiguration() {
        if (flatFile == null) return;

        new CratesLoader(Objects.requireNonNull(flatFile.prepareCrateFolder().get().listFiles()), crateCache);
    }

    public Messages getMessages() {
        return settings.getMessages();
    }
}
