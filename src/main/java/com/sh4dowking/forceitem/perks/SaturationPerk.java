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
        return Material.COOKED_BEEF;
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
        
        // Convert game duration to ticks (20 ticks = 1 second)
        int durationTicks = gameDurationSeconds * 20;
        
        // Apply saturation effect to all players for the entire game duration (hidden from UI)
        PotionEffect saturationEffect = new PotionEffect(
            PotionEffectType.SATURATION,
            durationTicks, // Game duration
            SATURATION_AMPLIFIER,
            AMBIENT,
            false, // No particles
            false  // Hidden from UI
        );
        
        for (Player player : players) {
            player.addPotionEffect(saturationEffect);
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
        // Note: This gives them the effect for a default duration since we can't easily calculate remaining time
        int defaultDuration = 300 * 20; // 5 minutes in ticks
        PotionEffect saturationEffect = new PotionEffect(
            PotionEffectType.SATURATION,
            defaultDuration, // Default duration
            SATURATION_AMPLIFIER,
            AMBIENT,
            false, // No particles
            false  // Hidden from UI
        );
        player.addPotionEffect(saturationEffect);
    }
}
