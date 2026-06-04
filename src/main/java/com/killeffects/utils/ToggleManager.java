package com.killeffects.utils;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ToggleManager {
    private final Set<UUID> disabledEffects = new HashSet<>();

    public void toggle(UUID uuid) {
        if (disabledEffects.contains(uuid)) {
            disabledEffects.remove(uuid);
        } else {
            disabledEffects.add(uuid);
        }
    }

    public boolean isEnabled(UUID uuid) {
        return !disabledEffects.contains(uuid);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        if (enabled) {
            disabledEffects.remove(uuid);
        } else {
            disabledEffects.add(uuid);
        }
    }
}
