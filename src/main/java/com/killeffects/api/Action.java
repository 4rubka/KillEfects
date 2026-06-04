package com.killeffects.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Action {
    /**
     * Executes the action at a specific location.
     * @param location The location where the effect should trigger (usually victim's death location)
     * @param killer The player who killed
     * @param victim The entity who was killed
     */
    void execute(Location location, Player killer, Entity victim);
}
