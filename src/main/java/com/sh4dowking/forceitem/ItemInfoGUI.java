package com.sh4dowking.forceitem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemInfoGUI - Simple item display interface
 * 
 * Provides a clean, minimalist GUI for displaying target items to players.
 * Shows only the vanilla item in the center without additional information
 * to maintain simplicity and avoid clutter.
 * 
 * Features:
 * - Clean 3x9 inventory layout
 * - Centered item display
 * - Click/drag protection
 * - Natural close behavior
 * 
 * @author Sh4dowking
 * @version 1.0
 */
public class ItemInfoGUI implements Listener {
    
    /**
     * Open item information GUI for a specific material
     * 
     * @param player Player to show GUI to
     * @param material Material to display information for
     */
    public void openItemInfo(Player player, Material material) {
        String itemName = formatMaterialName(material);
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.WHITE + "" + ChatColor.BOLD + "Target: " + ChatColor.AQUA + itemName);
        
        // Create simple centered display
        createSimpleGUI(gui, material);
        
        player.openInventory(gui);
    }
    
    /**
     * Create a minimalist GUI with centered item display
     * 
     * @param gui Inventory to populate
     * @param material Material to display
     */
    private void createSimpleGUI(Inventory gui, Material material) {
        // Display only the vanilla item in the center (slot 13 of 27-slot inventory)
        ItemStack displayItem = new ItemStack(material);
        gui.setItem(13, displayItem);
        
        // Fill remaining slots with decorative spacers
        fillEmptySlots(gui);
    }
    
    /**
     * Fill empty inventory slots with decorative glass panes
     * 
     * @param gui Inventory to fill
     */
    private void fillEmptySlots(Inventory gui) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" "); // Empty name to hide tooltip
            filler.setItemMeta(fillerMeta);
        }
        
        // Fill all empty slots with filler items
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
    }
    
    /**
     * Prevent players from clicking and taking items from the GUI
     * 
     * @param event InventoryClickEvent to handle
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Target:")) {
            event.setCancelled(true); // Block all clicking in item info GUI
        }
    }
    
    /**
     * Prevent players from dragging items in the GUI
     * 
     * @param event InventoryDragEvent to handle
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Target:")) {
            event.setCancelled(true); // Block all dragging in item info GUI
        }
    }
    
    /**
     * Format Material enum names into readable display names
     * Converts GOLDEN_APPLE to "Golden Apple"
     * 
     * @param material Material to format
     * @return Formatted display name
     */
    private String formatMaterialName(Material material) {
        String raw = material.name().toLowerCase().replace("_", " ");
        String[] words = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
