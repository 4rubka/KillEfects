package com.killeffects.commands;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.database.DatabaseManager;
import com.killeffects.gui.EffectGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final KillEffectsPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CommandManager(KillEffectsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            subcommands.add("gui");
            subcommands.add("toggle");
            if (sender.hasPermission("killeffects.admin")) {
                subcommands.add("reload");
            }
            return subcommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            new EffectGUI(plugin, player).open();
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            plugin.getToggleManager().toggle(player.getUniqueId());
            boolean enabled = plugin.getToggleManager().isEnabled(player.getUniqueId());
            String status = enabled ? "<green>enabled</green>" : "<red>disabled</red>";
            player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.prefix") + "Effects are now " + status));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("killeffects.admin")) {
                player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            plugin.reloadConfig();
            plugin.getEffectRegistry().loadEffects();
            player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.reloaded")));
            return true;
        }

        return true;
    }
}
