/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.command;

import com.rarley.crates.CratesPlugin;
import com.rarley.crates.model.Crate;
import com.rarley.crates.settings.Messages;
import com.rarley.crates.utils.command.CommandInfo;
import com.rarley.crates.utils.inventory.InventoryBuilder;
import com.rarley.crates.utils.item.builder.ItemBuilder;
import com.rarley.crates.utils.item.nbt.UniversalTagCompound;
import com.rarley.crates.validate.CrateCreateValidate;
import com.rarley.crates.validate.CrateDeleteValidate;
import com.rarley.crates.validate.CrateEditValidate;
import com.rarley.crates.validate.PermissionValidate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
@CommandInfo(name = "crates")
public class CratesCommand implements CommandExecutor {

    private final CratesPlugin instance;
    private final Inventory confirmInventory;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, args);

        return true;
    }

    private void execute(CommandSender sender, String[] args) {
        if (args.length != 2 && args.length != 4) {
            final String[] help = {
                    "§c/crate <create, delete, edit> <name>",
                    "§c/crate give <player> <crate> <amount>"
            };

            sender.sendMessage(help);
        } else {
            final String arg = args[0].toLowerCase();
            final String arg2 = args[1].toLowerCase();

            if (args.length == 2 && (sender instanceof Player)) {
                final Player player = (Player) sender;

                switch (arg) {
                    case "create":
                        PermissionValidate.of(instance, "create", arg2).accept(player, this::startCreationProcess);
                        break;
                    case "delete":
                        PermissionValidate.of(instance, "delete", arg2).accept(player, this::startDeleteProcess);
                        break;
                    case "edit":
                        PermissionValidate.of(instance, "edit", arg2).accept(player, this::startEditProcess);
                        break;
                }

            } else if (arg.equals("give")) {
                final Messages messages = instance.getMessages(); // messages instance

                if (!sender.hasPermission("crates.admin.give")) {
                    sender.sendMessage(messages.get("no-permission", null));
                    return;
                }

                /* args (/crate give player crate amount) */
                final Player target = Bukkit.getPlayer(arg2);
                final String name = args[2].toLowerCase();
                final int amount = NumberUtils.toInt(args[3], 0);

                if (amount <= 0) {
                    sender.sendMessage(messages.get("invalid-amount", null));

                } else if (!instance.getCrateCache().existsCrate(name)) {
                    sender.sendMessage(messages.get("crate-not-exists", null));

                } else if (target == null) {
                    sender.sendMessage(messages.get("offline-player", null));

                } else {
                    final Crate crate = instance.getCrateCache().getCrate(name);

                    if (sender instanceof Player && target.getInventory().firstEmpty() == -1) {
                        sender.sendMessage(messages.get("inventory-full", null));
                        return;
                    }

                    if (crate.getItems().isEmpty()) {
                        sender.sendMessage(messages.get("crate-no-items", null));
                        return;
                    }

                    crate.givePlayer(target, amount);

                    final String message = messages.get("crate-gave", null);

                    sender.sendMessage(message
                            .replace("%amount%", Integer.toString(amount))
                            .replace("%player%", target.getName())
                            .replace("%crate%", crate.getName()));
                }
            }
        }
    }

    private void startCreationProcess(Player player, String name) {
        final CrateCreateValidate validate = new CrateCreateValidate(instance, name);
        final Consumer<Player> action =
                (consumer -> consumer.openInventory(buildConfirmationInventory(name, "crate-create")));

        validate.run(player, action);
    }

    private void startDeleteProcess(Player player, String name) {
        final CrateDeleteValidate validate = new CrateDeleteValidate(instance, name);
        final Consumer<Player> action =
                (consumer -> consumer.openInventory(buildConfirmationInventory(name, "crate-delete")));

        validate.run(player, action);
    }

    private void startEditProcess(Player player, String name) {
        final CrateEditValidate validate = new CrateEditValidate(instance, name);
        final Consumer<Player> action = (consumer -> instance.getCrateCache().getCrate(name).openEditInventory(consumer));

        validate.run(player, action);
    }

    private Inventory buildConfirmationInventory(String crate, String value) {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(confirmInventory);
        final UniversalTagCompound confirmNbt = new UniversalTagCompound()
                .set(value, true)
                .set("crate-confirm", true)
                .set("crate-name", crate);

        final ItemStack confirm = new ItemBuilder(Material.WOOL, 5, confirmNbt)
                .setLore("§7Click to confirm.")
                .setDisplayName("§aConfirm")
                .build();

        final UniversalTagCompound cancelNbt = new UniversalTagCompound()
                .set(value, true)
                .set("crate-cancel", true)
                .set("crate-name", crate);

        final ItemStack cancel = new ItemBuilder(Material.WOOL, 14, cancelNbt)
                .setLore("§7Click to cancel.")
                .setDisplayName("§cCancel")
                .build();

        return inventoryBuilder
                .addItem(confirm, 12)
                .addItem(cancel, 14)
                .build();
    }

}
