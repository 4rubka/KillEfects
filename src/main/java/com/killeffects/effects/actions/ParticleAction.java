package com.killeffects.effects.actions;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record ParticleAction(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        double radius = 32.0; // View distance for effects
        double radiusSquared = radius * radius;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) && 
                player.getLocation().distanceSquared(location) <= radiusSquared) {
                
                if (KillEffectsPlugin.getInstance().getToggleManager().isEnabled(player.getUniqueId())) {
                    if (particle.getDataType() == Float.class) {
                        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, 1.0f);
                    } else if (particle.getDataType() == Integer.class) {
                        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, 0);
                    } else if (particle.getDataType() == org.bukkit.inventory.ItemStack.class) {
                        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE));
                    } else if (particle.getDataType() == org.bukkit.block.data.BlockData.class) {
                        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, org.bukkit.Bukkit.createBlockData(org.bukkit.Material.STONE));
                    } else {
                        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
                    }
                }
            }
        }
    }
}
