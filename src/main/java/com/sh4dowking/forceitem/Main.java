package com.sh4dowking.forceitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.scheduler.BukkitRunnable;

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
class CollectionEvent {
    private final Material item;
    private final long timestamp;
    private final boolean usedJoker;
    
    public CollectionEvent(Material item, long timestamp, boolean usedJoker) {
        this.item = item;
        this.timestamp = timestamp;
        this.usedJoker = usedJoker;
    }
    
    public Material getItem() { return item; }
    public long getTimestamp() { return timestamp; }
    public boolean wasJokered() { return usedJoker; }
    
    public String getFormattedTime(long gameStartTime) {
        long elapsedMs = timestamp - gameStartTime;
        long minutes = elapsedMs / 60000;
        long seconds = (elapsedMs % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

public class Main extends JavaPlugin implements Listener {
    
    // Core game components
    private CountdownTimer timer;
    private boolean gameRunning = false;
    private int currentJokersPerPlayer = 0;
    private long gameStartTime = 0;
    private LeaderboardGUI leaderboardGUI;
    private ItemInfoGUI itemInfoGUI;
    
    // Player data tracking
    private final Map<UUID, Material> playerTargets = new HashMap<>();
    private final Map<UUID, Integer> playerPoints = new HashMap<>();
    private final Map<UUID, Integer> playerJokers = new HashMap<>();
    private final Map<UUID, List<Material>> playerCollectedItems = new HashMap<>();
    private final Map<UUID, List<CollectionEvent>> playerCollectionHistory = new HashMap<>();
    private final Map<UUID, Inventory> playerBackpacks = new HashMap<>();
    private final Map<UUID, Set<Material>> playerAssignedTargets = new HashMap<>();
    
    // Display systems
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private BukkitRunnable displayTask;

    /**
     * Initialize the plugin when server starts
     * Registers event listeners and initializes GUI components
     */
    @Override
    public void onEnable() {
        // Register main plugin events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Initialize and register GUI components
        itemInfoGUI = new ItemInfoGUI();
        getServer().getPluginManager().registerEvents(itemInfoGUI, this);
        
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
        // Stop any running game
        if (gameRunning) {
            stopGame();
        }
        
        // Clean up display systems
        for (BossBar bossBar : playerBossBars.values()) {
            if (bossBar != null) {
                bossBar.removeAll();
            }
        }
        playerBossBars.clear();
        
        if (displayTask != null && !displayTask.isCancelled()) {
            displayTask.cancel();
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
        return gameRunning;
    }
    
    public LeaderboardGUI getLeaderboardGUI() {
        return leaderboardGUI;
    }
    
    public ItemInfoGUI getItemInfoGUI() {
        return itemInfoGUI;
    }
    
    public Material getPlayerTarget(UUID playerId) {
        return playerTargets.get(playerId);
    }
    
    public boolean hasPlayerTarget(UUID playerId) {
        return playerTargets.containsKey(playerId);
    }
    
    public void setPlayerTarget(UUID playerId, Material target) {
        playerTargets.put(playerId, target);
    }
    
    public int getPlayerJokers(UUID playerId) {
        return playerJokers.getOrDefault(playerId, 0);
    }
    
    public void setPlayerJokers(UUID playerId, int jokers) {
        playerJokers.put(playerId, jokers);
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
        Set<Material> assignedTargets = playerAssignedTargets.getOrDefault(playerId, new HashSet<>());
        
        // If player has been assigned all possible materials (very unlikely), reset their assigned targets
        if (assignedTargets.size() >= SurvivalItems.getAllSurvivalItems().size()) {
            assignedTargets.clear();
            playerAssignedTargets.put(playerId, assignedTargets);
        }
        
        Material newTarget;
        int attempts = 0;
        int maxAttempts = 50; // Prevent infinite loops
        
        do {
            newTarget = SurvivalItems.getRandomMaterial();
            attempts++;
        } while (assignedTargets.contains(newTarget) && attempts < maxAttempts);
        
        // Add the new target to the player's assigned targets set
        assignedTargets.add(newTarget);
        playerAssignedTargets.put(playerId, assignedTargets);
        
        return newTarget;
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
    
    public void updatePlayerDisplay(Player player) {
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null) {
            int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
            
            // Update boss bar title (text will show at top, but bar itself is invisible)
            String targetName = formatMaterialName(target);
            String displayText = "§f§lTarget: §b" + targetName + " §7| §f§lPoints: §a" + points;
            
            BossBar playerBossBar = playerBossBars.get(player.getUniqueId());
            if (playerBossBar != null) {
                playerBossBar.setTitle(displayText);
                // Ensure the bar remains invisible
                playerBossBar.setProgress(0.0);
            }
        }
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
        gameRunning = true;
        currentJokersPerPlayer = jokersPerPlayer;
        
        // Clear any existing leaderboard from previous games
        if (leaderboardGUI != null) {
            // Unregister old leaderboard event handlers to prevent conflicts
            try {
                org.bukkit.event.HandlerList.unregisterAll(leaderboardGUI);
            } catch (Exception e) {
                // Ignore any errors during unregistration
            }
            leaderboardGUI = null;
        }
        
        // Reset all player data for new game
        playerTargets.clear();
        playerPoints.clear();
        playerJokers.clear();
        playerCollectedItems.clear();
        playerCollectionHistory.clear();
        playerBackpacks.clear();
        playerAssignedTargets.clear();
        
        // Set game start time
        gameStartTime = System.currentTimeMillis();
        
        // Prepare all players and world
        preparePlayersAndWorld();
        
        // Announce game preparation to all players
        Bukkit.broadcastMessage("§f§lForceItem §7game starting!");
        Bukkit.broadcastMessage("§f§lTime: §b" + seconds + "s §7| §f§lJokers: §b" + jokersPerPlayer);
        // Start countdown with title display
        startCountdownAndGame(seconds, jokersPerPlayer);
    }
    
    public void stopGame() {
        gameRunning = false;
        currentJokersPerPlayer = 0;
        
        // Cancel the game timer
        if (timer != null) timer.cancel();
        
        // Stop display systems (boss bar, action bar updates)
        stopDisplaySystem();
        
        // Initialize leaderboard GUI for result viewing
        leaderboardGUI = new LeaderboardGUI(this, playerCollectedItems, playerPoints, playerCollectionHistory, gameStartTime);
        Bukkit.getPluginManager().registerEvents(leaderboardGUI, this);
        
        // Announce game end
        Bukkit.broadcastMessage("§f§lForceItem §7game ended!");
        
        // Schedule leaderboard display after brief delay
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                leaderboardGUI.showLeaderboard(player);
                // Play game end and leaderboard reveal sound
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.0f);
            }
        }, 20L); // Delay 1 second to let end message show
        
        // Also show text summary
        for (Player player : Bukkit.getOnlinePlayers()) {
            int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
            player.sendMessage("§f§lYour final score: §a" + points + " points");
            player.sendMessage("§7Leaderboard GUI will open shortly! Use §f/leaderboard §7to view it again.");
        }
    }

    // ===============================
    // END OF PUBLIC ACCESSOR METHODS
    // ===============================

    /**
     * Initialize and start a new ForceItem game
     * 
     * @param seconds Duration of the game in seconds
     * @param jokersPerPlayer Number of jokers each player receives
     */
    private void startGame(int seconds, int jokersPerPlayer) {
        // This is now handled by the public method above
    }
    
    /**
     * Prepare all players and world for the game
     * Clears inventories, sets health/hunger, and resets world time
     */
    private void preparePlayersAndWorld() {
        // Set world time to day (0 ticks) and configure gamerules
        for (World world : Bukkit.getWorlds()) {
            world.setTime(0);
            // Enable keep inventory so players don't lose items on death
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            // Enable immediate respawn so players respawn instantly
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }
        
        // Prepare all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Clear inventory completely
            player.getInventory().clear();
            
            // Set health to maximum
            player.setHealth(player.getMaxHealth());
            
            // Set hunger and saturation to maximum
            player.setFoodLevel(20);
            player.setSaturation(20.0f);
            
            // Clear any active effects (optional enhancement)
            player.getActivePotionEffects().forEach(effect -> 
                player.removePotionEffect(effect.getType())
            );
            
            // Reset experience
            player.setExp(0);
            player.setLevel(0);
        }
    }
    
    /**
     * Start the countdown sequence and then begin the actual game
     * Shows countdown titles from 3 to 1, then "GO!"
     */
    private void startCountdownAndGame(int gameDuration, int jokersPerPlayer) {
        // Schedule countdown tasks
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // Countdown: 3
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§e§l3", "§7Get ready...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.0f);
            }
        }, 0L);
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // Countdown: 2
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§6§l2", "§7Prepare yourself...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.2f);
            }
        }, 20L);
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // Countdown: 1
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§c§l1", "§7Almost there...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.4f);
            }
        }, 40L);
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            // Game start: GO!
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§a§lGO!", "§7Find your items!", 0, 30, 20);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.5f);
            }
            
            // Actually start the game now
            actuallyStartGame(gameDuration, jokersPerPlayer);
        }, 60L);
    }
    
    /**
     * Actually start the game mechanics after countdown
     * This method contains the original game start logic
     */
    private void actuallyStartGame(int seconds, int jokersPerPlayer) {
        // Assign targets and items to all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material randomItem = getRandomMaterialForPlayer(player.getUniqueId());
            playerTargets.put(player.getUniqueId(), randomItem);
            playerPoints.put(player.getUniqueId(), 0);
            playerJokers.put(player.getUniqueId(), jokersPerPlayer);
            playerCollectedItems.put(player.getUniqueId(), new ArrayList<>());
            playerCollectionHistory.put(player.getUniqueId(), new ArrayList<>());
            
            // Send target message first
            player.sendMessage("§f§lYour target item: §b" + formatMaterialName(randomItem));
            
            // Give player joker items and send joker message after
            if (jokersPerPlayer > 0) {
                ItemStack joker = createJokerItem();
                joker.setAmount(jokersPerPlayer);
                giveItemOrDrop(player, joker);
                player.sendMessage("§7You have been given §4§l" + jokersPerPlayer + " §4§lJokers§7! Right-click to skip your current target.");
            }
            
            // Give player backpack item
            ItemStack backpack = createBackpackItem();
            giveItemOrDrop(player, backpack);
            player.sendMessage("§7You have been given a §6§lBackpack§7! Right-click to access your personal storage.");
        }
        
        // Start the game timer and systems
        if (timer != null) timer.cancel();
        timer = new CountdownTimer(this, seconds, this::checkInventories, this::stopGame);
        timer.start();
        
        // Start the display system
        startDisplaySystem();
    }

    private void checkInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material target = playerTargets.get(player.getUniqueId());
            if (target != null && player.getInventory().contains(target)) {
                int newScore = playerPoints.get(player.getUniqueId()) + 1;
                playerPoints.put(player.getUniqueId(), newScore);
                
                // Add to collected items
                playerCollectedItems.get(player.getUniqueId()).add(target);
                
                // Record collection event
                playerCollectionHistory.get(player.getUniqueId()).add(new CollectionEvent(target, System.currentTimeMillis(), false));
                
                // Play success sound for the player
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
                
                player.sendMessage("§a✓ You collected your item! +1 point!");
                // Assign new target
                Material newTarget = getRandomMaterialForPlayer(player.getUniqueId());
                playerTargets.put(player.getUniqueId(), newTarget);
                player.sendMessage("§f§lNew target: §b" + formatMaterialName(newTarget));
                
                // Update display immediately
                updatePlayerDisplay(player);
            }
        }
    }

    /**
     * End the current game and display results
     * Creates leaderboard GUI and shows final scores to all players
     */
    private void stopGame() {
        gameRunning = false;
        currentJokersPerPlayer = 0;
        
        // Cancel the game timer
        if (timer != null) timer.cancel();
        
        // Stop display systems (boss bar, action bar updates)
        stopDisplaySystem();
        
        // Initialize leaderboard GUI for result viewing
        leaderboardGUI = new LeaderboardGUI(this, playerCollectedItems, playerPoints, playerCollectionHistory, gameStartTime);
        Bukkit.getPluginManager().registerEvents(leaderboardGUI, this);
        
        // Announce game end
        Bukkit.broadcastMessage("§f§lForceItem §7game ended!");
        
        // Schedule leaderboard display after brief delay
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                leaderboardGUI.showLeaderboard(player);
                // Play game end and leaderboard reveal sound
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.0f);
            }
        }, 20L); // Delay 1 second to let end message show
        
        // Also show text summary
        for (Player player : Bukkit.getOnlinePlayers()) {
            int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
            player.sendMessage("§f§lYour final score: §a" + points + " points");
            player.sendMessage("§7Leaderboard GUI will open shortly! Use §f/leaderboard §7to view it again.");
        }
    }

    private Material getRandomMaterial() {
        return SurvivalItems.getRandomMaterial();
    }

    /**
     * Get a random material for a specific player, avoiding duplicates they've already been assigned this game
     * 
     * @param playerId The UUID of the player
     * @return A Material that the player hasn't been assigned as a target this game
     */
    private Material getRandomMaterialForPlayer(UUID playerId) {
        Set<Material> assignedTargets = playerAssignedTargets.getOrDefault(playerId, new HashSet<>());
        
        // If player has been assigned all possible materials (very unlikely), reset their assigned targets
        if (assignedTargets.size() >= SurvivalItems.getAllSurvivalItems().size()) {
            assignedTargets.clear();
            playerAssignedTargets.put(playerId, assignedTargets);
        }
        
        Material newTarget;
        int attempts = 0;
        int maxAttempts = 50; // Prevent infinite loops
        
        do {
            newTarget = SurvivalItems.getRandomMaterial();
            attempts++;
        } while (assignedTargets.contains(newTarget) && attempts < maxAttempts);
        
        // Add the new target to the player's assigned targets set
        assignedTargets.add(newTarget);
        playerAssignedTargets.put(playerId, assignedTargets);
        
        return newTarget;
    }

    /**
     * Create a joker item that players can use to skip targets
     * 
     * @return ItemStack configured as a joker with proper display name and lore
     */
    private ItemStack createJokerItem() {
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

    /**
     * Create a backpack item that players can use to access extra storage
     * The bundle is prefilled with dummy items to prevent vanilla bundle mechanics
     * 
     * @return ItemStack configured as a backpack with proper display name and lore
     */
    private ItemStack createBackpackItem() {
        ItemStack backpack = new ItemStack(Material.BUNDLE);
        BundleMeta meta = (BundleMeta) backpack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + "Backpack");
            meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-click to open your personal storage!",
                ChatColor.GRAY + "Stores items safely during the game",
                ChatColor.RED + "Cannot be dropped."
            ));
            
            backpack.setItemMeta(meta);
        }
        return backpack;
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
            backpack = Bukkit.createInventory(null, 27, ChatColor.AQUA + player.getName() + "'s Backpack");
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
    private String formatMaterialName(Material material) {
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

    /**
     * Handle player interactions with joker items
     * Processes right-clicks to use jokers and skip targets
     * 
     * @param event PlayerInteractEvent containing interaction details
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only process during active games
        if (!gameRunning) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
    
        // Handle joker item usage
        if (isJokerItem(item)) {
            event.setCancelled(true); // Prevent placement or other interactions
            
            // Process right-click actions (joker activation)
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                int jokersLeft = playerJokers.getOrDefault(player.getUniqueId(), 0);
                
                if (jokersLeft > 0) {
                    // Process joker usage
                    playerJokers.put(player.getUniqueId(), jokersLeft - 1);
                    
                    // Grant a point for using the joker
                    int currentScore = playerPoints.getOrDefault(player.getUniqueId(), 0);
                    playerPoints.put(player.getUniqueId(), currentScore + 1);
                    
                    // Track the skipped target as a "joker use" in collected items
                    Material currentTarget = playerTargets.get(player.getUniqueId());
                    if (currentTarget != null) {
                        playerCollectedItems.get(player.getUniqueId()).add(currentTarget);
                        playerCollectionHistory.get(player.getUniqueId()).add(new CollectionEvent(currentTarget, System.currentTimeMillis(), true));
                    }
                    
                    // Remove one joker from inventory
                    if (item != null && item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else if (item != null) {
                        player.getInventory().remove(item);
                    }
                    
                    // Assign new target
                    Material newTarget = getRandomMaterialForPlayer(player.getUniqueId());
                    playerTargets.put(player.getUniqueId(), newTarget);
                    
                    // Give player the item they jokered
                    ItemStack jokerReward = new ItemStack(currentTarget);
                    giveItemOrDrop(player, jokerReward);
                    
                    // Play pleasant joker use sound
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
                    
                    player.sendMessage("§4★ Joker used! §a+1 point!");
                    player.sendMessage("§f§lYou received: §b" + formatMaterialName(currentTarget)); 
                    player.sendMessage("§f§lNew target: §b" + formatMaterialName(newTarget));
                    
                    // Update action bar immediately
                    updatePlayerDisplay(player);
                } else {
                    // Play error sound for no jokers left
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage("§cYou have no jokers left!");
                }
            }
        }
        
        // Handle backpack item usage
        if (isBackpackItem(item)) {
            // Only cancel right-click actions that we want to handle specially
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                event.setCancelled(true); // Prevent placement and handle as backpack opening
                
                // Open the player's backpack inventory
                Inventory backpack = getPlayerBackpack(player);
                player.openInventory(backpack);
                
                // Play chest open sound
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
            }
            // Let other interactions (like left-click, pickup, etc.) proceed normally
            // This allows normal inventory management while still preventing right-click placement
        }
    }

    @EventHandler
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
    public void onInventoryDrag(InventoryDragEvent event) {
        // Simple drag handler - no special bundle logic needed
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
    public void onPlayerPickup(PlayerAttemptPickupItemEvent event) {
        if (!gameRunning) return;
        Player player = event.getPlayer();
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null && event.getItem().getItemStack().getType() == target) {
            int newScore = playerPoints.get(player.getUniqueId()) + 1;
            playerPoints.put(player.getUniqueId(), newScore);
            
            // Add to collected items
            playerCollectedItems.get(player.getUniqueId()).add(target);
            
            // Record collection event
            playerCollectionHistory.get(player.getUniqueId()).add(new CollectionEvent(target, System.currentTimeMillis(), false));
            
            player.sendMessage("§a✓ You collected your item! +1 point");
            // Assign new target
            Material newTarget = getRandomMaterialForPlayer(player.getUniqueId());
            playerTargets.put(player.getUniqueId(), newTarget);
            player.sendMessage("§f§lNew target: §b" + formatMaterialName(newTarget));
            
            // Update display immediately
            updatePlayerDisplay(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // If a game is running, set up this player to participate
        if (gameRunning) {
            // Give the player a boss bar if they don't have one
            if (!playerBossBars.containsKey(playerId)) {
                BossBar bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
                bossBar.setProgress(0.0);
                bossBar.addPlayer(player);
                playerBossBars.put(playerId, bossBar);
            }
            
            // Initialize player data if they don't have any (new player joining mid-game)
            if (!playerTargets.containsKey(playerId)) {
                // Initialize player data
                playerTargets.put(playerId, getRandomMaterialForPlayer(playerId));
                playerPoints.put(playerId, 0);
                playerJokers.put(playerId, currentJokersPerPlayer);
                playerCollectedItems.put(playerId, new ArrayList<>());
                playerCollectionHistory.put(playerId, new ArrayList<>());
                
                // Give them a backpack
                ItemStack backpack = createBackpackItem();
                giveItemOrDrop(player, backpack);

                // Give them a backpack
                ItemStack joker = createJokerItem();
                giveItemOrDrop(player, joker);

                // Create their personal backpack inventory
                Inventory backpackInv = Bukkit.createInventory(null, 27, player.getName() + "'s Backpack");
                playerBackpacks.put(playerId, backpackInv);
                
                // Welcome them to the ongoing game
                player.sendMessage("§6§lWelcome to the ongoing Force Item game!");
                player.sendMessage("§f§lTarget: §b" + formatMaterialName(playerTargets.get(playerId)));
                player.sendMessage("§f§lJokers remaining: §e" + playerJokers.get(playerId));
            }
            
            // Update their display immediately
            updatePlayerDisplay(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Clean up player's boss bar if they have one
        BossBar playerBossBar = playerBossBars.remove(playerId);
        if (playerBossBar != null) {
            playerBossBar.removeAll();
        }
        
        // Note: We don't remove other player data (targets, points, etc.) because:
        // 1. The player might rejoin during the same game
        // 2. Their data is needed for final leaderboard/results
        // 3. All data gets cleared when a new game starts anyway
    }

    private void startDisplaySystem() {
        // Create individual invisible boss bars for each player
        playerBossBars.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
            bossBar.setProgress(0.0); // This makes the actual bar invisible
            bossBar.addPlayer(player);
            playerBossBars.put(player.getUniqueId(), bossBar);
        }
        
        // Start display task that only updates boss bar (timer is handled by CountdownTimer)
        displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameRunning) {
                    this.cancel();
                    return;
                }
                
                // Update boss bar for each player (no timer or game ending logic here)
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (gameRunning) {
                        // Update boss bar with player-specific info only
                        updatePlayerDisplay(player);
                    }
                }
            }
        };
        displayTask.runTaskTimer(this, 0L, 20L); // Run every second for boss bar updates
    }
    
    private void updatePlayerDisplay(Player player) {
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null) {
            int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
            
            // Update boss bar title (text will show at top, but bar itself is invisible)
            String targetName = formatMaterialName(target);
            String displayText = "§f§lTarget: §b" + targetName + " §7| §f§lPoints: §a" + points;
            
            BossBar playerBossBar = playerBossBars.get(player.getUniqueId());
            if (playerBossBar != null) {
                playerBossBar.setTitle(displayText);
                // Ensure the bar remains invisible
                playerBossBar.setProgress(0.0);
            }
        }
    }
    
    private void stopDisplaySystem() {
        if (displayTask != null && !displayTask.isCancelled()) {
            displayTask.cancel();
        }
        for (BossBar bossBar : playerBossBars.values()) {
            if (bossBar != null) {
                bossBar.removeAll();
            }
        }
        playerBossBars.clear();
    }
    
    private void giveItemOrDrop(Player player, ItemStack item) {
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
}
