/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ItemCrate {

    private final ItemStack item;

    @Setter
    private double chance;
    @Setter
    private int id;

    public boolean hasChance() {
        return chance > 0.0d;
    }


}
