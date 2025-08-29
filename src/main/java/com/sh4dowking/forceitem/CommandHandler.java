package com.sh4dowking.forceitem;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                Material target = plugin.getPlayerTarget(player.getUniqueId());
                if (target != null) {
                    // Play item info sound
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    plugin.getItemInfoGUI().openItemInfo(player, target);
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
        if (!plugin.hasPlayerTarget(targetUUID)) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not participating in the current game!");
            return true;
        }
        
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
        sender.sendMessage("§aSuccessfully skipped §f" + playerName + "§a's target!");
        sender.sendMessage("§7Previous target: §c" + currentTargetName);
        sender.sendMessage("§7New target: §b" + plugin.formatMaterialName(newTarget));

        // Broadcast to all players (optional - you can remove this if you want it to be silent)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != targetPlayer && player != sender) {
                player.sendMessage("§7Administrator skipped §f" + playerName + "§7's target item.");
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
        if (!plugin.hasPlayerTarget(targetUUID)) {
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
        targetPlayer.sendMessage("§a✨ You received §6" + jokerAmount + " joker" + (jokerAmount == 1 ? "" : "s") + " §afrom an administrator!");
        targetPlayer.sendMessage("§7Total jokers: §6" + newJokerCount);
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        
        // Notify the command sender
        sender.sendMessage("§aSuccessfully gave §6" + jokerAmount + " joker" + (jokerAmount == 1 ? "" : "s") + " §ato §f" + playerName + "§a!");
        sender.sendMessage("§7" + playerName + " now has §6" + newJokerCount + " total jokers§7.");
        
        return true;
    }
}
