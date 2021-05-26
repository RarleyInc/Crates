/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.nms;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class NMSUtils {

    private static final String SERVER_VERSION = Bukkit
            .getServer()
            .getClass()
            .getPackage()
            .getName()
            .substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

    public static final Class<?> NBT_COMPOUND = getNMSClass("NBTTagCompound");

    @SneakyThrows
    public static Class<?> getCraftBukkitClass(String category, String className) {
        return Class.forName(String.format("org.bukkit.craftbukkit.%s.%s.%s", SERVER_VERSION, category, className));
    }

    @SneakyThrows
    public static Class<?> getNMSClass(String className) {
        return Class.forName(String.format("net.minecraft.server.%s.%s", SERVER_VERSION, className));
    }

    @SneakyThrows
    public static Object craftNmsItemStack(@NonNull ItemStack item) {
        return getCraftBukkitClass("inventory", "CraftItemStack")
                .getMethod("asNMSCopy", ItemStack.class)
                .invoke(null, item);
    }

    @SneakyThrows
    public static ItemStack craftItemStack(@NonNull Object item) {
        return (ItemStack) getCraftBukkitClass("inventory", "CraftItemStack")
                .getMethod("asBukkitCopy", NMSUtils.getNMSClass("ItemStack"))
                .invoke(null, item);
    }
}
