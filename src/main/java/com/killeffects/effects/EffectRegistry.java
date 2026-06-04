package com.killeffects.effects;

import com.killeffects.api.Effect;
import com.killeffects.effects.actions.ParticleAction;
import com.killeffects.effects.actions.SoundAction;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EffectRegistry {
    private final Plugin plugin;
    private final Map<String, Effect> effects = new HashMap<>();

    public EffectRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadEffects() {
        effects.clear();
        File file = new File(plugin.getDataFolder(), "effects.yml");
        if (!file.exists()) {
            plugin.saveResource("effects.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("effects");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Effect effect = new Effect(key);
            ConfigurationSection actionsSection = section.getConfigurationSection(key + ".actions");
            if (actionsSection == null) continue;

            for (String delayStr : actionsSection.getKeys(false)) {
                long delay = Long.parseLong(delayStr);
                ConfigurationSection actionData = actionsSection.getConfigurationSection(delayStr);
                
                if (actionData.contains("sound")) {
                    try {
                        effect.addAction(delay, new SoundAction(
                                Sound.valueOf(actionData.getString("sound").toUpperCase()),
                                (float) actionData.getDouble("volume", 1.0),
                                (float) actionData.getDouble("pitch", 1.0)
                        ));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound '" + actionData.getString("sound") + "' in effect " + key);
                    }
                }
                
                if (actionData.contains("particle")) {
                    try {
                        effect.addAction(delay, new ParticleAction(
                                Particle.valueOf(actionData.getString("particle").toUpperCase()),
                                actionData.getInt("count", 10),
                                actionData.getDouble("offsetX", 0.5),
                                actionData.getDouble("offsetY", 0.5),
                                actionData.getDouble("offsetZ", 0.5),
                                actionData.getDouble("extra", 0.1)
                        ));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid particle '" + actionData.getString("particle") + "' in effect " + key);
                    }
                }

                if (actionData.contains("lightning")) {
                    effect.addAction(delay, new com.killeffects.effects.actions.LightningAction(
                            actionData.getBoolean("lightning.fake", true)
                    ));
                }

                if (actionData.contains("command")) {
                    effect.addAction(delay, new com.killeffects.effects.actions.CommandAction(
                            actionData.getString("command")
                    ));
                }

                if (actionData.contains("structure")) {
                    ConfigurationSection struct = actionData.getConfigurationSection("structure");
                    effect.addAction(delay, new com.killeffects.effects.actions.DisplayEntityAction(
                            org.bukkit.Material.valueOf(struct.getString("material", "STONE")),
                            struct.getLong("duration", 3000L),
                            new org.joml.Vector3f(
                                    (float) struct.getDouble("scale.x", 1.0),
                                    (float) struct.getDouble("scale.y", 1.0),
                                    (float) struct.getDouble("scale.z", 1.0)
                            ),
                            new org.joml.Vector3f(
                                    (float) struct.getDouble("offset.x", 0.0),
                                    (float) struct.getDouble("offset.y", 0.0),
                                    (float) struct.getDouble("offset.z", 0.0)
                            )
                    ));
                }
            }
            effects.put(key, effect);
        }
    }

    public Effect getEffect(String id) {
        return effects.get(id);
    }

    public Map<String, Effect> getEffects() {
        return effects;
    }
}
