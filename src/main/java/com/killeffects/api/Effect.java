package com.killeffects.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Effect {
    private final String id;
    private final TreeMap<Long, List<Action>> scheduledActions = new TreeMap<>();

    public Effect(String id) {
        this.id = id;
    }

    public void addAction(long delayMs, Action action) {
        scheduledActions.computeIfAbsent(delayMs, k -> new java.util.ArrayList<>()).add(action);
    }

    public void play(Plugin plugin, Location location, Player killer, Entity victim) {
        for (Map.Entry<Long, List<Action>> entry : scheduledActions.entrySet()) {
            long delayTicks = entry.getKey() / 50; // Convert ms to ticks (approx)
            
            if (delayTicks <= 0) {
                entry.getValue().forEach(action -> action.execute(location, killer, victim));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entry.getValue().forEach(action -> action.execute(location, killer, victim));
                    }
                }.runTaskLater(plugin, delayTicks);
            }
        }
    }

    public String getId() {
        return id;
    }
}
