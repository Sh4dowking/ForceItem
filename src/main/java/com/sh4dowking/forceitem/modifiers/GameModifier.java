package com.sh4dowking.forceitem.modifiers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sh4dowking.forceitem.Main;

/**
 * Abstract base class for all game modifiers
 * 
 * Game modifiers alter the standard ForceItem gameplay mechanics.
 * Each modifier can override specific aspects like target assignment,
 * collection handling, scoring, and display formatting.
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public abstract class GameModifier {
    
    protected final Main plugin;
    protected final String modifierName;
    protected final String description;
    protected final ItemStack displayItem;
    
    /**
     * Constructor for game modifiers
     * 
     * @param plugin The main plugin instance
     * @param modifierName The name of this modifier
     * @param description A description of what this modifier does
     * @param displayItem The item to display in GUI menus
     */
    public GameModifier(Main plugin, String modifierName, String description, ItemStack displayItem) {
        this.plugin = plugin;
        this.modifierName = modifierName;
        this.description = description;
        this.displayItem = displayItem;
    }
    
    // ===============================
    // ABSTRACT METHODS - MUST IMPLEMENT
    // ===============================
    
    /**
     * Called when a game starts to initialize modifier-specific data
     * 
     * @param players List of players participating in the game
     * @param jokersPerPlayer Number of jokers each player receives
     */
    public abstract void onGameStart(List<Player> players, int jokersPerPlayer);
    
    /**
     * Called when a player collects their target item
     * Modifiers can override the default collection behavior
     * 
     * @param player The player who collected the item
     * @param collectedItem The material that was collected
     * @return true if the modifier handled the collection, false to use default behavior
     */
    public abstract boolean onTargetCollected(Player player, Material collectedItem);
    
    /**
     * Get the target item(s) for a specific player
     * 
     * @param playerId The UUID of the player
     * @return List of target materials (normally 1, but modifiers can have multiple)
     */
    public abstract List<Material> getPlayerTargets(UUID playerId);
    
    /**
     * Modify the display text shown to players
     * 
     * @param player The player viewing the display
     * @param baseDisplay The default display text
     * @return Modified display text
     */
    public abstract String modifyDisplayText(Player player, String baseDisplay);
    
    /**
     * Called when a player uses a joker
     * Modifiers can override joker behavior
     * 
     * @param player The player using the joker
     * @return true if the modifier handled the joker use, false for default behavior
     */
    public abstract boolean onJokerUsed(Player player);
    
    // ===============================
    // OPTIONAL OVERRIDE METHODS
    // ===============================
    
    /**
     * Called when the game ends
     * Override to clean up modifier-specific data
     */
    public void onGameEnd() {
        // Default: no cleanup needed
    }
    
    /**
     * Called when a new player joins during an active game
     * Override to initialize modifier data for the new player
     * 
     * @param player The player who joined
     */
    public void onPlayerJoin(Player player) {
        // Default: no special handling needed
    }
    
    /**
     * Called when a player leaves during an active game
     * Override to clean up modifier data for the leaving player
     * 
     * @param playerId The UUID of the player who left
     */
    public void onPlayerLeave(UUID playerId) {
        // Default: no cleanup needed
    }
    
    /**
     * Called when an admin skips a player's targets
     * Override to handle target skipping for the modifier
     * 
     * @param player The player whose targets are being skipped
     * @return true if the modifier handled the skip, false for default behavior
     */
    public boolean onPlayerSkipped(Player player) {
        // Default: no special handling, let standard logic handle it
        return false;
    }
    
    // ===============================
    // GETTER METHODS
    // ===============================
    
    public String getModifierName() {
        return modifierName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }
    
    /**
     * Check if this modifier requires multiple targets per player
     * 
     * @return true if this modifier uses multiple targets
     */
    public boolean isMultiTarget() {
        return false; // Default: single target
    }
    
    /**
     * Get the maximum number of targets this modifier assigns per player
     * 
     * @return Maximum target count (1 for standard, 2+ for multi-target modifiers)
     */
    public int getMaxTargetsPerPlayer() {
        return 1; // Default: single target
    }
}
