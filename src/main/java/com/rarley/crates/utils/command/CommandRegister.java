/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.command;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class CommandRegister {

    public static void register(JavaPlugin instance, CommandExecutor command) {
        final CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

        instance.getCommand(info.name()).setExecutor(command);
    }

}
