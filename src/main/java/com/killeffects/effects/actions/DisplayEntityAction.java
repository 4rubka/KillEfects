package com.killeffects.effects.actions;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public record DisplayEntityAction(Material material, long durationMs, Vector3f scale, Vector3f offset) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        Location spawnLoc = location.clone().add(offset.x, offset.y, offset.z);
        
        BlockDisplay display = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, entity -> {
            entity.setBlock(material.createBlockData());
            entity.setBrightness(new Display.Brightness(15, 15));
            
            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(scale);
            entity.setTransformation(transformation);
        });

        // Cleanup task
        Bukkit.getScheduler().runTaskLater(KillEffectsPlugin.getInstance(), display::remove, durationMs / 50);
    }
}
