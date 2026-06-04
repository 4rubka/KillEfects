package com.killeffects.effects.actions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.killeffects.KillEffectsPlugin;
import com.killeffects.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public record LightningAction(boolean fake) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        if (!fake) {
            location.getWorld().strikeLightning(location);
            return;
        }

        // Fake lightning logic using ProtocolLib
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        int entityId = (int) (Math.random() * 100000) + 999999;
        
        packet.getIntegers().write(0, entityId);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.LIGHTNING_BOLT);
        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        double radiusSquared = 64 * 64;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) && 
                player.getLocation().distanceSquared(location) <= radiusSquared) {
                
                if (KillEffectsPlugin.getInstance().getToggleManager().isEnabled(player.getUniqueId())) {
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
