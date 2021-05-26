/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.crate;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rarley.crates.CratesPlugin;
import com.rarley.crates.cache.crate.adapter.ItemStackAdapter;
import com.rarley.crates.database.exception.DatabaseException;
import com.rarley.crates.model.Crate;
import com.rarley.crates.model.ItemCrate;
import com.rarley.crates.utils.item.ItemSerializer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class CrateCache {

    private final CratesPlugin instance;
    private final Map<String, Crate> cache = Maps.newConcurrentMap();

    private final Gson itemCrateGson = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter("item"))
            .create();

    public Crate getCrate(@NonNull String name) {
        return cache.get(name);
    }

    public boolean existsCrate(@NonNull String name) {
        return cache.containsKey(name);
    }

    public void createCrate(@NonNull Crate crate) {
        cache.put(crate.getName().toLowerCase(), crate);

        updateCrate(crate);
    }

    public void deleteCrate(@NonNull String name) {
        cache.remove(name);

        if (!instance.getFlatFile().getCrateFile(name).delete())
            instance.log("Error deleting crate file.");
    }

    @SneakyThrows
    public void updateCrate(@NonNull Crate crate) {
        final File file = instance.getFlatFile().prepareCrateFile(crate.getName().toLowerCase()).get();

        try {
            final FileConfiguration configuration = instance.getFlatFile().getConfiguration(file);

            configuration.set("name", crate.getName());
            configuration.set("icon", ItemSerializer.write(crate.getIcon()));
            configuration.set("items", itemCrateGson.toJson(crate.getItems()));

            configuration.save(file);

        } catch (Exception ignored) {
            throw new DatabaseException(DatabaseException.DatabaseStatus.CRATES_SAVE);
        }
    }

    @SneakyThrows
    public void shutdown() {
        cache.values().forEach(this::updateCrate);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void loadCrate(@NonNull File file) {
        final FileConfiguration configuration = instance.getFlatFile().getConfiguration(file);

        if(!configuration.contains("name") || !configuration.contains("icon") || !configuration.contains("items")) return;

        final String name = configuration.getString("name");
        final ItemStack icon = ItemSerializer.read(configuration.getString("icon"))[0];

        final Set<ItemCrate> items = itemCrateGson.fromJson(configuration.getString("items"),
                ItemCrateType.builder().getType());

        final Crate crate = new Crate(name, icon);

        crate.getItems().addAll(items);

        createCrate(crate);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class ItemCrateType {
        static TypeToken<Set<ItemCrate>> builder() {
            return new TypeToken<Set<ItemCrate>>() {
            };
        }
    }

}
