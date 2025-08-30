package com.sh4dowking.forceitem.perks;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.sh4dowking.forceitem.Main;

/**
 * VeinMinerPerk - Automatically mines connected blocks of the same type
 * 
 * This perk enables vein mining for ores and logs. When a player breaks 
 * an ore or log block, all connected blocks of the same type will be 
 * automatically broken and their drops collected.
 * 
 * Features:
 * - Supports all ore types and wood types
 * - Uses flood-fill algorithm for efficient connected block detection
 * - Respects tool durability and enchantments
 * - Limited to prevent server lag (max 64 blocks per vein)
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public class VeinMinerPerk extends GamePerk implements Listener {
    
    private boolean isActive = false;
    private static final int MAX_VEIN_SIZE = 64; // Maximum blocks that can be broken in one vein
    private static final int MAX_SEARCH_DISTANCE = 32; // Maximum distance to search for connected blocks
    
    // Define materials that can be vein mined
    private static final Set<Material> VEIN_MINABLE_ORES = EnumSet.of(
        Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
        Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
        Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
        Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
        Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
        Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE
    );
    
    private static final Set<Material> VEIN_MINABLE_LOGS = EnumSet.of(
        Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
        Material.CHERRY_LOG, Material.MANGROVE_LOG, Material.BAMBOO_BLOCK,
        Material.CRIMSON_STEM, Material.WARPED_STEM
    );
    
    public VeinMinerPerk(Main plugin) {
        super(plugin);
    }
    
    @Override
    public String getPerkName() {
        return "Vein Miner";
    }
    
    @Override
    public String getDescription() {
        return "Automatically mines connected ores and logs of the same type";
    }
    
    @Override
    public Material getDisplayMaterial() {
        return Material.IRON_PICKAXE;
    }
    
    @Override
    public List<String> getLore() {
        return Arrays.asList(
            "§7Automatically breaks connected blocks",
            "§7of the same type when mining.",
            "",
            "§aFeatures:",
            "§8• §7Works on all ore types",
            "§8• §7Works on all log types", 
            "§8• §7Limited to " + MAX_VEIN_SIZE + " blocks per vein"
        );
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer, int gameDurationSeconds) {
        // Enable the vein mining effect
        isActive = true;
        
        // Register event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        plugin.getLogger().info("[VeinMinerPerk] Enabled vein mining for all players");
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        // Vein mining is a global effect, no per-player setup needed
        plugin.getLogger().info(String.format("[VeinMinerPerk] Player %s joined - vein mining already active", 
                               player.getName()));
    }
    
    @Override
    public void onPlayerLeave(UUID playerId) {
        // Vein mining is a global effect, no per-player cleanup needed
    }
    
    @Override
    public void onGameEnd() {
        // Disable the vein mining effect
        isActive = false;
        
        // Unregister event listener
        BlockBreakEvent.getHandlerList().unregister(this);
        
        plugin.getLogger().info("[VeinMinerPerk] Disabled vein mining effect");
    }
    
    /**
     * Handle block break events to trigger vein mining
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isActive || event.isCancelled()) return;
        
        Block brokenBlock = event.getBlock();
        Material blockType = brokenBlock.getType();
        Player player = event.getPlayer();
        
        // Check if this block type can be vein mined
        if (!isVeinMinable(blockType)) return;
        
        // Find all connected blocks of the same type
        Set<Block> connectedBlocks = findConnectedBlocks(brokenBlock, blockType);
        
        if (connectedBlocks.size() <= 1) return; // No connected blocks found
        
        // Remove the original block since it's already being broken
        connectedBlocks.remove(brokenBlock);
        
        // Break all connected blocks
        ItemStack tool = player.getInventory().getItemInMainHand();
        for (Block block : connectedBlocks) {
            breakBlockWithTool(player, block, tool);
        }
        
        plugin.getLogger().info(String.format("[VeinMinerPerk] Player %s vein mined %d %s blocks", 
                               player.getName(), connectedBlocks.size() + 1, blockType.name()));
    }
    
    /**
     * Check if a material can be vein mined
     */
    private boolean isVeinMinable(Material material) {
        return VEIN_MINABLE_ORES.contains(material) || VEIN_MINABLE_LOGS.contains(material);
    }
    
    /**
     * Find all connected blocks of the same type using flood-fill algorithm
     */
    private Set<Block> findConnectedBlocks(Block startBlock, Material targetType) {
        Set<Block> visited = new HashSet<>();
        Set<Block> connected = new HashSet<>();
        Queue<Block> toCheck = new LinkedList<>();
        
        toCheck.offer(startBlock);
        visited.add(startBlock);
        
        while (!toCheck.isEmpty() && connected.size() < MAX_VEIN_SIZE) {
            Block current = toCheck.poll();
            connected.add(current);
            
            // Check all 26 adjacent blocks (including diagonals)
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Skip the center block (current block itself)
                        if (x == 0 && y == 0 && z == 0) continue;
                        
                        Block adjacent = current.getRelative(x, y, z);
                
                        // Skip if already visited or too far from origin
                        if (visited.contains(adjacent) || 
                            adjacent.getLocation().distance(startBlock.getLocation()) > MAX_SEARCH_DISTANCE) {
                            continue;
                        }
                        
                        visited.add(adjacent);
                        
                        // Add to queue if it's the same material
                        if (adjacent.getType() == targetType) {
                            toCheck.offer(adjacent);
                        }
                    }
                }
            }
        }
        
        return connected;
    }
    
    /**
     * Break a block with the player's tool, respecting tool durability and enchantments
     */
    private void breakBlockWithTool(Player player, Block block, ItemStack tool) {
        // Simulate the block break with proper drops and tool damage
        block.breakNaturally(tool);
        
        // Note: Tool durability is handled automatically by breakNaturally()
        // Enchantments like Fortune are also applied automatically
    }
}
