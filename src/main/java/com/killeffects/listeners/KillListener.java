package com.killeffects.listeners;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.api.Effect;
import com.killeffects.database.DatabaseManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillListener implements Listener {
    private final KillEffectsPlugin plugin;
    private final Map<UUID, Integer> currentStreaks = new HashMap<>();

    public KillListener(KillEffectsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Reset victim's streak
        currentStreaks.remove(victim.getUniqueId());

        if (killer == null) return;

        // Process Kill for Database
        DatabaseManager.PlayerData data = plugin.getDatabaseManager().getPlayerData(killer.getUniqueId());
        data.addKill();

        // Streak logic
        int newStreak = currentStreaks.getOrDefault(killer.getUniqueId(), 0) + 1;
        currentStreaks.put(killer.getUniqueId(), newStreak);
        data.setHighestStreak(newStreak);

        // Play Selected Effect
        playEffect(killer, victim);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) return; // Handled by onPlayerDeath
        
        Entity victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer == null) return;
        if (!plugin.getConfig().getBoolean("settings.mobs_enabled", false)) return;

        playEffect(killer, victim);
    }

    private void playEffect(Player killer, Entity victim) {
        DatabaseManager.PlayerData data = plugin.getDatabaseManager().getPlayerData(killer.getUniqueId());
        String effectId = data.getSelectedEffect();
        
        if (effectId != null && !effectId.equalsIgnoreCase("none")) {
            Effect effect = plugin.getEffectRegistry().getEffect(effectId);
            if (effect != null) {
                effect.play(plugin, victim.getLocation(), killer, victim);
            }
        }
    }
}
