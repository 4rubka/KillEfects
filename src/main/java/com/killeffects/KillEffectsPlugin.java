package com.killeffects;

import com.killeffects.database.DatabaseManager;
import com.killeffects.effects.EffectRegistry;
import com.killeffects.listeners.KillListener;
import org.bukkit.plugin.java.JavaPlugin;

public class KillEffectsPlugin extends JavaPlugin {
    private static KillEffectsPlugin instance;
    private DatabaseManager databaseManager;
    private EffectRegistry effectRegistry;
    private com.killeffects.utils.ToggleManager toggleManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        toggleManager = new com.killeffects.utils.ToggleManager();

        // Initialize Database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize(
                getConfig().getString("database.type", "sqlite"),
                getConfig().getString("database.host", "localhost"),
                getConfig().getInt("database.port", 3306),
                getConfig().getString("database.name", "killeffects"),
                getConfig().getString("database.username", "root"),
                getConfig().getString("database.password", "")
        );

        // Initialize Registry
        effectRegistry = new EffectRegistry(this);
        effectRegistry.loadEffects();

        // Register Listeners
        getServer().getPluginManager().registerEvents(new KillListener(this), this);

        // Register Commands
        com.killeffects.commands.CommandManager cmdManager = new com.killeffects.commands.CommandManager(this);
        getCommand("killeffects").setExecutor(cmdManager);
        getCommand("killeffects").setTabCompleter(cmdManager);

        // Register Placeholders
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new com.killeffects.utils.KillEffectsPlaceholders(this).register();
        }

        getLogger().info("KillEffects has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("KillEffects has been disabled.");
    }

    public static KillEffectsPlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EffectRegistry getEffectRegistry() {
        return effectRegistry;
    }

    public com.killeffects.utils.ToggleManager getToggleManager() {
        return toggleManager;
    }
}
