/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.cache.crate.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.rarley.crates.utils.item.ItemSerializer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@RequiredArgsConstructor
public class ItemStackAdapter extends TypeAdapter<ItemStack> {

    private final String placeholder;

    @Override
    public void write(JsonWriter writer, ItemStack value) throws IOException {
        final String serialize = ItemSerializer.write(value);

        writer.beginObject();
        writer.name(placeholder).value(serialize);
        writer.endObject();
    }

    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        reader.beginObject();

        ItemStack item = new ItemStack(Material.STONE);

        while (reader.hasNext()) {
            if (reader.nextName().equals(placeholder)) {
                item = ItemSerializer.read(reader.nextString())[0];
                break;
            }
        }

        reader.endObject();

        return item;
    }
}
