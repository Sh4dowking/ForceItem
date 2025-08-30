package com.sh4dowking.forceitem.perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sh4dowking.forceitem.Main;

/**
 * PerkManager - Manages all game perks and their lifecycle
 * 
 * Unlike ModifierManager which handles a single active modifier, PerkManager
 * can handle multiple active perks simultaneously. Perks are designed to be
 * stackable and work with any game mode or modifier.
 * 
 * Key Features:
 * - Multiple perks can be active at once
 * - Perks work with any modifier/game mode
 * - Automatic lifecycle management (start, end, player join/leave)
 * - Periodic updates for perks that need them
 * - Event delegation to all active perks
 * 
 * @author Sh4dowking
 * @version 2.0.0
 */
public class PerkManager {
    
    private final Main plugin;
    private final Map<String, GamePerk> registeredPerks = new HashMap<>();
    private final Set<String> activePerks = new HashSet<>();
    private int currentGameDurationSeconds = 300; // Default duration, updated when game starts
    
    public PerkManager(Main plugin) {
        this.plugin = plugin;
        
        // Register built-in perks
        registerPerk(new ToolsPerk(plugin));
        registerPerk(new BackpackPerk(plugin));
        registerPerk(new SaturationPerk(plugin));
        registerPerk(new SpeedPerk(plugin));
        registerPerk(new NightVisionPerk(plugin));
        registerPerk(new FastSmeltingPerk(plugin));
        registerPerk(new VeinMinerPerk(plugin));
    }
    
    /**
     * Register a new perk
     * 
     * @param perk The perk to register
     */
    public void registerPerk(GamePerk perk) {
        registeredPerks.put(perk.getPerkName(), perk);
    }
    
    /**
     * Enable a perk for the current game
     * 
     * @param perkName The name of the perk to enable
     * @return true if the perk was successfully enabled
     */
    public boolean enablePerk(String perkName) {
        if (registeredPerks.containsKey(perkName)) {
            activePerks.add(perkName);
            return true;
        }
        return false;
    }
    
    /**
     * Disable a perk
     * 
     * @param perkName The name of the perk to disable
     */
    public void disablePerk(String perkName) {
        activePerks.remove(perkName);
    }
    
    /**
     * Check if a perk is currently active
     * 
     * @param perkName The name of the perk to check
     * @return true if the perk is active
     */
    public boolean isPerkActive(String perkName) {
        return activePerks.contains(perkName);
    }
    
    /**
     * Get all registered perks
     * 
     * @return Map of perk names to perk instances
     */
    public Map<String, GamePerk> getRegisteredPerks() {
        return new HashMap<>(registeredPerks);
    }
    
    /**
     * Get the names of all currently active perks
     * 
     * @return Set of active perk names
     */
    public Set<String> getActivePerks() {
        return new HashSet<>(activePerks);
    }
    
    /**
     * Get the current game duration in seconds
     * 
     * @return Game duration in seconds
     */
    public int getCurrentGameDurationSeconds() {
        return currentGameDurationSeconds;
    }
    
    /**
     * Clear all active perks (manual reset - normally perks persist between rounds)
     * This method is available for admin use but is not called automatically.
     */
    public void clearActivePerks() {
        activePerks.clear();
    }
    
    // ===============================
    // GAME LIFECYCLE EVENTS
    // ===============================
    
    /**
     * Called when the game starts - notifies all active perks
     * 
     * @param players List of players in the game
     * @param jokersPerPlayer Number of jokers each player has
     * @param gameDurationSeconds Duration of the game in seconds
     */
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        this.currentGameDurationSeconds = gameDurationSeconds;
        
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onGameStart(players, jokersPerPlayer, gameDurationSeconds);
            }
        }
    }
    
    /**
     * Called when the game ends - notifies all active perks
     */
    public void onGameEnd() {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onGameEnd();
            }
        }
        // Note: We DON'T clear active perks here anymore!
        // This allows players to keep their perk selections between rounds
    }
    
    /**
     * Called when a new player joins during an active game
     * 
     * @param player The player who joined
     * @param isNewPlayer Whether this is a truly new player (not rejoining)
     */
    public void onPlayerJoin(Player player, boolean isNewPlayer) {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                // Only give perk items to truly new players, not rejoining players
                if (isNewPlayer) {
                    perk.onPlayerJoin(player);
                }
            }
        }
    }
    
    /**
     * Called when a player leaves during an active game
     * 
     * @param playerId The UUID of the player who left
     */
    public void onPlayerLeave(UUID playerId) {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onPlayerLeave(playerId);
            }
        }
    }
    
    /**
     * Called periodically during the game (every second from display task)
     * 
     * @param players List of current online players in the game
     */
    public void onPeriodicUpdate(List<Player> players) {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onPeriodicUpdate(players);
            }
        }
    }
    
    /**
     * Called when a player collects their target item
     * 
     * @param player The player who collected an item
     * @param collectedItem The material that was collected
     */
    public void onItemCollected(Player player, Material collectedItem) {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onItemCollected(player, collectedItem);
            }
        }
    }
    
    /**
     * Called when a player uses a joker
     * 
     * @param player The player who used a joker
     */
    public void onJokerUsed(Player player) {
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null) {
                perk.onJokerUsed(player);
            }
        }
    }
    
    // ===============================
    // UTILITY METHODS
    // ===============================
    
    /**
     * Check if any active perks are incompatible with a modifier
     * 
     * @param modifierName The name of the modifier to check
     * @return List of incompatible perk names
     */
    public List<String> getIncompatiblePerks(String modifierName) {
        List<String> incompatible = new ArrayList<>();
        
        for (String perkName : activePerks) {
            GamePerk perk = registeredPerks.get(perkName);
            if (perk != null && !perk.isCompatibleWith(modifierName)) {
                incompatible.add(perkName);
            }
        }
        
        return incompatible;
    }
    
    /**
     * Get a formatted string of all active perk names
     * 
     * @return Formatted string for display purposes
     */
    public String getActivePerkDisplay() {
        if (activePerks.isEmpty()) {
            return "None";
        }
        
        return String.join(", ", activePerks);
    }
}
