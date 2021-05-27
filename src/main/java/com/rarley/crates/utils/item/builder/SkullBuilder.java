/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.item.builder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class SkullBuilder extends ItemBuilder {

    public SkullBuilder() {
        super(Material.SKULL_ITEM, 3);
    }

    protected String url;
    protected String owner;

    public SkullBuilder setOwner(String owner) {
        this.owner = owner;

        return this;
    }

    public SkullBuilder setUrl(String url) {
        this.url = url;

        return this;
    }

    @Override
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (url != null)
            mutateItemMeta((SkullMeta) itemMeta, url);

        if (owner != null)
            ((SkullMeta) itemMeta).setOwner(owner);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    protected void mutateItemMeta(SkullMeta meta, String url) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder()
                .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
