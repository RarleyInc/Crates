/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.settings;

import com.rarley.crates.loader.messages.MessagesLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class Messages {

    private final MessagesLoader loader;

    public String get(@NonNull String field, String category) {
        if (loader == null) return "";

        final String path = "messages.";

        return ChatColor.translateAlternateColorCodes('&',
                (category == null && loader.getMessagesConfiguration().contains(path + field))
                        ? loader.getMessagesConfiguration().getString(path + field)
                        : getCategory(path + category + "." + field)); // messages.create.cancelled
    }

    private String getCategory(String path) {
        return loader.getMessagesConfiguration().contains(path) ? loader.getMessagesConfiguration().getString(path) : "";
    }

}
