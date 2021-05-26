/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.crate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RequiredArgsConstructor
public class CrateFlatFile {

    private final JavaPlugin instance;

    @SneakyThrows({IOException.class, SecurityException.class})
    public Supplier<File> prepareCrateFile(String name) {
        final File file = getCrateFile(name);

        file.createNewFile(); // create the file if it does not exist

        return () -> file;
    }

    @SneakyThrows(SecurityException.class)
    public Supplier<File> prepareCrateFolder() {
        final File folder = new File(instance.getDataFolder() + File.separator + "crates");

        if (!folder.exists())
            folder.mkdirs();

        return () -> folder;
    }

    @SneakyThrows(NullPointerException.class)
    public File getCrateFile(String name) {
        return new File(instance.getDataFolder() + File.separator + "crates", name + ".yml");
    }

    public FileConfiguration getConfiguration(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

}
