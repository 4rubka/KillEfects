package com.killeffects.effects.actions;

import com.killeffects.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record CommandAction(String command) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        String formatted = command
                .replace("%player%", killer.getName())
                .replace("%victim%", victim.getName());
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
    }
}
