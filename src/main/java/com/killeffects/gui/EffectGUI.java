package com.killeffects.gui;

import com.killeffects.KillEffectsPlugin;
import com.killeffects.database.DatabaseManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EffectGUI implements Listener {
    private final KillEffectsPlugin plugin;
    private final Player player;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public EffectGUI(KillEffectsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, mm.deserialize("<gray>Select Kill Effect</gray>"));
        
        DatabaseManager.PlayerData data = plugin.getDatabaseManager().getPlayerData(player.getUniqueId());
        String selected = data.getSelectedEffect();

        int slot = 10;
        for (String effectId : plugin.getEffectRegistry().getEffects().keySet()) {
            boolean isSelected = effectId.equals(selected);
            
            ItemStack item = new ItemStack(isSelected ? Material.ENCHANTED_BOOK : Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(mm.deserialize("<yellow>" + effectId + "</yellow>"));
            
            List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
            lore.add(mm.deserialize("<gray>Status: " + (isSelected ? "<green>Selected" : "<red>Not Selected") + "</gray>"));
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<yellow>Click to select!</yellow>"));
            meta.lore(lore);
            
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
            
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
        }

        player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(player.getOpenInventory().getTopInventory())) return;
        if (event.getWhoClicked() != player) return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        // Extract effect name from display name (stripping MiniMessage tags)
        String effectId = mm.serialize(meta.displayName()).replaceAll("<[^>]*>", "");
        
        DatabaseManager.PlayerData data = plugin.getDatabaseManager().getPlayerData(player.getUniqueId());
        data.setSelectedEffect(effectId);
        
        player.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.prefix") + "Selected effect: <yellow>" + effectId + "</yellow>"));
        player.closeInventory();
    }
}
