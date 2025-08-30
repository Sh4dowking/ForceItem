package com.sh4dowking.forceitem.modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sh4dowking.forceitem.Main;

/**
 * Double Trouble Modifier
 * 
 * Each player receives 2 target items at once. When either target is collected,
 * both targets reset and the player receives 2 new different items to find.
 * 
 * @author Sh4dowking
 * @version 1.0.0
 */
public class DoubleTroubleModifier extends GameModifier {
    
    // Store dual targets for each player
    private final Map<UUID, List<Material>> playerDualTargets = new HashMap<>();
    
    public DoubleTroubleModifier(Main plugin) {
        super(plugin, 
              "Double Trouble", 
              "§7Each player gets §e2 targets§7 at once!\n§7Collect either one to get +1 point§7.",
              new ItemStack(Material.REDSTONE_BLOCK));
    }
    
    @Override
    public void onGameStart(List<Player> players, int jokersPerPlayer) {
        // Initialize dual targets for all players
        for (Player player : players) {
            UUID playerId = player.getUniqueId();
            List<Material> dualTargets = generateDualTargets(playerId);
            playerDualTargets.put(playerId, dualTargets);
            
            // Send special double trouble message
            player.sendMessage("§f§lYour targets: §b" + plugin.formatMaterialName(dualTargets.get(0)) + 
                             " §7or §b" + plugin.formatMaterialName(dualTargets.get(1)));
        }
    }
    
    @Override
    public boolean onTargetCollected(Player player, Material collectedItem) {
        UUID playerId = player.getUniqueId();
        List<Material> currentTargets = playerDualTargets.get(playerId);
        
        if (currentTargets == null || !currentTargets.contains(collectedItem)) {
            return false; // Not a valid target for this player
        }
        
        // Player collected one of their dual targets!
        
        // Determine which item was missed (the other target)
        Material missedItem = currentTargets.get(0).equals(collectedItem) ? 
                             currentTargets.get(1) : currentTargets.get(0);
        
        // Award points (+1 point for double trouble)
        int currentScore = plugin.getPlayerPoints(playerId);
        plugin.getGameManager().setPlayerPoints(playerId, currentScore + 1); // +1 point for double trouble

        // Record the collection in history with missed item info
        plugin.getGameManager().recordCollection(playerId, collectedItem, false, missedItem);
        
        // Play success sound (same as base game)
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        player.sendMessage("§a✓ Double Trouble target collected! +1 point!");

        // Generate new dual targets
        List<Material> newTargets = generateDualTargets(playerId);
        playerDualTargets.put(playerId, newTargets);
        
        // Announce new targets
        player.sendMessage("§f§lNew targets: §b" + plugin.formatMaterialName(newTargets.get(0)) + 
                         " §7or §b" + plugin.formatMaterialName(newTargets.get(1)));
        
        // Update display
        plugin.updatePlayerDisplay(player);
        
        return true; // We handled this collection
    }
    
    @Override
    public List<Material> getPlayerTargets(UUID playerId) {
        return playerDualTargets.getOrDefault(playerId, new ArrayList<>());
    }
    
    @Override
    public String modifyDisplayText(Player player, String baseDisplay) {
        UUID playerId = player.getUniqueId();
        List<Material> targets = playerDualTargets.get(playerId);
        
        if (targets == null || targets.size() < 2) {
            return baseDisplay; // Fallback to base display
        }
        
        // Create custom display for dual targets
        int points = plugin.getPlayerPoints(playerId);
        String target1 = plugin.formatMaterialName(targets.get(0));
        String target2 = plugin.formatMaterialName(targets.get(1));
        
        return "§f§lTargets: §b" + target1 + " §7or §b" + target2 + " §7| §f§lPoints: §a" + points;
    }
    
    @Override
    public boolean onJokerUsed(Player player) {
        UUID playerId = player.getUniqueId();
        List<Material> currentTargets = playerDualTargets.get(playerId);
        
        if (currentTargets == null || currentTargets.isEmpty()) {
            return false; // Let default handler deal with it
        }
        
        // Check if player has jokers
        int jokersLeft = plugin.getPlayerJokers(playerId);
        if (jokersLeft <= 0) {
            return false; // No jokers left, let default handler show error
        }
        
        // Process joker usage
        plugin.getGameManager().setPlayerJokers(playerId, jokersLeft - 1);
        
        // Award points (+1 for joker use in double trouble)
        int currentScore = plugin.getPlayerPoints(playerId);
        plugin.getGameManager().setPlayerPoints(playerId, currentScore + 1);
        
        // Give player one random item from their dual targets
        Material randomTarget = currentTargets.get((int) (Math.random() * currentTargets.size()));
        Material missedTarget = currentTargets.get(0).equals(randomTarget) ? 
                               currentTargets.get(1) : currentTargets.get(0);
        ItemStack rewardItem = new ItemStack(randomTarget);
        plugin.giveItemOrDrop(player, rewardItem);
        
        // Record the joker use with the random item given and missed item info
        plugin.getGameManager().recordCollection(playerId, randomTarget, true, missedTarget);
        
        // Generate new dual targets
        List<Material> newTargets = generateDualTargets(playerId);
        playerDualTargets.put(playerId, newTargets);
        
        // Play joker sound (same as base game)
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
        
        // Send messages
        player.sendMessage("§4★Joker used! §a+1 point!");
        player.sendMessage("§f§lYou received: §b" + plugin.formatMaterialName(randomTarget));
        player.sendMessage("§f§lNew targets: §b" + plugin.formatMaterialName(newTargets.get(0)) + 
                         " §7or §b" + plugin.formatMaterialName(newTargets.get(1)));
        
        // Update display
        plugin.updatePlayerDisplay(player);
        
        return true; // We handled the joker use
    }
    
    @Override
    public void onGameEnd() {
        // Clean up dual targets data
        playerDualTargets.clear();
    }
    
    @Override
    public void onPlayerJoin(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Check if the player already has dual targets (rejoining)
        if (playerDualTargets.containsKey(playerId)) {
            // Player is rejoining - just restore their targets without duplicate welcome message
            List<Material> existingTargets = playerDualTargets.get(playerId);
            player.sendMessage("§f§lYour Double Trouble targets: §b" + plugin.formatMaterialName(existingTargets.get(0)) + 
                             " §7or §b" + plugin.formatMaterialName(existingTargets.get(1)));
        } else {
            // New player joining mid-game - initialize dual targets
            List<Material> dualTargets = generateDualTargets(playerId);
            playerDualTargets.put(playerId, dualTargets);
            
            // Welcome message for new player (but GameManager already sent a general welcome)
            player.sendMessage("§6§lDouble Trouble mode activated!");
            player.sendMessage("§f§lYour targets: §b" + plugin.formatMaterialName(dualTargets.get(0)) + 
                             " §7or §b" + plugin.formatMaterialName(dualTargets.get(1)));
        }
    }
    
    @Override
    public void onPlayerLeave(UUID playerId) {
        // Don't remove dual targets - preserve them in case the player rejoins
        // The targets will be cleared when the game ends in onGameEnd()
    }
    
    @Override
    public boolean isMultiTarget() {
        return true;
    }
    
    @Override
    public int getMaxTargetsPerPlayer() {
        return 2;
    }
    
    @Override
    public boolean onPlayerSkipped(Player player) {
        UUID playerId = player.getUniqueId();
        List<Material> currentTargets = playerDualTargets.get(playerId);
        
        if (currentTargets == null) {
            return false; // Let default handler deal with it
        }
        
        // Generate new dual targets
        List<Material> newTargets = generateDualTargets(playerId);
        playerDualTargets.put(playerId, newTargets);
        
        // Notify the player
        player.sendMessage("§e⚡ Your Double Trouble targets have been skipped by an administrator!");
        player.sendMessage("§f§lNew targets: §b" + plugin.formatMaterialName(newTargets.get(0)) + 
                         " §7or §b" + plugin.formatMaterialName(newTargets.get(1)));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        
        // Update display
        plugin.updatePlayerDisplay(player);
        
        return true; // We handled the skip
    }
    
    /**
     * Generate two different random materials for a player
     * 
     * @param playerId The UUID of the player
     * @return List containing exactly 2 different materials
     */
    private List<Material> generateDualTargets(UUID playerId) {
        List<Material> targets = new ArrayList<>();
        
        // Get first target using existing method
        Material firstTarget = plugin.getRandomMaterialForPlayer(playerId);
        targets.add(firstTarget);
        
        // Get second target that's different from the first
        Material secondTarget;
        int attempts = 0;
        int maxAttempts = 50; // Prevent infinite loops
        
        do {
            secondTarget = plugin.getRandomMaterialForPlayer(playerId);
            attempts++;
        } while (secondTarget == firstTarget && attempts < maxAttempts);
        
        targets.add(secondTarget);
        return targets;
    }
}
