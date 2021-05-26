/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
                                      *
 Author github.com/pedroagrs          *
                                      *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.item;

import com.rarley.crates.utils.item.builder.ItemBuilder;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface Item<I extends ItemBuilder, N extends UniversalTagCompound> {

    N getNbt();

    I setNbt(UniversalTagCompound nbt);

    I setDisplayName(String name);

    I setItemMeta(ItemMeta itemMeta);

    I setLore(List<String> lore);

    I setLore(String... lines);

    I hideAllFlags();

    I hideFlags(ItemFlag... flags);

    ItemStack build();
}
