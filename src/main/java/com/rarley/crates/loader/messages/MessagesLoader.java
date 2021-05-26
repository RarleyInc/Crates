/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.loader.messages;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessagesLoader {

    private final JavaPlugin instance;

    @Getter
    private final FileConfiguration messagesConfiguration;

    public MessagesLoader(JavaPlugin instance) {
        this.instance = instance;

        final File file = getFileMessage();

        if (!file.exists()) instance.saveResource("messages.yml", false);

        this.messagesConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private File getFileMessage() {
        return new File(instance.getDataFolder() + File.separator, "messages.yml");
    }


}
