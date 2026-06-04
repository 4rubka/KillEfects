package com.killeffects.utils;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.database.DatabaseManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KillEffectsPlaceholders extends PlaceholderExpansion {
    private final KillEffectsPlugin plugin;

    public KillEffectsPlaceholders(KillEffectsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "killeffects";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GeminiCLI";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        DatabaseManager.PlayerData data = plugin.getDatabaseManager().getPlayerData(player.getUniqueId());

        return switch (params.toLowerCase()) {
            case "kills" -> String.valueOf(data.getKills());
            case "streak" -> String.valueOf(data.getHighestStreak());
            case "selected" -> data.getSelectedEffect();
            case "toggle" -> String.valueOf(plugin.getToggleManager().isEnabled(player.getUniqueId()));
            default -> null;
        };
    }
}
