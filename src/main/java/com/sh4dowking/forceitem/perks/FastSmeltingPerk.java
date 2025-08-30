package com.sh4dowking.forceitem.perks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import com.sh4dowking.forceitem.Main;

/**
 * FastSmeltingPerk - Increases furnace smelting speed for all players
 * 
 * Provides significantly faster smelting times for all furnaces during the game.
 * The speed boost is applied globally and works for all types of furnaces
 * (furnace, blast furnace, smoker).
 * 
 * Features:
 * - 20x faster smelting speed for all furnaces
 * - Works with furnaces, blast furnaces, and smokers
 * - Global effect that benefits all players
 * - Automatically applied when furnaces are placed during the game
 * 
 * @author Sh4dowking
 * @version 2.0.0
 */
public class FastSmeltingPerk extends GamePerk implements Listener {
    
    private boolean isActive = false;
    private BukkitTask speedTask = null;
    private final Set<Location> trackedFurnaces = new HashSet<>();
    private static final int SPEED_MULTIPLIER = 20; // 20x faster smelting

    public FastSmeltingPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Fast Smelting";
    }
    
    @Override
    public String getDescription() {
        return "Increases furnace smelting speed by 20x for all players";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.FURNACE;
    }
    
    @Override
    public List<String> getLore() {
        return Arrays.asList(
            "§7Significantly speeds up all furnace types",
            "§7when they are placed during the game.",
            "",
            "§aEffects:",
            "§8• §7" + SPEED_MULTIPLIER + "x faster smelting speed",
            "§8• §7Works with all furnace types",
            "§8• §7Applied automatically on placement"
        );
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Enable the fast smelting effect
        isActive = true;
        trackedFurnaces.clear();
        
        // Register event listener to catch furnace placements
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // Start task to speed up tracked furnaces
        speedTask = Bukkit.getScheduler().runTaskTimer(plugin, this::speedUpTrackedFurnaces, 1L, 1L);
        
        plugin.getLogger().info(String.format("[FastSmeltingPerk] Enabled fast smelting (%dx speed) for all players", 
                               SPEED_MULTIPLIER));
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Fast smelting is a global effect, no per-player setup needed
        plugin.getLogger().info(String.format("[FastSmeltingPerk] Player %s joined - fast smelting already active", 
                               player.getName()));
    }
    
    @Override
    public void onPlayerLeave(UUID playerId) {
        // Fast smelting is a global effect, no per-player cleanup needed
    }
    
    @Override
    public void onGameEnd() {
        // Disable the fast smelting effect
        isActive = false;
        
        // Cancel the speed task
        if (speedTask != null) {
            speedTask.cancel();
            speedTask = null;
        }
        
        // Clear tracked furnaces
        trackedFurnaces.clear();
        
        // Unregister event listener
        BlockPlaceEvent.getHandlerList().unregister(this);
        
        plugin.getLogger().info("[FastSmeltingPerk] Disabled fast smelting effect");
    }
    
    /**
     * Handle block placement events to detect furnace placement
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isActive) return;
        
        Block block = event.getBlock();
        if (isFurnaceType(block.getType())) {
            // Track this furnace for speed enhancement
            trackedFurnaces.add(block.getLocation());
            
            plugin.getLogger().info(String.format("[FastSmeltingPerk] Now tracking %s placed by %s", 
                                   block.getType().toString().toLowerCase(), event.getPlayer().getName()));
        }
    }
    
    /**
     * Speed up all tracked furnaces
     */
    private void speedUpTrackedFurnaces() {
        if (!isActive || trackedFurnaces.isEmpty()) return;
        
        // Remove any furnaces that no longer exist
        trackedFurnaces.removeIf(location -> !isFurnaceType(location.getBlock().getType()));
        
        // Speed up remaining furnaces
        for (Location location : trackedFurnaces) {
            Block block = location.getBlock();
            if (block.getState() instanceof Furnace furnace) {
                // Only speed up if the furnace is actively smelting
                if (furnace.getCookTime() > 0 && furnace.getBurnTime() > 0) {
                    // Increase cook time progress by the speed multiplier
                    short currentCookTime = furnace.getCookTime();
                    short newCookTime = (short) Math.min(currentCookTime + (SPEED_MULTIPLIER - 1), 200);
                    
                    furnace.setCookTime(newCookTime);
                    furnace.update();
                }
            }
        }
    }
    
    /**
     * Check if a material is a furnace type
     */
    private boolean isFurnaceType(Material material) {
        return material == Material.FURNACE || 
               material == Material.BLAST_FURNACE || 
               material == Material.SMOKER;
    }
}
