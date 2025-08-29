package com.sh4dowking.forceitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * GameManager - Handles all ForceItem game logic and state
 * 
 * Manages game sessions, player data, timers, and display systems.
 * Separated from main plugin class for better code organization.
 * 
 * @author Sh4dowking
 * @version 1.1.0
 */
public class GameManager {
    
    private final Main plugin;
    
    // Core game state
    private CountdownTimer timer;
    private boolean gameRunning = false;
    private int currentJokersPerPlayer = 0;
    private long gameStartTime = 0;
    
    // Player data tracking
    private final Map<UUID, Material> playerTargets = new HashMap<>();
    private final Map<UUID, Integer> playerPoints = new HashMap<>();
    private final Map<UUID, Integer> playerJokers = new HashMap<>();
    private final Map<UUID, List<Material>> playerCollectedItems = new HashMap<>();
    private final Map<UUID, List<CollectionEvent>> playerCollectionHistory = new HashMap<>();
    private final Map<UUID, Set<Material>> playerAssignedTargets = new HashMap<>();
    
    // Display systems
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private BukkitRunnable displayTask;
    
    public GameManager(Main plugin) {
        this.plugin = plugin;
    }
    
    // ===============================
    // PUBLIC GAME STATE ACCESSORS
    // ===============================
    
    public boolean isGameRunning() {
        return gameRunning;
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
    
    public int getPlayerPoints(UUID playerId) {
        return playerPoints.getOrDefault(playerId, 0);
    }
    
    public Map<UUID, Integer> getAllPlayerPoints() {
        return new HashMap<>(playerPoints);
    }
    
    public Map<UUID, List<Material>> getAllPlayerCollectedItems() {
        return new HashMap<>(playerCollectedItems);
    }
    
    public Map<UUID, List<CollectionEvent>> getAllPlayerCollectionHistory() {
        return new HashMap<>(playerCollectionHistory);
    }
    
    public long getGameStartTime() {
        return gameStartTime;
    }
    
    // ===============================
    // GAME LIFECYCLE MANAGEMENT
    // ===============================
    
    public void startGame(int seconds, int jokersPerPlayer) {
        gameRunning = true;
        currentJokersPerPlayer = jokersPerPlayer;
        
        // Clear any existing leaderboard from previous games
        plugin.clearLeaderboard();
        
        // Reset all player data for new game
        playerTargets.clear();
        playerPoints.clear();
        playerJokers.clear();
        playerCollectedItems.clear();
        playerCollectionHistory.clear();
        plugin.clearPlayerBackpacks();
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
        plugin.createLeaderboard(playerCollectedItems, playerPoints, playerCollectionHistory, gameStartTime);
        
        // Announce game end
        Bukkit.broadcastMessage("§f§lForceItem §7game ended!");
        
        // Schedule leaderboard display after brief delay
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getLeaderboardGUI().showLeaderboard(player);
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
    // GAME SETUP AND PREPARATION
    // ===============================
    
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Countdown: 3
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§e§l3", "§7Get ready...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.0f);
            }
        }, 0L);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Countdown: 2
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§6§l2", "§7Prepare yourself...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.2f);
            }
        }, 20L);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Countdown: 1
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§c§l1", "§7Almost there...", 0, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.4f);
            }
        }, 40L);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
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
            player.sendMessage("§f§lYour target item: §b" + plugin.formatMaterialName(randomItem));
            
            // Give player joker items and send joker message after
            if (jokersPerPlayer > 0) {
                ItemStack joker = plugin.createJokerItem();
                joker.setAmount(jokersPerPlayer);
                plugin.giveItemOrDrop(player, joker);
                player.sendMessage("§7You have been given §4§l" + jokersPerPlayer + " §4§lJokers§7! Right-click to skip your current target.");
            }
            
            // Give player backpack item
            ItemStack backpack = plugin.createBackpackItem();
            plugin.giveItemOrDrop(player, backpack);
            player.sendMessage("§7You have been given a §6§lBackpack§7! Right-click to access your personal storage.");
        }
        
        // Start the game timer and systems
        if (timer != null) timer.cancel();
        timer = new CountdownTimer(plugin, seconds, this::checkInventories, this::stopGame);
        timer.start();
        
        // Start the display system
        startDisplaySystem();
    }
    
    // ===============================
    // GAME MECHANICS
    // ===============================
    
    /**
     * Check all player inventories for target items
     * Called periodically by the countdown timer
     */
    public void checkInventories() {
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
                player.sendMessage("§f§lNew target: §b" + plugin.formatMaterialName(newTarget));
                
                // Update display immediately
                updatePlayerDisplay(player);
            }
        }
    }
    
    /**
     * Handle when a player uses a joker item
     * 
     * @param player The player using the joker
     * @param jokerItem The joker item stack
     * @return true if joker was successfully used
     */
    public boolean useJoker(Player player, ItemStack jokerItem) {
        if (!gameRunning) return false;
        
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
            if (jokerItem != null && jokerItem.getAmount() > 1) {
                jokerItem.setAmount(jokerItem.getAmount() - 1);
            } else if (jokerItem != null) {
                player.getInventory().remove(jokerItem);
            }
            
            // Assign new target
            Material newTarget = getRandomMaterialForPlayer(player.getUniqueId());
            playerTargets.put(player.getUniqueId(), newTarget);
            
            // Give player the item they jokered
            ItemStack jokerReward = new ItemStack(currentTarget);
            plugin.giveItemOrDrop(player, jokerReward);
            
            // Play pleasant joker use sound
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
            
            player.sendMessage("§4★ Joker used! §a+1 point!");
            player.sendMessage("§f§lYou received: §b" + plugin.formatMaterialName(currentTarget)); 
            player.sendMessage("§f§lNew target: §b" + plugin.formatMaterialName(newTarget));
            
            // Update action bar immediately
            updatePlayerDisplay(player);
            return true;
        } else {
            // Play error sound for no jokers left
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage("§cYou have no jokers left!");
            return false;
        }
    }
    
    /**
     * Handle when a player picks up their target item
     * 
     * @param player The player who picked up the item
     * @param targetMaterial The material that was picked up
     */
    public void handleTargetItemPickup(Player player, Material targetMaterial) {
        if (!gameRunning) return;
        
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null && targetMaterial == target) {
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
            player.sendMessage("§f§lNew target: §b" + plugin.formatMaterialName(newTarget));
            
            // Update display immediately
            updatePlayerDisplay(player);
        }
    }
    
    /**
     * Initialize a new player who joins during an active game
     * 
     * @param player The player who joined
     */
    public void initializeNewPlayer(Player player) {
        if (!gameRunning) return;
        
        UUID playerId = player.getUniqueId();
        
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
            ItemStack backpack = plugin.createBackpackItem();
            plugin.giveItemOrDrop(player, backpack);

            // Give them jokers
            if (currentJokersPerPlayer > 0) {
                ItemStack joker = plugin.createJokerItem();
                joker.setAmount(currentJokersPerPlayer);
                plugin.giveItemOrDrop(player, joker);
            }

            // Create their personal backpack inventory
            plugin.createPlayerBackpack(playerId, player.getName());
            
            // Welcome them to the ongoing game
            player.sendMessage("§6§lWelcome to the ongoing Force Item game!");
            player.sendMessage("§f§lTarget: §b" + plugin.formatMaterialName(playerTargets.get(playerId)));
            player.sendMessage("§f§lJokers remaining: §e" + playerJokers.get(playerId));
        }
        
        // Update their display immediately
        updatePlayerDisplay(player);
    }
    
    /**
     * Clean up when a player leaves
     * 
     * @param player The player who left
     */
    public void handlePlayerLeave(Player player) {
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
    
    // ===============================
    // DISPLAY SYSTEM MANAGEMENT
    // ===============================
    
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
        displayTask.runTaskTimer(plugin, 0L, 20L); // Run every second for boss bar updates
    }
    
    public void updatePlayerDisplay(Player player) {
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null) {
            int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
            
            // Update boss bar title (text will show at top, but bar itself is invisible)
            String targetName = plugin.formatMaterialName(target);
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
    
    // ===============================
    // UTILITY METHODS
    // ===============================
    
    /**
     * Get a random material for a specific player, avoiding duplicates they've already been assigned this game
     * 
     * @param playerId The UUID of the player
     * @return A Material that the player hasn't been assigned as a target this game
     */
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
    
    /**
     * Clean up all game state when plugin disables
     */
    public void cleanup() {
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
    }
}
