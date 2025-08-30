package com.sh4dowking.forceitem.perks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sh4dowking.forceitem.Main;

/**
 * ToolsPerk - Provides all players with high-quality unbreakable netherite tools
 * 
 * This perk gives each player a set of powerful netherite tools at the start of the game:
 * - Unbreakable Netherite Axe with Efficiency V and Sharpness V
 * - Unbreakable Netherite Pickaxe with Efficiency V  
 * - Unbreakable Netherite Shovel with Efficiency V
 * 
 * Features:
 * - Tools are given at game start to all players
 * - New players joining mid-game also receive the tools
 * - All tools are unbreakable and highly enchanted
 * - Compatible with all modifiers and game modes
 * 
 * @author Sh4dowking
 * @version 2.0.0
 */
public class ToolsPerk extends GamePerk {
    
    public ToolsPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Tools";
    }
    
    @Override
    public String getDescription() {
        return "Provides all players with unbreakable netherite tools at game start";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.NETHERITE_PICKAXE;
    }
    
    @Override
    public List<String> getLore() {
        return List.of(
            "§7Grants powerful §8Netherite Tools §7to all players",
            "§7at the start of the game.",
            "",
            "§aProvided Tools:",
            "§8• §7Netherite Axe (Efficiency V, Sharpness V)",
            "§8• §7Netherite Pickaxe (Efficiency V)",
            "§8• §7Netherite Shovel (Efficiency V)",
            "",
            "§6§lAll tools are unbreakable!"
        );
    }
    
    @Override
    public boolean isCompatibleWith(String modifierName) {
        // Tools perk is compatible with all modifiers
        return true;
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Give tools to all players at game start
        for (Player player : players) {
            giveToolsToPlayer(player);
        }
        
        plugin.getLogger().info("[Perks] Provided netherite tools to " + players.size() + " players");
    }
    
    @Override
    public void onGameEnd() {
        // No cleanup needed - players keep their tools after game ends
        plugin.getLogger().info("[Perks] Tools perk ended - players keep their tools");
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Give tools to players who join mid-game
        giveToolsToPlayer(player);
    }
    
    /**
     * Give the complete set of netherite tools to a player
     * 
     * @param player The player to give tools to
     */
    private void giveToolsToPlayer(Player player) {
        // Create Netherite Axe with Efficiency V and Sharpness V
        ItemStack axe = createTool(Material.NETHERITE_AXE);
        axe.addUnsafeEnchantment(Enchantment.EFFICIENCY, 5);
        axe.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
        
        // Create Netherite Pickaxe with Efficiency V
        ItemStack pickaxe = createTool(Material.NETHERITE_PICKAXE);
        pickaxe.addUnsafeEnchantment(Enchantment.EFFICIENCY, 5);
        
        // Create Netherite Shovel with Efficiency V
        ItemStack shovel = createTool(Material.NETHERITE_SHOVEL);
        shovel.addUnsafeEnchantment(Enchantment.EFFICIENCY, 5);
        
        // Give tools to player (or drop if inventory is full)
        plugin.giveItemOrDrop(player, axe);
        plugin.giveItemOrDrop(player, pickaxe);
        plugin.giveItemOrDrop(player, shovel);
    }
    
    /**
     * Create a base tool with unbreakable property and custom name/lore
     * 
     * @param material The tool material
     * @param displayName The display name for the tool
     * @param lore The lore for the tool
     * @return The created tool ItemStack
     */
    private ItemStack createTool(Material material) {
        ItemStack tool = new ItemStack(material);
        ItemMeta meta = tool.getItemMeta();
        
        if (meta != null) {
            // Make unbreakable
            meta.setUnbreakable(true);
            
            tool.setItemMeta(meta);
        }
        
        return tool;
    }
}
