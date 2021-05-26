/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.item.builder;

import com.rarley.crates.utils.item.Item;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemBuilder implements Item<ItemBuilder, UniversalTagCompound> {

    protected final ItemStack itemStack;

    protected ItemMeta itemMeta;
    protected UniversalTagCompound nbt;

    public ItemBuilder(@NonNull ItemStack itemStack, UniversalTagCompound nbt) {
        this.itemStack = itemStack;
        this.nbt = (nbt != null ? nbt : new UniversalTagCompound(itemStack));
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(@NonNull Material material, int data) {
        this.itemStack = new ItemStack(material, 1, (short) data);
        this.nbt = new UniversalTagCompound(itemStack);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(@NonNull Material material, int data, UniversalTagCompound nbt) {
        this.itemStack = new ItemStack(material, 1, (short) data);
        this.nbt = (nbt != null ? nbt : new UniversalTagCompound(itemStack));
        this.itemMeta = itemStack.getItemMeta();
    }

    /***
     * @apiNote If the item does not have a meta it will be created
     * @param item to create builder
     * @return builder new instance
     */

    public static ItemBuilder of(@NonNull ItemStack item) {
        return new ItemBuilder(item, new UniversalTagCompound(item))
                .setItemMeta(item.getItemMeta());
    }

    @Override
    public ItemBuilder setNbt(UniversalTagCompound nbt) {
        this.nbt = nbt;

        return this;
    }

    @Override
    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;

        return this;
    }

    @Override
    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        return this;
    }

    @Override
    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore.stream().map(r -> r.replace("&", "§"))
                .collect(Collectors.toList()));

        return this;
    }

    @Override
    public ItemBuilder setLore(String... lines) {
        itemMeta.setLore(Arrays.asList(lines));

        return this;
    }

    @Override
    public ItemBuilder hideAllFlags() {
        itemMeta.addItemFlags(ItemFlag.values());
        return this;
    }

    @Override
    public ItemBuilder hideFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);

        return this;
    }

    @Override
    public ItemStack build() {
        return nbt.build(itemStack, itemMeta);
    }
}
