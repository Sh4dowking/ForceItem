package com.sh4dowking.forceitem.perks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sh4dowking.forceitem.Main;

/**
 * SaturationPerk - Provides all players with permanent saturation effect during the game
 * 
 * This perk applies the Saturation potion effect to all players throughout the entire
 * duration of the game. Players will not lose hunger and will have increased health
 * regeneration capabilities.
 * 
 * Features:
 * - Applied to all players when game starts
 * - Automatically applied to new players who join mid-game
 * - Effect is maintained throughout the game with periodic refresh
 * - Compatible with all modifiers and game modes
 * - Effect is properly removed when the game ends
 * 
 * @author Sh4dowking
 * @version 2.0.0
 */
public class SaturationPerk extends GamePerk {
    
    private static final int SATURATION_AMPLIFIER = 0; // Level 1 saturation
    private static final boolean AMBIENT = false;
    private static final boolean PARTICLES = false;
    private static final boolean ICON = true;
    
    private int gameDurationSeconds = 300; // Default 5 minutes, will be updated when game starts
    
    public SaturationPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Saturation";
    }
    
    @Override
    public String getDescription() {
        return "Provides permanent saturation effect to all players during the game";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.GOLDEN_APPLE;
    }
    
    @Override
    public List<String> getLore() {
        return List.of(
            "§7Grants §6Saturation §7effect to all players",
            "§7during the entire game duration.",
            "",
            "§aEffects:",
            "§8• §7No hunger loss"
        );
    }
    
    @Override
    public boolean isCompatibleWith(String modifierName) {
        // Saturation perk is compatible with all modifiers
        return true;
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Store the game duration for proper effect timing
        this.gameDurationSeconds = gameDurationSeconds;
        
        // Apply saturation effect to all players at game start
        for (Player player : players) {
            applySaturationEffect(player);
        }
        
        plugin.getLogger().info(String.format("[Perks] Applied Saturation effect to %d players for %d seconds", 
            players.size(), gameDurationSeconds));
    }
    
    @Override
    public void onGameEnd() {
        // Remove saturation effect from all online players when game ends
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPotionEffect(PotionEffectType.SATURATION)) {
                player.removePotionEffect(PotionEffectType.SATURATION);
            }
        }
        
        plugin.getLogger().info("[Perks] Removed Saturation effect from all players");
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Apply saturation effect to players who join mid-game
        applySaturationEffect(player);
    }
    
    @Override
    public void onPeriodicUpdate(List<Player> players) {
        // Refresh saturation effect every periodic update (every second)
        // This ensures the effect doesn't wear off
        for (Player player : players) {
            if (!player.hasPotionEffect(PotionEffectType.SATURATION)) {
                // Reapply if effect was somehow removed
                applySaturationEffect(player);
            } else {
                // Check if effect duration is getting low and refresh if needed
                PotionEffect currentEffect = player.getPotionEffect(PotionEffectType.SATURATION);
                if (currentEffect != null && currentEffect.getDuration() < 100) { // Less than 5 seconds left
                    applySaturationEffect(player);
                }
            }
        }
    }
    
    /**
     * Apply the saturation potion effect to a player with infinite duration
     * 
     * @param player The player to apply the effect to
     */
    private void applySaturationEffect(Player player) {
        // Use maximum possible duration for effectively infinite saturation
        int infiniteDuration = Integer.MAX_VALUE; // Effectively infinite duration
        
        PotionEffect saturationEffect = new PotionEffect(
            PotionEffectType.SATURATION,
            infiniteDuration, // Infinite duration
            SATURATION_AMPLIFIER,
            AMBIENT,
            PARTICLES,
            ICON
        );
        
        player.addPotionEffect(saturationEffect, true); // Force override existing effect
    }
}
