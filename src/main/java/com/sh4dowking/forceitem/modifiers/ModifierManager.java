package com.sh4dowking.forceitem.modifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sh4dowking.forceitem.Main;

/**
 * ModifierManager - Manages all game modifiers
 * 
 * Handles registration of modifiers and delegates modifier-specific
 * game events to the active modifier.
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public class ModifierManager {
    
    private final Main plugin;
    private final Map<String, GameModifier> registeredModifiers = new LinkedHashMap<>();
    private GameModifier activeModifier = null;
    
    public ModifierManager(Main plugin) {
        this.plugin = plugin;
        registerDefaultModifiers();
    }
    
    /**
     * Register all default modifiers
     */
    private void registerDefaultModifiers() {
        registerModifier(new DoubleTroubleModifier(plugin));
        // Future modifiers will be registered here
    }
    
    /**
     * Register a new game modifier
     * 
     * @param modifier The modifier to register
     */
    public void registerModifier(GameModifier modifier) {
        registeredModifiers.put(modifier.getModifierName(), modifier);
    }
    
    /**
     * Set the active modifier for the current game
     * 
     * @param modifierName The name of the modifier to activate (null for no modifier)
     */
    public void setActiveModifier(String modifierName) {
        if (modifierName == null || modifierName.equals("None")) {
            activeModifier = null;
        } else {
            activeModifier = registeredModifiers.get(modifierName);
        }
    }
    
    /**
     * Get the currently active modifier
     * 
     * @return The active modifier, or null if no modifier is active
     */
    public GameModifier getActiveModifier() {
        return activeModifier;
    }
    
    /**
     * Get all registered modifiers
     * 
     * @return Map of modifier names to modifier instances
     */
    public Map<String, GameModifier> getRegisteredModifiers() {
        return new HashMap<>(registeredModifiers);
    }
    
    /**
     * Check if any modifier is currently active
     * 
     * @return true if a modifier is active
     */
    public boolean hasActiveModifier() {
        return activeModifier != null;
    }
    
    // ===============================
    // MODIFIER EVENT DELEGATION
    // ===============================
    
    /**
     * Called when a game starts
     */
    public void onGameStart(List<Player> players, int jokersPerPlayer) {
        if (activeModifier != null) {
            activeModifier.onGameStart(players, jokersPerPlayer);
        }
    }
    
    /**
     * Called when a player collects their target item
     * 
     * @param player The player who collected the item
     * @param collectedItem The material that was collected
     * @return true if a modifier handled the collection, false for default behavior
     */
    public boolean onTargetCollected(Player player, Material collectedItem) {
        if (activeModifier != null) {
            return activeModifier.onTargetCollected(player, collectedItem);
        }
        return false; // No modifier active, use default behavior
    }
    
    /**
     * Get the target item(s) for a specific player
     * 
     * @param playerId The UUID of the player
     * @return List of target materials
     */
    public List<Material> getPlayerTargets(UUID playerId) {
        if (activeModifier != null) {
            return activeModifier.getPlayerTargets(playerId);
        }
        
        // Default behavior: return single target from game manager
        Material singleTarget = plugin.getPlayerTarget(playerId);
        if (singleTarget != null) {
            return Arrays.asList(singleTarget);
        }
        return new ArrayList<>();
    }
    
    /**
     * Modify the display text shown to players
     * 
     * @param player The player viewing the display
     * @param baseDisplay The default display text
     * @return Modified display text
     */
    public String modifyDisplayText(Player player, String baseDisplay) {
        if (activeModifier != null) {
            return activeModifier.modifyDisplayText(player, baseDisplay);
        }
        return baseDisplay; // No modifier, return original text
    }
    
    /**
     * Called when a player uses a joker
     * 
     * @param player The player using the joker
     * @return true if a modifier handled the joker use, false for default behavior
     */
    public boolean onJokerUsed(Player player) {
        if (activeModifier != null) {
            return activeModifier.onJokerUsed(player);
        }
        return false; // No modifier active, use default behavior
    }
    
    /**
     * Called when the game ends
     */
    public void onGameEnd() {
        if (activeModifier != null) {
            activeModifier.onGameEnd();
        }
        activeModifier = null; // Clear active modifier
    }
    
    /**
     * Called when a new player joins during an active game
     * 
     * @param player The player who joined
     */
    public void onPlayerJoin(Player player) {
        if (activeModifier != null) {
            activeModifier.onPlayerJoin(player);
        }
    }
    
    /**
     * Called when a player leaves during an active game
     * 
     * @param playerId The UUID of the player who left
     */
    public void onPlayerLeave(UUID playerId) {
        if (activeModifier != null) {
            activeModifier.onPlayerLeave(playerId);
        }
    }
    
    /**
     * Called when an admin skips a player's targets
     * 
     * @param player The player whose targets are being skipped
     * @return true if a modifier handled the skip, false for default behavior
     */
    public boolean onPlayerSkipped(Player player) {
        if (activeModifier != null) {
            return activeModifier.onPlayerSkipped(player);
        }
        return false; // No modifier active, use default behavior
    }
    
    /**
     * Get the name of the currently active modifier
     * 
     * @return The modifier name, or "None" if no modifier is active
     */
    public String getActiveModifierName() {
        return activeModifier != null ? activeModifier.getModifierName() : "None";
    }
}
