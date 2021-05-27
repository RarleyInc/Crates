/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.loader.plugin;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.rarley.crates.utils.command.CommandRegister.register;

public abstract class PluginLoader extends JavaPlugin {

    private final Logger logger = Bukkit.getLogger();

    public abstract boolean enable();

    public abstract void disable();

    public void initializer() {
    }

    /***
     * @param plugin instance
     * @param args commands to register
     * @implNote If the varargs parameter array is used only to transmit a variable number of arguments
     *          from the caller to the method—which is, after all, the purpose of varargs—then the method is safe.
     *          - Effective Java;
     */

    @SafeVarargs
    @SneakyThrows
    public final <P extends PluginLoader, C extends CommandExecutor> void registerCommands(P plugin, C... args) {
        Stream.of(args).forEach(command -> register(plugin, command));
    }

    /***
     * @param listeners to register
     */

    public final void registerListeners(Listener... listeners) {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        Stream.of(listeners)
                .forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    public void initConfiguration() {
    }

    @Override
    public final void onEnable() {
        final Class<?> clazz = getClass();

        if (!clazz.isAnnotationPresent(PluginInfo.class) || clazz.getAnnotation(PluginInfo.class).name().equals("undefined")) {
            severe(String.format("Error on enable %s [ADD ANNOTATION INFO]", clazz.getSimpleName()));

            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        if (!enable()) debug(STATUS.ERROR);

        else {

            final PluginInfo info = clazz.getAnnotation(PluginInfo.class);

            if (info.useInitializer())
                initializer();

            if (info.useConfig())
                initConfiguration();

            debug(STATUS.ENABLE);
        }
    }

    @Override
    public final void onDisable() {
        if (!getClass().isAnnotationPresent(PluginInfo.class)) return;

        HandlerList.unregisterAll(this);

        disable();

        debug(STATUS.DISABLE);
    }

    /***
     * @param message that will be sent
     */
    public final void log(String message) {
        logger.info("[CratesLoader] " + message);
    }

    /***
     * @param message that will be sent
     */
    public final void severe(String message) {
        logger.severe("[CratesLoader] " + message);
    }

    /***
     * @param status current plugin
     */
    private void debug(@NonNull STATUS status) {
        final PluginInfo info = getClass().getAnnotation(PluginInfo.class);

        final String name = info.name();
        final String description = info.description();

        if (status == STATUS.ENABLE)
            log(String.format("%s is initializing...", name));

        else if (status == STATUS.DISABLE)
            log(String.format("%s is disabling...", name));

        else if (status == STATUS.ERROR) {
            severe(String.format("%s cannot be initialized!!", name));

            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!description.equals("undefined"))
            log(String.format("Description: %s", description));
    }

    private enum STATUS {
        ENABLE, DISABLE, ERROR
    }
}
