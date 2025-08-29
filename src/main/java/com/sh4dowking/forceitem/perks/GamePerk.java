package com.sh4dowking.forceitem.perks;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sh4dowking.forceitem.Main;

/**
 * Abstract base class for all game perks
 * 
 * Perks are special abilities or modifications that can be enabled for any game mode.
 * Unlike modifiers which change core gameplay mechanics, perks provide additional
 * benefits or features that enhance the player experience.
 * 
 * Key Differences from Modifiers:
 * - Perks can be combined (multiple perks active simultaneously)
 * - Perks work with any modifier/game mode
 * - Perks are additive enhancements, not core gameplay changes
 * 
 * @author Sh4dowking
 * @version 2.0.0
 */
public abstract class GamePerk {
    
    protected final Main plugin;
    
    public GamePerk(Main plugin) {
        this.plugin = plugin;
    }
    
    // Abstract methods that must be implemented
    public abstract String getPerkName();
    public abstract String getDescription();
    public abstract Material getDisplayMaterial();
    public abstract List<String> getLore();
    public abstract void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds);
    public abstract void onGameEnd();
    public abstract void onPlayerJoin(Player player);
    
    // Optional methods with default implementations
    public void onPlayerLeave(UUID playerId) {}
    public void onPeriodicUpdate(List<Player> players) {}
    public void onItemCollected(Player player, Material collectedItem) {}
    public void onJokerUsed(Player player) {}
    public boolean isCompatibleWith(String modifierName) { return true; }
}
