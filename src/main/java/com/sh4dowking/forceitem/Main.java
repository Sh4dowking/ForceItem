package com.sh4dowking.forceitem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.sh4dowking.forceitem.modifiers.ModifierManager;
import com.sh4dowking.forceitem.perks.PerkManager;

/**
 * ForceItem - A competitive Minecraft minigame plugin
 * 
 * Players compete to collect randomly assigned target items within a time limit.
 * Features include:
 * - Timed gameplay with customizable duration
 * - Joker system for skipping difficult targets
 * - Real-time leaderboard with GUI interface
 * - Sound effects and visual feedback
 * - Boss bar and action bar displays
 * 
 * Commands:
 * - /startgame <seconds> <jokers> - Start a new game
 * - /stopgame - End the current game
 * - /leaderboard - View game results
 * - /item - View current target item info
 * - /skip <playername> - Skip a player's target item (admin only)
 * 
 * @author Sh4dowking
 * @version 1.0
 */

/**
 * Represents a single collection event with timestamp and method used
 */
/**
 * Main class for the ForceItem plugin
 */
public class Main extends JavaPlugin implements Listener {
    
    // Core game components
    private GameManager gameManager;
    private ModifierManager modifierManager;
    private PerkManager perkManager;
    private LeaderboardGUI leaderboardGUI;
    private ItemInfoGUI itemInfoGUI;
    private StartGameGUI startGameGUI;
    
    // Player data tracking - only backpacks remain here for UI handling
    private final Map<UUID, Inventory> playerBackpacks = new HashMap<>();

    /**
     * Initialize the plugin when server starts
     * Registers event listeners and initializes GUI components
     */
    @Override
    public void onEnable() {
        // Initialize game manager
        gameManager = new GameManager(this);
        
        // Initialize modifier manager
        modifierManager = new ModifierManager(this);
        
        // Initialize perk manager
        perkManager = new PerkManager(this);
        
        // Register main plugin events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Initialize and register GUI components
        itemInfoGUI = new ItemInfoGUI();
        getServer().getPluginManager().registerEvents(itemInfoGUI, this);
        
        startGameGUI = new StartGameGUI(this);
        getServer().getPluginManager().registerEvents(startGameGUI, this);
        
        // Initialize and register command handler
        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("startgame").setExecutor(commandHandler);
        getCommand("stopgame").setExecutor(commandHandler);
        getCommand("leaderboard").setExecutor(commandHandler);
        getCommand("item").setExecutor(commandHandler);
        getCommand("skip").setExecutor(commandHandler);
        getCommand("givejoker").setExecutor(commandHandler);
        
        getLogger().info("ForceItem plugin has been enabled!");
    }

    /**
     * Clean up resources when plugin shuts down
     */
    @Override
    public void onDisable() {
        // Clean up game manager
        if (gameManager != null) {
            gameManager.cleanup();
        }
        
        // Clean up leaderboard GUI
        if (leaderboardGUI != null) {
            try {
                org.bukkit.event.HandlerList.unregisterAll(leaderboardGUI);
            } catch (Exception e) {
                // Ignore any errors during unregistration
            }
            leaderboardGUI = null;
        }
        
        getLogger().info("ForceItem plugin has been disabled!");
    }

    // ===============================
    // PUBLIC ACCESSOR METHODS FOR COMMAND HANDLER
    // ===============================
    
    public boolean isGameRunning() {
        return gameManager.isGameRunning();
    }
    
    public LeaderboardGUI getLeaderboardGUI() {
        return leaderboardGUI;
    }
    
    public StartGameGUI getStartGameGUI() {
        return startGameGUI;
    }
    
    // ===============================
    // PUBLIC METHODS FOR GAMEMANAGER
    // ===============================
    
    public void clearLeaderboard() {
        // Clear any existing leaderboard from previous games
        if (leaderboardGUI != null) {
            // Unregister old leaderboard event handlers to prevent conflicts
            try {
                org.bukkit.event.HandlerList.unregisterAll(leaderboardGUI);
            } catch (Exception e) {
                // Ignore unregister errors
            }
            leaderboardGUI = null;
        }
    }
    
    public void clearPlayerBackpacks() {
        playerBackpacks.clear();
    }
    
    public ItemStack createBackpackItem() {
        ItemStack backpack = new ItemStack(Material.BUNDLE);
        BundleMeta meta = (BundleMeta) backpack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lBackpack");
            List<String> lore = Arrays.asList(
                "§7Right-click to access your",
                "§7personal storage space!"
            );
            meta.setLore(lore);
            backpack.setItemMeta(meta);
        }
        return backpack;
    }
    
    public void createLeaderboard(Map<UUID, List<Material>> playerCollectedItems, 
                                  Map<UUID, Integer> playerPoints, 
                                  Map<UUID, List<CollectionEvent>> playerCollectionHistory, 
                                  long gameStartTime) {
        // Initialize leaderboard GUI for result viewing
        leaderboardGUI = new LeaderboardGUI(this, playerCollectedItems, playerPoints, playerCollectionHistory, gameStartTime);
        Bukkit.getPluginManager().registerEvents(leaderboardGUI, this);
    }
    
    public void createPlayerBackpack(UUID playerId, String playerName) {
        Inventory backpackInv = Bukkit.createInventory(null, 27, ChatColor.WHITE + playerName + "'s Backpack");
        playerBackpacks.put(playerId, backpackInv);
    }
    
    public ItemInfoGUI getItemInfoGUI() {
        return itemInfoGUI;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public ModifierManager getModifierManager() {
        return modifierManager;
    }
    
    public PerkManager getPerkManager() {
        return perkManager;
    }
    
    public String formatMaterialName(Material material) {
        String raw = material.name().toLowerCase().replace("_", " ");
        String[] words = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    public Material getRandomMaterialForPlayer(UUID playerId) {
        return gameManager.getRandomMaterialForPlayer(playerId);
    }
    
    public ItemStack createJokerItem() {
        ItemStack joker = new ItemStack(Material.BARRIER);
        ItemMeta meta = joker.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_RED + "★ " + ChatColor.BOLD + "Joker");
            meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-click to skip your current target!",
                ChatColor.GRAY + "Grants +1 point when used",
                ChatColor.RED + "Cannot be placed."
            ));
            joker.setItemMeta(meta);
        }
        return joker;
    }
    
    public void giveItemOrDrop(Player player, ItemStack item) {
        // Try to add the item to the player's inventory
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        
        // If there are leftover items (inventory was full), drop them near the player
        if (!leftover.isEmpty()) {
            for (ItemStack droppedItem : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), droppedItem);
            }
            player.sendMessage("§e⚠ Your inventory was full! The item was dropped near you.");
        }
    }
    
    public void startGame(int seconds, int jokersPerPlayer) {
        gameManager.startGame(seconds, jokersPerPlayer);
    }
    
    public void startGame(int seconds, int jokersPerPlayer, String modifierName) {
        gameManager.startGame(seconds, jokersPerPlayer, modifierName);
    }
    
    public void stopGame() {
        gameManager.stopGame();
    }

    // ===============================
    // PLAYER TARGET AND JOKER ACCESS METHODS
    // ===============================
    
    public Material getPlayerTarget(UUID playerId) {
        return gameManager.getPlayerTarget(playerId);
    }
    
    public boolean hasPlayerTarget(UUID playerId) {
        return gameManager.hasPlayerTarget(playerId);
    }
    
    public int getPlayerJokers(UUID playerId) {
        return gameManager.getPlayerJokers(playerId);
    }
    
    public int getPlayerPoints(UUID playerId) {
        return gameManager.getPlayerPoints(playerId);
    }
    
    public void setPlayerTarget(UUID playerId, Material target) {
        gameManager.setPlayerTarget(playerId, target);
    }
    
    public void setPlayerJokers(UUID playerId, int jokers) {
        gameManager.setPlayerJokers(playerId, jokers);
    }
    
    public void updatePlayerDisplay(Player player) {
        gameManager.updatePlayerDisplay(player);
    }

    /**
     * Check if an ItemStack is a backpack item
     * 
     * @param item ItemStack to check
     * @return true if item is a valid backpack
     */
    private boolean isBackpackItem(ItemStack item) {
        if (item == null || item.getType() != Material.BUNDLE) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        String expectedName = "" + ChatColor.GOLD + ChatColor.BOLD + "Backpack";
        String actualName = meta.getDisplayName();
        
        return expectedName.equals(actualName);
    }

    /**
     * Get or create a backpack inventory for a player
     * 
     * @param player The player to get the backpack for
     * @return The player's backpack inventory
     */
    private Inventory getPlayerBackpack(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory backpack = playerBackpacks.get(playerId);
        
        if (backpack == null) {
            // Create new backpack inventory (27 slots = 3 rows)
            backpack = Bukkit.createInventory(null, 27, ChatColor.WHITE + player.getName() + "'s Backpack");
            playerBackpacks.put(playerId, backpack);
        }
        
        return backpack;
    }

    /**
     * Check if an ItemStack is a joker item
     * 
     * @param item ItemStack to check
     * @return true if item is a valid joker
     */
    private boolean isJokerItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        String expectedName = ChatColor.DARK_RED + "★ " + ChatColor.DARK_RED + ChatColor.BOLD + "Joker";
        String actualName = meta.getDisplayName();
        
        return expectedName.equals(actualName);
    }

    /**
     * Convert Material enum names to readable format
     * Example: GOLDEN_APPLE -> Golden Apple
     * 
     * @param material Material to format
     * @return Formatted display name
     */

    /**
     * Handle player interactions with joker items and backpacks
     * Processes right-clicks to use jokers or open backpack
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
    
        // Handle joker item usage - delegate to GameManager
        if (isJokerItem(item)) {
            event.setCancelled(true);
            if (gameManager.isGameRunning()) {
                gameManager.useJoker(player, item);
            }
        }
        
        // Handle backpack item usage
        if (isBackpackItem(item)) {
            event.setCancelled(true);
            // Open player's personal backpack
            Inventory backpack = getPlayerBackpack(player);
            player.openInventory(backpack);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        }
    }    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isJokerItem(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot place Jokers!");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Prevent players from dropping backpack items
        if (isBackpackItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage("§cYou cannot drop your backpack!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        String inventoryTitle = event.getView().getTitle();
        
        // Handle Double Trouble targets GUI
        if (inventoryTitle.contains("Double Trouble Targets")) {
            event.setCancelled(true); // Prevent all interactions
            return;
        }
        
        // Handle standard target GUI (check for color-formatted title)
        if (inventoryTitle.contains("Current Target")) {
            event.setCancelled(true); // Prevent all interactions
            return;
        }
        
        // Handle backpack inventory interactions
        if (inventoryTitle.contains("Backpack")) {
            // Prevent placing backpack items inside backpack (infinite recursion)
            if (event.getCursor() != null && isBackpackItem(event.getCursor())) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot put a backpack inside itself!");
            }
            // Allow all other interactions in backpack GUI
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Check if closing a backpack inventory
        if (event.getPlayer() instanceof Player player) {
            String inventoryTitle = event.getView().getTitle();
            if (inventoryTitle.contains("Backpack")) {
                // Play chest close sound
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String inventoryTitle = event.getView().getTitle();
        
        // Handle Double Trouble targets GUI
        if (inventoryTitle.contains("Double Trouble Targets")) {
            event.setCancelled(true); // Prevent all dragging
        }
        
        // Handle standard target GUI
        if (inventoryTitle.contains("Current Target")) {
            event.setCancelled(true); // Prevent all dragging
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerAttemptPickupItemEvent event) {
        // Only handle if game is running and it's a target item
        if (!gameManager.isGameRunning()) return;
        
        Player player = event.getPlayer();
        Material target = gameManager.getPlayerTarget(player.getUniqueId());
        
        // Immediate processing for better user experience
        if (target != null && event.getItem().getItemStack().getType() == target) {
            gameManager.handleTargetItemPickup(player, target);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // If a game is running, set up this player to participate
        if (gameManager.isGameRunning()) {
            // GameManager handles all initialization including jokers, backpack items, etc.
            gameManager.initializeNewPlayer(player);
            
            // Notify perk system of new player
            perkManager.onPlayerJoin(player);
            
            // Create their personal backpack inventory for UI handling
            Inventory backpackInv = Bukkit.createInventory(null, 27, ChatColor.WHITE + player.getName() + "'s Backpack");
            playerBackpacks.put(playerId, backpackInv);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up player's boss bar through GameManager
        if (gameManager.isGameRunning()) {
            gameManager.handlePlayerLeave(player);
            
            // Notify perk system of player leaving
            perkManager.onPlayerLeave(player.getUniqueId());
        }
    }
}
