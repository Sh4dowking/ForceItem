package com.sh4dowking.forceitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * LeaderboardGUI - Comprehensive game results interface
 * 
 * Provides an interactive leaderboard system for viewing ForceItem game results.
 * Features include:
 * - Ranked player display with scores
 * - Detailed view of each player's collected items
 * - Pagination for large item collections
 * - Player navigation (previous/next)
 * - Click/drag protection
 * - Clean, themed visual design
 * 
 * Navigation:
 * - Main leaderboard shows all players ranked by score
 * - Click any player head to view their collected items
 * - Use arrow buttons to navigate between players
 * - Page navigation for viewing all collected items
 * 
 * @author Sh4dowking
 * @version 1.0
 */
public class LeaderboardGUI implements Listener {
    private final Main plugin;
    private final Map<UUID, List<Material>> playerCollectedItems;
    private final Map<UUID, Integer> playerScores;
    private final List<PlayerResult> sortedResults;
    
    // Pagination tracking for individual player views
    private final Map<UUID, Integer> currentPlayerPage = new HashMap<>();
    private final Map<UUID, Integer> currentViewingPlayer = new HashMap<>();
    
    public LeaderboardGUI(Main plugin, Map<UUID, List<Material>> collectedItems, Map<UUID, Integer> scores) {
        this.plugin = plugin;
        this.playerCollectedItems = new HashMap<>(collectedItems);
        this.playerScores = new HashMap<>(scores);
        
        // Create sorted results
        this.sortedResults = scores.entrySet().stream()
            .map(entry -> {
                Player player = Bukkit.getPlayer(entry.getKey());
                return new PlayerResult(
                    entry.getKey(),
                    player != null ? player.getName() : "Unknown",
                    entry.getValue(),
                    collectedItems.getOrDefault(entry.getKey(), new ArrayList<>())
                );
            })
            .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
            .collect(Collectors.toList());
    }
    
    public void showLeaderboard(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.WHITE + "" + ChatColor.BOLD + "🏆 ForceItem Leaderboard");
        
        // Fill background
        fillBackground(inv);
        
        // Add player heads for top players (positions 10-16)
        for (int i = 0; i < Math.min(7, sortedResults.size()); i++) {
            PlayerResult result = sortedResults.get(i);
            ItemStack head = createPlayerHead(result, i + 1);
            inv.setItem(10 + i, head);
        }
        
        // Add navigation info
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.WHITE + "🏆" + ChatColor.BOLD + " Leaderboard");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click a player to view details!");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Top performers:");
            for (int i = 0; i < Math.min(3, sortedResults.size()); i++) {
                PlayerResult result = sortedResults.get(i);
                lore.add(ChatColor.GOLD + "" + (i + 1) + ". " + ChatColor.WHITE + result.getPlayerName() + 
                        ChatColor.GRAY + " - " + ChatColor.GREEN + result.getScore() + " points");
            }
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(4, info);
        
        viewer.openInventory(inv);
        currentViewingPlayer.put(viewer.getUniqueId(), -1); // -1 means leaderboard view
        currentPlayerPage.put(viewer.getUniqueId(), 0);
    }
    
    public void showPlayerItems(Player viewer, int playerIndex) {
        if (playerIndex < 0 || playerIndex >= sortedResults.size()) {
            return;
        }
        
        PlayerResult result = sortedResults.get(playerIndex);
        int page = currentPlayerPage.getOrDefault(viewer.getUniqueId(), 0);
        
        String title = ChatColor.GOLD + "" + (playerIndex + 1) + ". " + ChatColor.WHITE + result.getPlayerName() + 
                      ChatColor.GRAY + " (" + ChatColor.GREEN + result.getScore() + " pts" + ChatColor.GRAY + ") - Page " + (page + 1);
        
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        // Fill background
        fillBackground(inv);
        
        // Add collected items (36 slots for items, from slot 9 to 44)
        List<Material> items = result.getCollectedItems();
        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, items.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            Material material = items.get(i);
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + formatMaterialName(material));
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Item #" + (i + 1)));
                item.setItemMeta(meta);
            }
            inv.setItem(9 + (i - startIndex), item);
        }
        
        // Navigation buttons
        addNavigationButtons(inv, playerIndex, page, items.size());
        
        viewer.openInventory(inv);
        currentViewingPlayer.put(viewer.getUniqueId(), playerIndex);
        currentPlayerPage.put(viewer.getUniqueId(), page);
    }
    
    private void addNavigationButtons(Inventory inv, int playerIndex, int currentPage, int totalItems) {
        int maxPages = (totalItems + 35) / 36;
        
        // Back to leaderboard
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "← Back to Leaderboard");
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(0, backButton);
        
        // Previous player
        if (playerIndex > 0) {
            ItemStack prevPlayer = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta prevMeta = prevPlayer.getItemMeta();
            if (prevMeta != null) {
                PlayerResult prevResult = sortedResults.get(playerIndex - 1);
                prevMeta.setDisplayName(ChatColor.AQUA + "◀ " + ChatColor.WHITE + prevResult.getPlayerName());
                prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "View previous player"));
                prevPlayer.setItemMeta(prevMeta);
            }
            inv.setItem(1, prevPlayer);
        }
        
        // Next player
        if (playerIndex < sortedResults.size() - 1) {
            ItemStack nextPlayer = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta nextMeta = nextPlayer.getItemMeta();
            if (nextMeta != null) {
                PlayerResult nextResult = sortedResults.get(playerIndex + 1);
                nextMeta.setDisplayName(ChatColor.AQUA + "▶ " + ChatColor.WHITE + nextResult.getPlayerName());
                nextMeta.setLore(Arrays.asList(ChatColor.GRAY + "View next player"));
                nextPlayer.setItemMeta(nextMeta);
            }
            inv.setItem(7, nextPlayer);
        }
        
        // Previous page
        if (currentPage > 0) {
            ItemStack prevPage = new ItemStack(Material.PAPER);
            ItemMeta prevMeta = prevPage.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.YELLOW + "◀ Previous Page");
                prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Page " + currentPage));
                prevPage.setItemMeta(prevMeta);
            }
            inv.setItem(45, prevPage);
        }
        
        // Page info
        ItemStack pageInfo = new ItemStack(Material.BOOK);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        if (pageMeta != null) {
            pageMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Page " + (currentPage + 1) + " of " + maxPages);
            pageMeta.setLore(Arrays.asList(ChatColor.GRAY + "Total items: " + ChatColor.AQUA + totalItems));
            pageInfo.setItemMeta(pageMeta);
        }
        inv.setItem(49, pageInfo);
        
        // Next page
        if (currentPage < maxPages - 1) {
            ItemStack nextPage = new ItemStack(Material.PAPER);
            ItemMeta nextMeta = nextPage.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page ▶");
                nextMeta.setLore(Arrays.asList(ChatColor.GRAY + "Page " + (currentPage + 2)));
                nextPage.setItemMeta(nextMeta);
            }
            inv.setItem(53, nextPage);
        }
    }
    
    private ItemStack createPlayerHead(PlayerResult result, int position) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            Player player = Bukkit.getPlayer(result.getPlayerId());
            if (player != null) {
                skullMeta.setOwningPlayer(player);
            }
            
            String positionColor = ChatColor.WHITE.toString();
            if (position == 1) positionColor = ChatColor.GOLD.toString();
            else if (position == 2) positionColor = ChatColor.GRAY.toString();
            else if (position == 3) positionColor = ChatColor.DARK_GRAY.toString();
            
            skullMeta.setDisplayName(positionColor + "" + position + ". " + ChatColor.WHITE + result.getPlayerName());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Score: " + ChatColor.AQUA + result.getScore() + " points");
            lore.add(ChatColor.WHITE + "Items collected: " + ChatColor.AQUA + result.getCollectedItems().size());
            lore.add(ChatColor.GRAY + "Click to view collected items!");
            skullMeta.setLore(lore);
            
            head.setItemMeta(skullMeta);
        }
        return head;
    }
    
    private void fillBackground(Inventory inv) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        
        // Fill top and bottom rows
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, glass);
            if (inv.getItem(45 + i) == null) inv.setItem(45 + i, glass);
        }
    }
    
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
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        // Check if this is any of our GUI inventories
        if (!title.contains("ForceItem Leaderboard") && !title.contains("pts") && !title.contains("Page")) {
            return;
        }
        
        // Cancel the event to prevent item extraction
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        int slot = event.getSlot();
        
        // Handle leaderboard view
        if (title.contains("ForceItem Leaderboard")) {
            if (slot >= 10 && slot <= 16) {
                int playerIndex = slot - 10;
                if (playerIndex < sortedResults.size()) {
                    showPlayerItems(player, playerIndex);
                }
            }
            return;
        }
        
        // Handle player items view
        int currentPlayer = currentViewingPlayer.getOrDefault(player.getUniqueId(), -1);
        int currentPage = currentPlayerPage.getOrDefault(player.getUniqueId(), 0);
        
        switch (slot) {
            case 0: // Back to leaderboard
                showLeaderboard(player);
                break;
            case 1: // Previous player
                if (currentPlayer > 0) {
                    currentPlayerPage.put(player.getUniqueId(), 0); // Reset to first page
                    showPlayerItems(player, currentPlayer - 1);
                }
                break;
            case 7: // Next player
                if (currentPlayer < sortedResults.size() - 1) {
                    currentPlayerPage.put(player.getUniqueId(), 0); // Reset to first page
                    showPlayerItems(player, currentPlayer + 1);
                }
                break;
            case 45: // Previous page
                if (currentPage > 0) {
                    currentPlayerPage.put(player.getUniqueId(), currentPage - 1);
                    showPlayerItems(player, currentPlayer);
                }
                break;
            case 53: // Next page
                if (currentPlayer >= 0) {
                    List<Material> items = sortedResults.get(currentPlayer).getCollectedItems();
                    int maxPages = (items.size() + 35) / 36;
                    if (currentPage < maxPages - 1) {
                        currentPlayerPage.put(player.getUniqueId(), currentPage + 1);
                        showPlayerItems(player, currentPlayer);
                    }
                }
                break;
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        // Check if this is any of our GUI inventories
        if (title.contains("ForceItem Leaderboard") || title.contains("pts") || title.contains("Page")) {
            // Cancel the event to prevent item dragging
            event.setCancelled(true);
        }
    }
    
    private static class PlayerResult {
        private final UUID playerId;
        private final String playerName;
        private final int score;
        private final List<Material> collectedItems;
        
        public PlayerResult(UUID playerId, String playerName, int score, List<Material> collectedItems) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.score = score;
            this.collectedItems = new ArrayList<>(collectedItems);
        }
        
        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public List<Material> getCollectedItems() { return collectedItems; }
    }
}
