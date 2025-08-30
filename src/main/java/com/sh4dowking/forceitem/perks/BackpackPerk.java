package com.sh4dowking.forceitem.perks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sh4dowking.forceitem.Main;

/**
 * BackpackPerk - Provides players with a personal storage backpack during the game
 * 
 * This perk gives each player a 27-slot personal storage backpack that they can
 * access throughout the game. The backpack is useful for storing items, organizing
 * inventory, and keeping important resources safe.
 * 
 * Features:
 * - 27-slot personal storage per player
 * - Right-click to access backpack
 * - Backpack persists throughout the game
 * - Automatically given to new players who join mid-game (when perk is active)
 * - Compatible with all modifiers and game modes
 * 
 * @author Sh4dowking
 * @version 2.1.0
 */
public class BackpackPerk extends GamePerk {
    
    public BackpackPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Backpack";
    }
    
    @Override
    public String getDescription() {
        return "Provides each player with a 27-slot personal storage backpack";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.CHEST;
    }
    
    @Override
    public List<String> getLore() {
        return List.of(
            "§7Gives each player a §6personal backpack",
            "§7with 27 slots for item storage.",
            "",
            "§aFeatures:",
            "§8• §727-slot personal storage",
            "§8• §7Right-click to access"
        );
    }
    
    @Override
    public boolean isCompatibleWith(String modifierName) {
        // Backpack perk is compatible with all modifiers
        return true;
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Create backpack inventories for all players
        plugin.createBackpacksForAllOnlinePlayers();
        
        // Give backpack item to all players at game start
        for (Player player : players) {
            giveBackpackToPlayer(player);
        }
        
        plugin.getLogger().info("[Perks] Gave backpacks to " + players.size() + " players");
    }
    
    @Override
    public void onGameEnd() {
        // Backpack cleanup is handled by the Main plugin when clearing player backpack inventories
        plugin.getLogger().info("[Perks] Backpack perk ended - inventories will be cleaned up by main plugin");
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Create backpack inventory for the new player
        plugin.createPlayerBackpack(player.getUniqueId(), player.getName());
        
        // Give backpack item to players who join mid-game
        giveBackpackToPlayer(player);
    }
    
    /**
     * Give a backpack item to a player and create their personal inventory
     * 
     * @param player The player to give the backpack to
     */
    private void giveBackpackToPlayer(Player player) {
        // Create the backpack item
        ItemStack backpack = plugin.createBackpackItem();
        plugin.giveItemOrDrop(player, backpack);
        
        // The personal backpack inventory is created by the Main plugin when the player joins
        // We don't need to create it here as it's handled in the Main.onPlayerJoin event
    }
}
