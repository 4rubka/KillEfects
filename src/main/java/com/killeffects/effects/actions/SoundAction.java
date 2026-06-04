package com.killeffects.effects.actions;

import com.killeffects.api.Action;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record SoundAction(Sound sound, float volume, float pitch) implements Action {
    @Override
    public void execute(Location location, Player killer, Entity victim) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
