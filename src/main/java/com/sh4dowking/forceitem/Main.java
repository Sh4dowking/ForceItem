package com.sh4dowking.forceitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
 * 
 * @author Sh4dowking
 * @version 1.0
 */
public class Main extends JavaPlugin implements Listener {
    
    // Core game components
    private CountdownTimer timer;
    private boolean gameRunning = false;
    private LeaderboardGUI leaderboardGUI;
    private ItemInfoGUI itemInfoGUI;
    
    // Player data tracking
    private final Map<UUID, Material> playerTargets = new HashMap<>();
    private final Map<UUID, Integer> playerPoints = new HashMap<>();
    private final Map<UUID, Integer> playerJokers = new HashMap<>();
    private final Map<UUID, List<Material>> playerCollectedItems = new HashMap<>();
    
    // Display systems
    private BossBar gameBossBar;
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
        if (gameBossBar != null) {
            gameBossBar.removeAll();
        }
        
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

    /**
     * Handle all plugin commands
     * 
     * @param sender Command sender (player or console)
     * @param command The command being executed
     * @param label Command alias used
     * @param args Command arguments
     * @return true if command was handled successfully
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Start a new ForceItem game
        if (command.getName().equalsIgnoreCase("startgame")) {
            if (gameRunning) {
                sender.sendMessage("§cA game is already running! Use /stopgame to stop it first.");
                return true;
            }
            
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /startgame <seconds> <number of jokers>");
                return true;
            }
            
            try {
                int seconds = Integer.parseInt(args[0]);
                int jokers = Integer.parseInt(args[1]);
                
                if (seconds <= 0) {
                    sender.sendMessage("§cTime must be greater than 0 seconds.");
                    return true;
                }
                if (jokers < 0) {
                    sender.sendMessage("§cNumber of jokers cannot be negative.");
                    return true;
                }
                
                startGame(seconds, jokers);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cPlease enter valid numbers for time and jokers.");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("stopgame")) {
            if (gameRunning) {
                stopGame();
                sender.sendMessage("§cGame stopped.");
            } else {
                sender.sendMessage("§cNo game is currently running.");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("leaderboard")) {
            if (sender instanceof Player player) {
                if (leaderboardGUI != null) {
                    leaderboardGUI.showLeaderboard(player);
                } else {
                    player.sendMessage("§cNo game data available. Start a game first!");
                }
            } else {
                if (sender != null) {
                    sender.sendMessage("§cOnly players can view the leaderboard GUI.");
                }
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("item")) {
            if (sender instanceof Player player) {
                if (gameRunning) {
                    Material target = playerTargets.get(player.getUniqueId());
                    if (target != null) {
                        // Play item info sound
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        itemInfoGUI.openItemInfo(player, target);
                    } else {
                        player.sendMessage("§cYou don't have a target item assigned!");
                        // Play error sound
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
                } else {
                    player.sendMessage("§cNo game is currently running!");
                    // Play error sound
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else {
                if (sender != null) {
                    sender.sendMessage("§cOnly players can use this command!");
                }
            }
            return true;
        }
        
        return false;
    }

    /**
     * Initialize and start a new ForceItem game
     * 
     * @param seconds Duration of the game in seconds
     * @param jokersPerPlayer Number of jokers each player receives
     */
    private void startGame(int seconds, int jokersPerPlayer) {
        gameRunning = true;
        
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
        
        // Announce game start to all players
        Bukkit.broadcastMessage("§f§lForceItem §7game started! §eTime: §b" + seconds + "s §7| §eJokers: §b" + jokersPerPlayer);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material randomItem = getRandomMaterial();
            playerTargets.put(player.getUniqueId(), randomItem);
            playerPoints.put(player.getUniqueId(), 0);
            playerJokers.put(player.getUniqueId(), jokersPerPlayer);
            playerCollectedItems.put(player.getUniqueId(), new ArrayList<>());
            
            // Play game start sound
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.5f);
            
            // Send target message first
            player.sendMessage("§f§lYour target item: §b" + formatMaterialName(randomItem));
            
            // Give player joker items and send joker message after
            if (jokersPerPlayer > 0) {
                ItemStack joker = createJokerItem();
                joker.setAmount(jokersPerPlayer);
                player.getInventory().addItem(joker);
                player.sendMessage("§eYou have been given §b" + jokersPerPlayer + " §eJokers! §7Right-click to skip your current target.");
            }
        }
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
                
                // Play success sound for the player
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
                
                player.sendMessage("§6You collected your item! New target assigned.");
                // Assign new target
                Material newTarget = getRandomMaterial();
                playerTargets.put(player.getUniqueId(), newTarget);
                player.sendMessage("§6Your new target item: §b" + formatMaterialName(newTarget));
                
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
        
        // Cancel the game timer
        if (timer != null) timer.cancel();
        
        // Stop display systems (boss bar, action bar updates)
        stopDisplaySystem();
        
        // Initialize leaderboard GUI for result viewing
        leaderboardGUI = new LeaderboardGUI(this, playerCollectedItems, playerPoints);
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
            int jokersLeft = playerJokers.getOrDefault(player.getUniqueId(), 0);
            int itemsCollected = playerCollectedItems.getOrDefault(player.getUniqueId(), new ArrayList<>()).size();
            player.sendMessage("§f§lYour final score: §a" + points + " points");
            player.sendMessage("§fItems collected: §b" + itemsCollected);
            player.sendMessage("§fJokers remaining: §e" + jokersLeft);
            player.sendMessage("§7Leaderboard GUI will open shortly! Use §f/leaderboard §7to view it again.");
        }
    }

    private Material getRandomMaterial() {
        return SurvivalItems.getRandomMaterial();
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
            meta.setDisplayName(ChatColor.RED + "★ " + ChatColor.BOLD + "Joker");
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
     * Check if an ItemStack is a joker item
     * 
     * @param item ItemStack to check
     * @return true if item is a valid joker
     */
    private boolean isJokerItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        String expectedName = ChatColor.RED + "★ " + ChatColor.RED + ChatColor.BOLD + "Joker";
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
                    }
                    
                    // Remove one joker from inventory
                    if (item != null && item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else if (item != null) {
                        player.getInventory().remove(item);
                    }
                    
                    // Assign new target
                    Material newTarget = getRandomMaterial();
                    playerTargets.put(player.getUniqueId(), newTarget);
                    
                    // Play pleasant joker use sound
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
                    
                    player.sendMessage("§e★ Joker used! §a+1 point! §7New target: §b" + formatMaterialName(newTarget));
                    player.sendMessage("§6Jokers remaining: §b" + (jokersLeft - 1));
                    
                    // Update action bar immediately
                    updatePlayerDisplay(player);
                } else {
                    // Play error sound for no jokers left
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage("§cYou have no jokers left!");
                }
            }
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
    public void onPlayerPickup(PlayerAttemptPickupItemEvent event) {
        if (!gameRunning) return;
        Player player = event.getPlayer();
        Material target = playerTargets.get(player.getUniqueId());
        if (target != null && event.getItem().getItemStack().getType() == target) {
            int newScore = playerPoints.get(player.getUniqueId()) + 1;
            playerPoints.put(player.getUniqueId(), newScore);
            
            // Add to collected items
            playerCollectedItems.get(player.getUniqueId()).add(target);
            
            player.sendMessage("§a✓ You collected your item! §7New target assigned.");
            // Assign new target
            Material newTarget = getRandomMaterial();
            playerTargets.put(player.getUniqueId(), newTarget);
            player.sendMessage("§f§lNew target: §b" + formatMaterialName(newTarget));
            
            // Update display immediately
            updatePlayerDisplay(player);
        }
    }
    
    private void startDisplaySystem() {
        // Create an invisible boss bar by setting progress to 0 and using transparent style
        gameBossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        gameBossBar.setProgress(0.0); // This makes the actual bar invisible
        for (Player player : Bukkit.getOnlinePlayers()) {
            gameBossBar.addPlayer(player);
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
            
            if (gameBossBar != null) {
                gameBossBar.setTitle(displayText);
                // Ensure the bar remains invisible
                gameBossBar.setProgress(0.0);
            }
        }
    }
    
    private void stopDisplaySystem() {
        if (displayTask != null && !displayTask.isCancelled()) {
            displayTask.cancel();
        }
        if (gameBossBar != null) {
            gameBossBar.removeAll();
            gameBossBar = null;
        }
    }
}
