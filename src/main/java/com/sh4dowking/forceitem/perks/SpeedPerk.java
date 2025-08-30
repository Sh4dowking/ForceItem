package com.sh4dowking.forceitem.perks;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sh4dowking.forceitem.Main;

/**
 * SpeedPerk - Grants all players Speed II effect
 * 
 * Provides enhanced movement speed throughout the entire game duration.
 * The speed effect is automatically applied when the game starts and
 * lasts for the exact duration of the game.
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public class SpeedPerk extends GamePerk {
    
    public SpeedPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Switftness";
    }
    
    @Override
    public String getDescription() {
        return "Grants all players Speed II for enhanced movement";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.DIAMOND_BOOTS;
    }
    
    @Override
    public List<String> getLore() {
        return Arrays.asList(
            "§7Grants §6Speed §7effect to all players",
            "§7during the entire game duration.",
            "",
            "§aEffects:",
            "§8• §7Speed II"
        );
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Convert game duration to ticks (20 ticks = 1 second)
        int durationTicks = gameDurationSeconds * 20;
        
        // Apply Speed II effect to all players for the entire game duration (hidden from UI)
        PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, durationTicks, 1, false, false, false);
        
        for (Player player : players) {
            player.addPotionEffect(speedEffect);
        }
        
        plugin.getLogger().info(String.format("[SpeedPerk] Applied Speed II effect to %d players for %d seconds", 
                               players.size(), gameDurationSeconds));
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Apply speed effect to new players joining mid-game
        // Note: This gives them the effect for a default duration since we can't easily calculate remaining time
        int defaultDuration = 300 * 20; // 5 minutes in ticks
        PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, defaultDuration, 1, false, false, false);
        player.addPotionEffect(speedEffect);
        
        plugin.getLogger().info(String.format("[SpeedPerk] Applied Speed II effect to new player %s", 
                               player.getName()));
    }
    
    @Override
    public void onPlayerLeave(UUID playerId) {
        // Speed effect will naturally expire, no cleanup needed
        // The effect persists on the player if they rejoin quickly
    }
    
    @Override
    public void onGameEnd() {
        // Remove speed effects from all online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                // Only remove if it's our speed effect (level 2)
                PotionEffect currentEffect = player.getPotionEffect(PotionEffectType.SPEED);
                if (currentEffect != null && currentEffect.getAmplifier() == 1) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                }
            }
        }
        
        plugin.getLogger().info("[SpeedPerk] Removed Speed II effects from all players");
    }
}
