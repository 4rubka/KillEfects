package com.killeffects.effects.actions;

import com.killeffects.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record ModelAction(String provider, String modelId, long durationMs) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        if (provider.equalsIgnoreCase("itemsadder") && Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            // IA logic usually involves custom entities or furniture
            // For brevity in this prototype, we trigger the command if IA is present
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iaentity spawn " + modelId + " " + 
                    location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
        } else if (provider.equalsIgnoreCase("oraxen") && Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            // Oraxen logic
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "oraxen furniture spawn " + modelId + " " + 
                    location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ());
        }
    }
}
