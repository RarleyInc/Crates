/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.item.nbt;

import com.rarley.crates.utils.nms.NMSUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

import static com.rarley.crates.utils.nms.NMSUtils.NBT_COMPOUND;

public class UniversalTagCompound {

    private final UniversalTagAccess access;

    protected ItemStack item;
    protected Object nmsItemStack;

    @SneakyThrows
    public UniversalTagCompound(@NonNull ItemStack item) {
        final Object nmsItemStack = NMSUtils.craftNmsItemStack(item);

        if (nmsItemStack == null) {
            this.access = new UniversalTagAccess(NBT_COMPOUND.newInstance());
            return;
        }

        final Field field = nmsItemStack.getClass().getDeclaredField("tag");

        field.setAccessible(true);

        final Object nbt = (field.get(nmsItemStack) == null
                ? NBT_COMPOUND.newInstance()
                : field.get(nmsItemStack));

        this.nmsItemStack = nmsItemStack;
        this.access = new UniversalTagAccess(nbt);
        this.item = applyNbt(item, (item.hasItemMeta() ? item.getItemMeta() : null), nbt);
    }

    @SneakyThrows
    public UniversalTagCompound() {
        this.access = new UniversalTagAccess(NBT_COMPOUND.newInstance());
    }

    public boolean has(String key) {
        return access.hasKey(key);
    }

    public UniversalTagCompound set(String key, Object value) {
        access.set(key, value);

        if (nmsItemStack != null && item != null)
            applyNbt(item, (item.hasItemMeta() ? item.getItemMeta() : null), access.getCompound());

        return this;
    }

    public Object get(String key, Class<?> type) {
        return access.get(key, type);
    }

    public ItemStack build(@NonNull ItemStack item, ItemMeta meta) {
        return applyNbt(item, meta, access.getCompound());
    }

    @SneakyThrows
    private ItemStack applyNbt(ItemStack item, ItemMeta itemMeta, Object nbt) {
        final Object nmsItemStack = NMSUtils.craftNmsItemStack(item);

        nmsItemStack.getClass()
                .getMethod("setTag", nbt.getClass())
                .invoke(nmsItemStack, nbt);

        final ItemStack finalItem = NMSUtils.craftItemStack(nmsItemStack);

        if(itemMeta != null) {
            final ItemMeta finalMeta = finalItem.getItemMeta();

            finalMeta.setDisplayName(itemMeta.getDisplayName());
            finalMeta.setLore(itemMeta.getLore());

            itemMeta.getItemFlags().forEach(finalMeta::addItemFlags);

            finalItem.setItemMeta(finalMeta);
        }

        return finalItem;
    }

}
