package com.sh4dowking.forceitem;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * CommandHandler - Handles all ForceItem plugin commands
 * 
 * Separates command logic from main plugin class for better organization.
 * All commands delegate to appropriate methods in the Main plugin instance.
 * 
 * @author Sh4dowking
 * @version 1.1.0
 */
public class CommandHandler implements CommandExecutor {
    
    private final Main plugin;
    
    public CommandHandler(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Start a new ForceItem game
        if (command.getName().equalsIgnoreCase("startgame")) {
            return handleStartGameCommand(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("stopgame")) {
            return handleStopGameCommand(sender);
        }
        
        if (command.getName().equalsIgnoreCase("leaderboard")) {
            return handleLeaderboardCommand(sender);
        }
        
        if (command.getName().equalsIgnoreCase("item")) {
            return handleItemCommand(sender);
        }
        
        if (command.getName().equalsIgnoreCase("skip")) {
            return handleSkipCommand(sender, args);
        }
        
        if (command.getName().equalsIgnoreCase("givejoker")) {
            return handleGiveJokerCommand(sender, args);
        }
        
        return false;
    }
    
    /**
     * Handle the /startgame command
     */
    private boolean handleStartGameCommand(CommandSender sender, String[] args) {
        if (plugin.isGameRunning()) {
            sender.sendMessage("§cA game is already running! Use /stopgame to stop it first.");
            return true;
        }
        
        // If no arguments provided, open the GUI for players
        if (args.length == 0) {
            if (sender instanceof Player player) {
                // Try to open the GUI - it will handle the safety check internally
                plugin.getStartGameGUI().openStartGameGUI(player);
                return true;
            } else {
                sender.sendMessage("§cConsole must provide arguments: /startgame <seconds> <jokers>");
                return true;
            }
        }
        
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /startgame <seconds> <number of jokers>");
            sender.sendMessage("§7Or just type §e/startgame §7to open the GUI");
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
            
            plugin.startGame(seconds, jokers);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cPlease enter valid numbers for time and jokers.");
        }
        return true;
    }
    
    /**
     * Handle the /stopgame command
     */
    private boolean handleStopGameCommand(CommandSender sender) {
        if (plugin.isGameRunning()) {
            plugin.stopGame();
            sender.sendMessage("§cGame stopped.");
        } else {
            sender.sendMessage("§cNo game is currently running.");
        }
        return true;
    }
    
    /**
     * Handle the /leaderboard command
     */
    private boolean handleLeaderboardCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (plugin.getLeaderboardGUI() != null) {
                plugin.getLeaderboardGUI().showLeaderboard(player);
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
    
    /**
     * Handle the /item command
     */
    private boolean handleItemCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (plugin.isGameRunning()) {
                // Check if there's an active modifier first
                if (plugin.getModifierManager().hasActiveModifier()) {
                    // Modifier is active - show modifier targets
                    List<Material> modifierTargets = plugin.getModifierManager().getPlayerTargets(player.getUniqueId());
                    
                    if (!modifierTargets.isEmpty()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        
                        if (modifierTargets.size() == 1) {
                            // Single target from modifier
                            showStandardTarget(player, modifierTargets.get(0));
                        } else {
                            // Multiple targets (like Double Trouble) - show custom display
                            showModifierTargets(player, modifierTargets);
                        }
                    } else {
                        player.sendMessage("§cYou don't have target items assigned!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
                } else {
                    // No modifier active - standard game mode
                    Material target = plugin.getPlayerTarget(player.getUniqueId());
                    if (target != null) {
                        // Play item info sound
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        showStandardTarget(player, target);
                    } else {
                        player.sendMessage("§cYou don't have a target item assigned!");
                        // Play error sound
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
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
    
    /**
     * Handle the /skip command
     */
    private boolean handleSkipCommand(CommandSender sender, String[] args) {
        // Check if sender has operator privileges
        if (!sender.isOp()) {
            sender.sendMessage("§cYou must have operator privileges to use this command!");
            return true;
        }
        
        // Check if game is running
        if (!plugin.isGameRunning()) {
            sender.sendMessage("§cNo game is currently running! Start a game first with /startgame.");
            return true;
        }
        
        // Check argument count
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /skip <playername>");
            return true;
        }
        
        // Find the target player
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        
        if (targetPlayer == null) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not online!");
            return true;
        }
        
        // Check if player is in the game (has a target)
        UUID targetUUID = targetPlayer.getUniqueId();
        boolean hasStandardTarget = plugin.hasPlayerTarget(targetUUID);
        List<Material> modifierTargets = plugin.getModifierManager().getPlayerTargets(targetUUID);
        
        if (!hasStandardTarget && (modifierTargets == null || modifierTargets.isEmpty())) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not participating in the current game!");
            return true;
        }
        
        // Check if modifier wants to handle the skip
        if (plugin.getModifierManager().hasActiveModifier()) {
            // For modifier modes, regenerate modifier targets
            if (plugin.getModifierManager().onPlayerSkipped(targetPlayer)) {
                // Notify the command sender
                sender.sendMessage("§aSuccessfully skipped §f" + playerName + "'s§a targets!");
                
                // Broadcast to all players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player != targetPlayer && player != sender) {
                        player.sendMessage("§7Administrator skipped §f" + playerName + "'s§7 target items.");
                    }
                }
                return true;
            }
        }
        
        // Standard game mode skip logic
        // Get current target for feedback
        Material currentTarget = plugin.getPlayerTarget(targetUUID);
        String currentTargetName = currentTarget != null ? plugin.formatMaterialName(currentTarget) : "Unknown";
        
        // Assign new target without affecting jokers or points
        Material newTarget = plugin.getRandomMaterialForPlayer(targetUUID);
        plugin.setPlayerTarget(targetUUID, newTarget);
        
        // Notify the target player
        targetPlayer.sendMessage("§e⚡ Your item has been skipped by an administrator!");
        targetPlayer.sendMessage("§f§lNew target: §b" + plugin.formatMaterialName(newTarget));
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        
        // Update player display immediately
        plugin.updatePlayerDisplay(targetPlayer);
        
        // Notify the command sender
        sender.sendMessage("§aSuccessfully skipped §f" + playerName + "'s§a target!");
        sender.sendMessage("§f§lPrevious target: §c" + currentTargetName);
        sender.sendMessage("§f§lNew target: §b" + plugin.formatMaterialName(newTarget));

        // Broadcast to all players (optional - you can remove this if you want it to be silent)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != targetPlayer && player != sender) {
                player.sendMessage("§eAdministrator skipped §f" + playerName + "'s§e target item.");
            }
        }
        
        return true;
    }
    
    /**
     * Handle the /givejoker command
     */
    private boolean handleGiveJokerCommand(CommandSender sender, String[] args) {
        // Check if sender has operator privileges
        if (!sender.isOp()) {
            sender.sendMessage("§cYou must have operator privileges to use this command!");
            return true;
        }
        
        // Check if game is running
        if (!plugin.isGameRunning()) {
            sender.sendMessage("§cNo game is currently running! Start a game first with /startgame.");
            return true;
        }
        
        // Check argument count
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /givejoker <playername> <amount>");
            return true;
        }
        
        // Find the target player
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        
        if (targetPlayer == null) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not online!");
            return true;
        }
        
        // Parse joker amount
        int jokerAmount;
        try {
            jokerAmount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number! Please enter a valid joker amount.");
            return true;
        }
        
        if (jokerAmount <= 0) {
            sender.sendMessage("§cJoker amount must be greater than 0!");
            return true;
        }
        
        if (jokerAmount > 64) {
            sender.sendMessage("§cJoker amount cannot exceed 64!");
            return true;
        }
        
        // Check if player is in the game
        UUID targetUUID = targetPlayer.getUniqueId();
        boolean hasStandardTarget = plugin.hasPlayerTarget(targetUUID);
        List<Material> modifierTargets = plugin.getModifierManager().getPlayerTargets(targetUUID);
        
        if (!hasStandardTarget && (modifierTargets == null || modifierTargets.isEmpty())) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not participating in the current game!");
            return true;
        }
        
        // Give jokers to the player
        int currentJokers = plugin.getPlayerJokers(targetUUID);
        int newJokerCount = currentJokers + jokerAmount;
        plugin.setPlayerJokers(targetUUID, newJokerCount);
        
        ItemStack joker = plugin.createJokerItem();
        joker.setAmount(jokerAmount);
        plugin.giveItemOrDrop(targetPlayer, joker);
        
        // Notify the target player
        targetPlayer.sendMessage("§e✨ You received " + jokerAmount + " joker" + (jokerAmount == 1 ? "" : "s") + " §efrom an administrator!");
        targetPlayer.sendMessage("§f§lTotal jokers: §4" + newJokerCount);
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        
        // Notify the command sender
        sender.sendMessage("§aSuccessfully gave " + jokerAmount + " joker" + (jokerAmount == 1 ? "" : "s") + " §ato §f" + playerName + "§a!");
        sender.sendMessage("§f" + playerName + " now has §4" + newJokerCount + " total jokers§7.");
        
        return true;
    }
    
    /**
     * Show modifier targets in a custom GUI (for Double Trouble etc.)
     * 
     * @param player The player to show targets to
     * @param targets List of target materials
     */
    private void showModifierTargets(Player player, List<Material> targets) {
        // Create a GUI to show multiple targets
        String title = ChatColor.WHITE + "" + ChatColor.BOLD + "Double Trouble Targets";
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Fill background with gray glass
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, glass);
        }
        
        // Display targets in slots 11 and 15 (symmetrical)
        if (targets.size() >= 1) {
            ItemStack target1 = new ItemStack(targets.get(0));
            ItemMeta meta1 = target1.getItemMeta();
            if (meta1 != null) {
                meta1.setDisplayName(ChatColor.GOLD + plugin.formatMaterialName(targets.get(0)));
                meta1.setLore(Arrays.asList(ChatColor.GRAY + "Target #1", ChatColor.YELLOW + "Collect this item for +1 point!"));
                target1.setItemMeta(meta1);
            }
            gui.setItem(11, target1);
        }
        
        if (targets.size() >= 2) {
            ItemStack target2 = new ItemStack(targets.get(1));
            ItemMeta meta2 = target2.getItemMeta();
            if (meta2 != null) {
                meta2.setDisplayName(ChatColor.GOLD + plugin.formatMaterialName(targets.get(1)));
                meta2.setLore(Arrays.asList(ChatColor.GRAY + "Target #2", ChatColor.YELLOW + "Collect this item for +1 point!"));
                target2.setItemMeta(meta2);
            }
            gui.setItem(15, target2);
        }
        
        player.openInventory(gui);
    }
    
    /**
     * Show standard single target in a custom GUI
     * 
     * @param player The player to show target to
     * @param target The target material
     */
    private void showStandardTarget(Player player, Material target) {
        // Create a GUI to show single target
        String title = ChatColor.WHITE + "" + ChatColor.BOLD + "Current Target";
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Fill background with gray glass
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, glass);
        }
        
        // Display target in center slot (13)
        ItemStack targetItem = new ItemStack(target);
        ItemMeta meta = targetItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + plugin.formatMaterialName(target));
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Your current target",
                ChatColor.YELLOW + "Collect this item for +1 point!"
            ));
            targetItem.setItemMeta(meta);
        }
        gui.setItem(13, targetItem);
        
        player.openInventory(gui);
    }
}
