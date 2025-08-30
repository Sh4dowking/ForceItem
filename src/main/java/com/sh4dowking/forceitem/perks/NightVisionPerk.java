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
 * NightVisionPerk - Grants all players Night Vision effect
 * 
 * Provides enhanced visibility in dark environments throughout the entire game duration.
 * The night vision effect is automatically applied when the game starts and
 * lasts for the exact duration of the game.
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public class NightVisionPerk extends GamePerk {
    
    public NightVisionPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Night Vision";
    }
    
    @Override
    public String getDescription() {
        return "Grants all players Night Vision for enhanced visibility";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.SPYGLASS;
    }
    
    @Override
    public List<String> getLore() {
        return Arrays.asList(
            "§7Grants §6Night Vision §7effect to all players",
            "§7during the entire game duration.",
            "",
            "§aEffects:",
            "§8• §7Night Vision"
        );
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Convert game duration to ticks (20 ticks = 1 second)
        int durationTicks = gameDurationSeconds * 20;
        
        // Apply Night Vision effect to all players for the entire game duration (hidden from UI)
        PotionEffect nightVisionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, durationTicks, 0, false, false, false);
        
        for (Player player : players) {
            player.addPotionEffect(nightVisionEffect);
        }
        
        plugin.getLogger().info(String.format("[NightVisionPerk] Applied Night Vision effect to %d players for %d seconds", 
                               players.size(), gameDurationSeconds));
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Apply night vision effect to new players joining mid-game
        // Note: This gives them the effect for a default duration since we can't easily calculate remaining time
        int defaultDuration = 300 * 20; // 5 minutes in ticks
        PotionEffect nightVisionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, defaultDuration, 0, false, false, false);
        player.addPotionEffect(nightVisionEffect);
        
        plugin.getLogger().info(String.format("[NightVisionPerk] Applied Night Vision effect to new player %s", 
                               player.getName()));
    }
    
    @Override
    public void onPlayerLeave(UUID playerId) {
        // Night vision effect will naturally expire, no cleanup needed
        // The effect persists on the player if they rejoin quickly
    }
    
    @Override
    public void onGameEnd() {
        // Remove night vision effects from all online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                // Only remove if it's our night vision effect (level 1, amplifier 0)
                PotionEffect currentEffect = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
                if (currentEffect != null && currentEffect.getAmplifier() == 0) {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                }
            }
        }
        
        plugin.getLogger().info("[NightVisionPerk] Removed Night Vision effects from all players");
    }
}
