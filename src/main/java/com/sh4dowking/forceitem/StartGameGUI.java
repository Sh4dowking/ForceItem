package com.sh4dowking.forceitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sh4dowking.forceitem.perks.GamePerk;

/**
 * StartGameGUI - Provides a graphical interface for starting ForceItem games
 * 
 * Allows players to configure game settings through an intuitive GUI interface
 * instead of using command arguments. Features customizable time duration,
 * joker amounts, and other game parameters.
 * 
 * @author Sh4dowking
 * @version 1.1.0
 */
public class StartGameGUI implements Listener {
    
    private final Main plugin;
    private static final String GUI_TITLE = "§f§lStart ForceItem Game";
    private static final String SUB_GUI_TITLE_PREFIX = "§f§lForceItem - ";
    private static final int GUI_SIZE = 54; // Double chest (6 rows)
    
    // Time settings (in minutes)
    private static final int MIN_TIME_MINUTES = 1;
    private static final int MAX_TIME_MINUTES = 24 * 60; // 24 hours
    private int currentTimeMinutes = 30; // Default 30 minutes
    
    // Joker settings
    private static final int MIN_JOKERS = 0;
    private static final int MAX_JOKERS = 64;
    private int currentJokers = 3; // Default 3 jokers
    
    // Modifier settings
    private String selectedModifier = "Standard Game"; // Default standard game
    
    // Backup values for discarding changes
    private int backupTimeMinutes;
    private int backupJokers;
    private String backupModifier;
    private Set<String> backupActivePerks;
    
    // Temporary values used while editing in submenus
    private int tempTimeMinutes;
    private int tempJokers;
    
    // GUI access control - only one player can have the GUI open at a time
    private UUID currentGUIUser = null;
    
    /**
     * Clear the GUI user lock (called when game starts successfully)
     */
    public void clearGUILock() {
        currentGUIUser = null;
    }
    
    /**
     * Check if a specific player is currently using the GUI
     * 
     * @param playerId The UUID of the player to check
     * @return true if this player is the current GUI user
     */
    public boolean isCurrentGUIUser(UUID playerId) {
        return currentGUIUser != null && currentGUIUser.equals(playerId);
    }
    
    public StartGameGUI(Main plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the start game GUI for a player
     * 
     * @param player The player to show the GUI to
     * @return true if GUI was opened successfully, false if another player has it open
     */
    public boolean openStartGameGUI(Player player) {
        // Check if another player already has the GUI open
        if (currentGUIUser != null) {
            Player currentUser = Bukkit.getPlayer(currentGUIUser);
            if (currentUser != null && currentUser.isOnline()) {
                // Another player has the GUI open
                player.sendMessage("§cThe start game GUI is currently being used by §4" + currentUser.getName() + "§c!");
                player.sendMessage("§cPlease wait until they finish configuring the game settings.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            } else {
                // Previous user is offline, clear the lock
                currentGUIUser = null;
            }
        }
        
        // Set this player as the current GUI user
        currentGUIUser = player.getUniqueId();
        
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        
        // Fill the entire GUI with gray stained glass panes
        fillWithGrayGlass(gui);
        
        // Add the 4 menu items and control buttons
        addMenuItems(gui);
        addControlButtons(gui);
        
        // Open the GUI for the player
        player.openInventory(gui);
        return true;
    }
    
    /**
     * Add the main menu items to the GUI
     * 
     * @param inventory The inventory to add items to
     */
    private void addMenuItems(Inventory inventory) {
        // Place items in specific slots as requested
        inventory.setItem(19, createModifiersItem());
        inventory.setItem(12, createTimeSettingsItem());
        inventory.setItem(14, createJokerSettingsItem());
        inventory.setItem(25, createPerksItem());
    }
    
    /**
     * Add control buttons to the GUI
     * 
     * @param inventory The inventory to add buttons to
     */
    private void addControlButtons(Inventory inventory) {
        // Add cancel button in slot 38 (bottom left area)
        inventory.setItem(38, createCancelButton());

        // Add start game button in slot 42 (bottom right area)  
        inventory.setItem(42, createStartGameButton());
    }
    
    /**
     * Fill the entire inventory with gray stained glass panes
     * 
     * @param inventory The inventory to fill
     */
    private void fillWithGrayGlass(Inventory inventory) {
        ItemStack grayGlass = createGrayGlassPane();
        
        // Fill all slots with gray glass
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, grayGlass);
        }
    }
    
    /**
     * Create a gray stained glass pane item
     * 
     * @return ItemStack of gray stained glass pane
     */
    private ItemStack createGrayGlassPane() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" "); // Empty name to hide default name
            glass.setItemMeta(meta);
        }
        return glass;
    }
    
    /**
     * Create the Time Settings menu item
     * 
     * @return ItemStack for time settings
     */
    private ItemStack createTimeSettingsItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§lTime Settings");
            meta.setLore(java.util.Arrays.asList(
                "§7Configure game duration",
                "§7Current: §6" + formatTime(currentTimeMinutes),
                "",
                "§aClick to configure!"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the Joker Settings menu item
     * 
     * @return ItemStack for joker settings
     */
    private ItemStack createJokerSettingsItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§4§lJoker Settings");
            meta.setLore(java.util.Arrays.asList(
                "§7Configure jokers per player",
                "§7Current: §6" + currentJokers + " joker" + (currentJokers == 1 ? "" : "s"),
                "",
                "§aClick to configure!"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the Modifiers menu item
     * 
     * @return ItemStack for modifiers
     */
    private ItemStack createModifiersItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lModifiers");
            meta.setLore(Arrays.asList(
                "§7Game modification options",
                "§7Change gameplay mechanics",
                "",
                "§7Current: §6" + selectedModifier,
                "",
                "§aClick to configure!"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the Perks menu item
     * 
     * @return ItemStack for perks
     */
    private ItemStack createPerksItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§lPerks");
            meta.setLore(java.util.Arrays.asList(
                "§7Perks modification option",
                "§7Passive buffs and abilities",
                "",
                "§7Current: §6" + plugin.getPerkManager().getActivePerkDisplay(),
                "",
                "§a Click to configure!"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the Cancel button
     * 
     * @return ItemStack for cancel button
     */
    private ItemStack createCancelButton() {
        ItemStack item = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lCancel");
            meta.setLore(java.util.Arrays.asList(
                "§7Close this menu",
                "",
                "§cClick to cancel!"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the Start Game button
     * 
     * @return ItemStack for start game button
     */
    private ItemStack createStartGameButton() {
        ItemStack item = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a§lStart Game");
            
            // Build lore with current settings
            List<String> lore = new ArrayList<>();
            lore.add("§7Begin ForceItem game");
            lore.add("§7Duration: §6" + formatTime(currentTimeMinutes));
            lore.add("§7Jokers: §6" + currentJokers + " per player");
            lore.add("§7Modifier: §6" + selectedModifier);
            
            // Add active perks to the display
            String activePerksDisplay = plugin.getPerkManager().getActivePerkDisplay();
            lore.add("§7Active Perks: §6" + activePerksDisplay);
            
            lore.add("");
            lore.add("§aClick to start!");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Open a sub-menu for the specified setting type
     * 
     * @param player The player to show the sub-menu to
     * @param menuType The type of menu (Time Settings, Joker Settings, Modifiers, Perks)
     */
    public void openSubMenu(Player player, String menuType) {
        // Save backup values before entering the submenu
        backupTimeMinutes = currentTimeMinutes;
        backupJokers = currentJokers;
        backupModifier = selectedModifier;
        backupActivePerks = new HashSet<>(plugin.getPerkManager().getActivePerks());
        
        // Initialize temporary values for editing
        tempTimeMinutes = currentTimeMinutes;
        tempJokers = currentJokers;
        
        String title = SUB_GUI_TITLE_PREFIX + menuType;
        Inventory subGui = Bukkit.createInventory(null, GUI_SIZE, title);
        
        // Fill with gray glass panes
        fillWithGrayGlass(subGui);
        
        // Add specific content based on menu type
        if (menuType.equals("Time Settings")) {
            setupTimeSettingsMenu(subGui);
        } else if (menuType.equals("Joker Settings")) {
            setupJokerSettingsMenu(subGui);
        } else if (menuType.equals("Modifiers")) {
            setupModifiersMenu(subGui);
        } else if (menuType.equals("Perks")) {
            setupPerksMenu(subGui);
        } else {
            // For other menus, just add navigation for now
            addNavigationItems(subGui);
        }
        
        // Open the sub-menu for the player
        player.openInventory(subGui);
    }
    
    /**
     * Refresh the current submenu without resetting temporary values
     * 
     * @param player The player to refresh the menu for
     * @param menuType The type of menu to refresh
     */
    private void refreshSubMenu(Player player, String menuType) {
        String title = SUB_GUI_TITLE_PREFIX + menuType;
        Inventory subGui = Bukkit.createInventory(null, GUI_SIZE, title);
        
        // Fill with gray glass panes
        fillWithGrayGlass(subGui);
        
        // Add specific content based on menu type
        if (menuType.equals("Time Settings")) {
            setupTimeSettingsMenu(subGui);
        } else if (menuType.equals("Joker Settings")) {
            setupJokerSettingsMenu(subGui);
        } else if (menuType.equals("Modifiers")) {
            setupModifiersMenu(subGui);
        } else if (menuType.equals("Perks")) {
            setupPerksMenu(subGui);
        } else {
            // For other menus, just add navigation for now
            addNavigationItems(subGui);
        }
        
        // Open the refreshed sub-menu for the player
        player.openInventory(subGui);
    }
    
    /**
     * Add navigation items (back arrow and confirm button) to a sub-menu
     * 
     * @param inventory The inventory to add navigation items to
     */
    private void addNavigationItems(Inventory inventory) {
        // Back button in bottom left (slot 45) - now using red dye
        ItemStack backButton = new ItemStack(Material.RED_DYE);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§c§lDiscard & Back");
            backMeta.setLore(java.util.Arrays.asList(
                "§7Discard all changes made",
                "§7Return to main menu"
            ));
            backButton.setItemMeta(backMeta);
        }
        inventory.setItem(45, backButton);
        
        // Confirm button in bottom right (slot 53)
        ItemStack confirmButton = new ItemStack(Material.LIME_DYE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName("§a§lSave & Confirm");
            confirmMeta.setLore(java.util.Arrays.asList(
                "§7Save all changes made",
                "§7Return to main menu"
            ));
            confirmButton.setItemMeta(confirmMeta);
        }
        inventory.setItem(53, confirmButton);
    }
    
    /**
     * Setup the Time Settings sub-menu with clock display and increment/decrement controls
     * 
     * @param inventory The inventory to setup
     */
    private void setupTimeSettingsMenu(Inventory inventory) {
        // Add clock at the top center (slot 4)
        inventory.setItem(4, createTimeDisplayClock());
        
        // Add increment buttons (lime terracotta) in row 3 (slots 20-24) - shifted one right
        inventory.setItem(20, createTimeButton(Material.LIME_TERRACOTTA, "§a+1 Minute", 1, true));
        inventory.setItem(21, createTimeButton(Material.LIME_TERRACOTTA, "§a+5 Minutes", 5, true));
        inventory.setItem(22, createTimeButton(Material.LIME_TERRACOTTA, "§a+10 Minutes", 10, true));
        inventory.setItem(23, createTimeButton(Material.LIME_TERRACOTTA, "§a+30 Minutes", 30, true));
        inventory.setItem(24, createTimeButton(Material.LIME_TERRACOTTA, "§a+1 Hour", 60, true));
        
        // Add decrement buttons (red terracotta) in row 4 (slots 29-33) - shifted one right
        inventory.setItem(29, createTimeButton(Material.RED_TERRACOTTA, "§c-1 Minute", 1, false));
        inventory.setItem(30, createTimeButton(Material.RED_TERRACOTTA, "§c-5 Minutes", 5, false));
        inventory.setItem(31, createTimeButton(Material.RED_TERRACOTTA, "§c-10 Minutes", 10, false));
        inventory.setItem(32, createTimeButton(Material.RED_TERRACOTTA, "§c-30 Minutes", 30, false));
        inventory.setItem(33, createTimeButton(Material.RED_TERRACOTTA, "§c-1 Hour", 60, false));
        
        // Add navigation items
        addNavigationItems(inventory);
    }
    
    /**
     * Setup the Joker Settings sub-menu with joker display and increment/decrement controls
     * 
     * @param inventory The inventory to setup
     */
    private void setupJokerSettingsMenu(Inventory inventory) {
        // Add joker display at the top center (slot 4)
        inventory.setItem(4, createJokerDisplayItem());
        
        // Add increment buttons (lime terracotta) in row 3 (slots 21-23) - only 3 buttons for 1, 3, 5
        inventory.setItem(21, createJokerButton(Material.LIME_TERRACOTTA, "§a+1 Joker", 1, true));
        inventory.setItem(22, createJokerButton(Material.LIME_TERRACOTTA, "§a+3 Jokers", 3, true));
        inventory.setItem(23, createJokerButton(Material.LIME_TERRACOTTA, "§a+5 Jokers", 5, true));
        
        // Add decrement buttons (red terracotta) in row 4 (slots 30-32) - only 3 buttons for 1, 3, 5
        inventory.setItem(30, createJokerButton(Material.RED_TERRACOTTA, "§c-1 Joker", 1, false));
        inventory.setItem(31, createJokerButton(Material.RED_TERRACOTTA, "§c-3 Jokers", 3, false));
        inventory.setItem(32, createJokerButton(Material.RED_TERRACOTTA, "§c-5 Jokers", 5, false));
        
        // Add navigation items
        addNavigationItems(inventory);
    }
    
    /**
     * Setup the modifiers menu
     * 
     * @param inventory The inventory to setup
     */
    private void setupModifiersMenu(Inventory inventory) {
        // Add title item
        ItemStack title = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta titleMeta = title.getItemMeta();
        if (titleMeta != null) {
            titleMeta.setDisplayName("§5§lModifier Selection");
            titleMeta.setLore(Arrays.asList(
                "§7Select a game modifier",
                "",
                "§7Current modifier: §6" + selectedModifier
            ));
            title.setItemMeta(titleMeta);
        }
        inventory.setItem(4, title);
        
                // Add Standard Game button (slot 21)
        Material standardMaterial = Material.GRASS_BLOCK;
        ItemStack standardButton = new ItemStack(standardMaterial);
        ItemMeta standardMeta = standardButton.getItemMeta();
        if (standardMeta != null) {
            standardMeta.setDisplayName("§a§lStandard Game");
            standardMeta.setLore(Arrays.asList(
                "§7Classic ForceItem gameplay",
                "§7• 1 target item per player",
                "",
                selectedModifier.equals("Standard Game") ? "§a✓ Currently selected" : "§7Click to select"
            ));
            standardButton.setItemMeta(standardMeta);
        }
        inventory.setItem(21, standardButton);
        
        // Add Double Trouble button (slot 23)
        Material doubleTroubleMaterial = Material.NETHERITE_SCRAP;
        ItemStack doubleTroubleButton = new ItemStack(doubleTroubleMaterial);
        ItemMeta doubleTroubleMeta = doubleTroubleButton.getItemMeta();
        if (doubleTroubleMeta != null) {
            doubleTroubleMeta.setDisplayName("§c§lDouble Trouble");
            doubleTroubleMeta.setLore(Arrays.asList(
                "§7Enhanced gameplay mode",
                "§7• 2 target items per player",
                "§7• Collect either target to progress",
                "",
                selectedModifier.equals("Double Trouble") ? "§a✓ Currently selected" : "§7Click to select"
            ));
            doubleTroubleButton.setItemMeta(doubleTroubleMeta);
        }
        inventory.setItem(23, doubleTroubleButton);
        
        // Add navigation items
        addNavigationItems(inventory);
    }
    
    /**
     * Setup the Perks selection sub-menu
     * 
     * @param inventory The inventory to setup
     */
    private void setupPerksMenu(Inventory inventory) {
        // Add title item
        ItemStack title = new ItemStack(Material.DIAMOND);
        ItemMeta titleMeta = title.getItemMeta();
        if (titleMeta != null) {
            titleMeta.setDisplayName("§b§lPerk Selection");
            titleMeta.setLore(Arrays.asList(
                "§7Select perks to enhance your game",
                "§7Multiple perks can be active at once",
                "",
                "§7Active perks: §a" + plugin.getPerkManager().getActivePerkDisplay()
            ));
            title.setItemMeta(titleMeta);
        }
        inventory.setItem(4, title);
        
        // Add available perks from PerkManager
        int slot = 19; // Starting position for perks
        for (Map.Entry<String, GamePerk> entry : plugin.getPerkManager().getRegisteredPerks().entrySet()) {
            String perkName = entry.getKey();
            GamePerk perk = entry.getValue();
            boolean isActive = plugin.getPerkManager().isPerkActive(perkName);
            
            ItemStack perkItem = new ItemStack(perk.getDisplayMaterial());
            ItemMeta perkMeta = perkItem.getItemMeta();
            if (perkMeta != null) {
                perkMeta.setDisplayName("§6§l" + perk.getPerkName());
                
                // Create lore with perk description
                List<String> lore = new ArrayList<>(perk.getLore());
                lore.add("");
                if (isActive) {
                    lore.add("§a✓ Currently active");
                    lore.add("§7Click to deactivate");
                } else {
                    lore.add("§7Click to activate");
                }
                
                perkMeta.setLore(lore);
                perkItem.setItemMeta(perkMeta);
            }
            
            inventory.setItem(slot, perkItem);
            slot++; // Move to next slot for next perk
            
            if (slot > 25) break; // Limit to available slots
        }
        
        // Add navigation items
        addNavigationItems(inventory);
    }
    
    /**
     * Create a time adjustment button
     * 
     * @param material The material for the button
     * @param displayName The display name
     * @param amount The amount to change (in minutes)
     * @param isIncrement Whether this is an increment (true) or decrement (false) button
     * @return ItemStack for the time button
     */
    private ItemStack createTimeButton(Material material, String displayName, int amount, boolean isIncrement) {
        // Set stack size to represent the time amount (capped at 64 for large values like 60 minutes)
        int stackSize = Math.min(amount, 64);
        ItemStack item = new ItemStack(material, stackSize);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(java.util.Arrays.asList(
                "§7Click to " + (isIncrement ? "increase" : "decrease") + " time by " + amount + " minute" + (amount == 1 ? "" : "s"),
                "",
                "§7Current time: §6" + formatTime(tempTimeMinutes)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the time display clock showing current settings
     * 
     * @return ItemStack for the clock display
     */
    private ItemStack createTimeDisplayClock() {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§lGame Duration");
            meta.setLore(java.util.Arrays.asList(
                "§7Current setting: §6" + formatTime(tempTimeMinutes),
                "",
                "§7Use buttons below to adjust time",
                "§7Range: " + MIN_TIME_MINUTES + " minute - " + (MAX_TIME_MINUTES/60) + " hours"
            ));
            clock.setItemMeta(meta);
        }
        return clock;
    }
    
    /**
     * Format time in minutes to readable format
     * 
     * @param minutes Time in minutes
     * @return Formatted time string
     */
    private String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + " hour" + (hours == 1 ? "" : "s");
            } else {
                return hours + "h " + remainingMinutes + "m";
            }
        }
    }
    
    /**
     * Create a joker adjustment button
     * 
     * @param material The material for the button
     * @param displayName The display name
     * @param amount The amount to change (in jokers)
     * @param isIncrement Whether this is an increment (true) or decrement (false) button
     * @return ItemStack for the joker button
     */
    private ItemStack createJokerButton(Material material, String displayName, int amount, boolean isIncrement) {
        // Set stack size to represent the joker amount
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(java.util.Arrays.asList(
                "§7Click to " + (isIncrement ? "increase" : "decrease") + " jokers by " + amount,
                "",
                "§7Current jokers: §6" + tempJokers + " per player"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create the joker display item showing current settings
     * 
     * @return ItemStack for the joker display
     */
    private ItemStack createJokerDisplayItem() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§4§lJokers Per Player");
            meta.setLore(java.util.Arrays.asList(
                "§7Current setting: §6" + tempJokers + " joker" + (tempJokers == 1 ? "" : "s"),
                "",
                "§7Use buttons below to adjust jokers",
                "§7Range: " + MIN_JOKERS + " - " + MAX_JOKERS + " jokers"
            ));
            barrier.setItemMeta(meta);
        }
        return barrier;
    }
    
    /**
     * Handle clicks in the start game GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        // Check if this is one of our GUIs
        if (!title.equals(GUI_TITLE) && !title.startsWith(SUB_GUI_TITLE_PREFIX)) {
            return;
        }
        
        // Cancel all clicks to prevent item movement
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // Handle main GUI clicks
        if (title.equals(GUI_TITLE)) {
            handleMainMenuClick(player, event.getSlot(), clickedItem);
        }
        // Handle sub-menu clicks  
        else if (title.startsWith(SUB_GUI_TITLE_PREFIX)) {
            handleSubMenuClick(player, event.getSlot(), clickedItem, title);
        }
    }
    
    /**
     * Handle clicks in the main start game menu
     */
    private void handleMainMenuClick(Player player, int slot, ItemStack clickedItem) {
        switch (slot) {
            case 12: // Time Settings
                if (clickedItem.getType() == Material.CLOCK) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openSubMenu(player, "Time Settings");
                }
                break;
            case 14: // Joker Settings
                if (clickedItem.getType() == Material.BARRIER) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openSubMenu(player, "Joker Settings");
                }
                break;
            case 19: // Modifiers
                if (clickedItem.getType() == Material.ENCHANTED_BOOK) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openSubMenu(player, "Modifiers");
                }
                break;
            case 25: // Perks
                if (clickedItem.getType() == Material.DIAMOND) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openSubMenu(player, "Perks");
                }
                break;
            case 38: // Cancel button
                if (clickedItem.getType() == Material.RED_TERRACOTTA) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.closeInventory();
                }
                break;
            case 42: // Start Game button
                if (clickedItem.getType() == Material.LIME_TERRACOTTA) {
                    // Play experience orb collected sound
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    
                    // Start game with configured settings including modifier
                    player.closeInventory();
                    int seconds = currentTimeMinutes * 60; // Convert minutes to seconds
                    String modifierToUse = selectedModifier.equals("Standard Game") ? null : selectedModifier;
                    plugin.startGame(seconds, currentJokers, modifierToUse); // Use configured settings with modifier
                    
                    // Clear the GUI lock since the game started successfully
                    clearGUILock();
                    
                    player.sendMessage("§a§lStarting ForceItem game!");
                    player.sendMessage("§f§lDuration: §b" + formatTime(currentTimeMinutes) + " §7| §f§lJokers: §b" + currentJokers);
                    StringBuilder perkStringBuilder = new StringBuilder();
                    if(plugin.getPerkManager().getActivePerks().isEmpty()) {
                        perkStringBuilder.append("None");
                    } else {
                        for (String perk : plugin.getPerkManager().getActivePerks()) {
                            perkStringBuilder.append(perk).append(", ");
                        }
                        perkStringBuilder.setLength(perkStringBuilder.length() - 2);
                    }
                    player.sendMessage("§f§lModifier: §b" + selectedModifier);
                    player.sendMessage("§f§lPerks: §b" + perkStringBuilder.toString());
                }
                break;
        }
    }
    
    /**
     * Handle clicks in sub-menus
     */
    private void handleSubMenuClick(Player player, int slot, ItemStack clickedItem, String menuTitle) {
        if (slot == 45 && clickedItem.getType() == Material.RED_DYE) {
            // Back button clicked - discard changes and return to main menu
            discardChanges();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.7f);
            openStartGameGUI(player);
        } else if (slot == 53 && clickedItem.getType() == Material.LIME_DYE) {
            // Confirm button clicked - save changes and return to main menu
            saveChanges();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.3f);
            openStartGameGUI(player);
        } else if (menuTitle.contains("Time Settings")) {
            // Handle time setting adjustments
            handleTimeAdjustment(player, slot, clickedItem);
        } else if (menuTitle.contains("Joker Settings")) {
            // Handle joker setting adjustments
            handleJokerAdjustment(player, slot, clickedItem);
        } else if (menuTitle.contains("Modifiers")) {
            // Handle modifier selection
            handleModifierSelection(player, slot, clickedItem);
        } else if (menuTitle.contains("Perks")) {
            // Handle perk selection
            handlePerkSelection(player, slot, clickedItem);
        }
        
        // TODO: Handle other sub-menu specific items here
    }
    
    /**
     * Discard changes made in submenus and restore backup values
     */
    private void discardChanges() {
        currentTimeMinutes = backupTimeMinutes;
        currentJokers = backupJokers;
        selectedModifier = backupModifier;
        
        // Restore perk state - first get current active perks
        Set<String> currentActivePerks = new HashSet<>(plugin.getPerkManager().getActivePerks());
        
        // Disable all currently active perks that weren't active before
        for (String perkName : currentActivePerks) {
            if (!backupActivePerks.contains(perkName)) {
                plugin.getPerkManager().disablePerk(perkName);
            }
        }
        
        // Enable all perks that were active before but aren't active now
        for (String perkName : backupActivePerks) {
            if (!currentActivePerks.contains(perkName)) {
                plugin.getPerkManager().enablePerk(perkName);
            }
        }
    }
    
    /**
     * Save changes made in submenus to the main settings
     */
    private void saveChanges() {
        // Apply temporary time and joker settings to the main settings
        currentTimeMinutes = tempTimeMinutes;
        currentJokers = tempJokers;
        // Modifier and perk changes are already applied directly, so no need to save them here
    }
    
    /**
     * Handle time adjustment button clicks
     * 
     * @param player The player who clicked
     * @param slot The slot that was clicked
     * @param clickedItem The item that was clicked
     */
    private void handleTimeAdjustment(Player player, int slot, ItemStack clickedItem) {
        int timeChange = 0;
        boolean isIncrement = false;
        
        // Handle increment buttons (lime terracotta, slots 20-24)
        if (clickedItem.getType() == Material.LIME_TERRACOTTA) {
            isIncrement = true;
            switch (slot) {
                case 20: timeChange = 1; break;    // +1 minute
                case 21: timeChange = 5; break;    // +5 minutes
                case 22: timeChange = 10; break;   // +10 minutes
                case 23: timeChange = 30; break;   // +30 minutes
                case 24: timeChange = 60; break;   // +1 hour
            }
        }
        // Handle decrement buttons (red terracotta, slots 29-33)
        else if (clickedItem.getType() == Material.RED_TERRACOTTA) {
            isIncrement = false;
            switch (slot) {
                case 29: timeChange = 1; break;   // -1 minute
                case 30: timeChange = 5; break;   // -5 minutes
                case 31: timeChange = 10; break;  // -10 minutes
                case 32: timeChange = 30; break;  // -30 minutes
                case 33: timeChange = 60; break;  // -1 hour
            }
        }
        
        // If no valid button was clicked, return early (don't play sounds)
        if (timeChange == 0) {
            return;
        }
        
        // Check if the change would exceed limits before applying
        boolean wouldExceedLimits = false;
        if (isIncrement && (tempTimeMinutes + timeChange) > MAX_TIME_MINUTES) {
            wouldExceedLimits = true;
        } else if (!isIncrement && (tempTimeMinutes - timeChange) < MIN_TIME_MINUTES) {
            wouldExceedLimits = true;
        }
        
        if (wouldExceedLimits) {
            // Play error sound and send error message
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            
            if (isIncrement) {
                player.sendMessage("§cMaximum time limit reached! (24 hours)");
            } else {
                player.sendMessage("§cMinimum time limit reached! (1 minute)");
            }
        } else {
            // Apply the change
            boolean changed = adjustTime(timeChange, isIncrement);
            
            if (changed) {
                // Play different success sounds for increment vs decrement
                if (isIncrement) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.3f); // High pitch pling for time increment
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.7f); // Lower pitch pling for time decrement
                }
                refreshSubMenu(player, "Time Settings");
            }
        }
    }
    
    /**
     * Adjust the current time setting (using temporary variable)
     * 
     * @param amount The amount to adjust by (in minutes)
     * @param isIncrement Whether to increment (true) or decrement (false)
     * @return true if the time was successfully changed, false if it hit limits
     */
    private boolean adjustTime(int amount, boolean isIncrement) {
        int newTime;
        if (isIncrement) {
            newTime = tempTimeMinutes + amount;
            if (newTime > MAX_TIME_MINUTES) {
                return false; // Hit maximum limit
            }
        } else {
            newTime = tempTimeMinutes - amount;
            if (newTime < MIN_TIME_MINUTES) {
                return false; // Hit minimum limit
            }
        }
        
        tempTimeMinutes = newTime;
        return true;
    }
    
    /**
     * Handle joker adjustment button clicks
     * 
     * @param player The player who clicked
     * @param slot The slot that was clicked
     * @param clickedItem The item that was clicked
     */
    private void handleJokerAdjustment(Player player, int slot, ItemStack clickedItem) {
        int jokerChange = 0;
        boolean isIncrement = false;
        
        // Handle increment buttons (lime terracotta, slots 21-23)
        if (clickedItem.getType() == Material.LIME_TERRACOTTA) {
            isIncrement = true;
            switch (slot) {
                case 21: jokerChange = 1; break;   // +1 joker
                case 22: jokerChange = 3; break;   // +3 jokers
                case 23: jokerChange = 5; break;   // +5 jokers
            }
        }
        // Handle decrement buttons (red terracotta, slots 30-32)
        else if (clickedItem.getType() == Material.RED_TERRACOTTA) {
            isIncrement = false;
            switch (slot) {
                case 30: jokerChange = 1; break;   // -1 joker
                case 31: jokerChange = 3; break;   // -3 jokers
                case 32: jokerChange = 5; break;   // -5 jokers
            }
        }
        
        // If no valid button was clicked, return early (don't play sounds)
        if (jokerChange == 0) {
            return;
        }
        
        // Check if the change would exceed limits before applying
        boolean wouldExceedLimits = false;
        if (isIncrement && (tempJokers + jokerChange) > MAX_JOKERS) {
            wouldExceedLimits = true;
        } else if (!isIncrement && (tempJokers - jokerChange) < MIN_JOKERS) {
            wouldExceedLimits = true;
        }
        
        if (wouldExceedLimits) {
            // Play error sound and send error message
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            
            if (isIncrement) {
                player.sendMessage("§cMaximum joker limit reached! (" + MAX_JOKERS + " jokers)");
            } else {
                player.sendMessage("§cMinimum joker limit reached! (" + MIN_JOKERS + " jokers)");
            }
        } else {
            // Apply the change
            boolean changed = adjustJokers(jokerChange, isIncrement);
            
            if (changed) {
                // Play different success sounds for increment vs decrement
                if (isIncrement) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.3f); // High pitch pling for joker increment
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.7f); // Lower pitch pling for joker decrement
                }
                refreshSubMenu(player, "Joker Settings");
            }
        }
    }
    
    /**
     * Adjust the current joker setting (using temporary variable)
     * 
     * @param amount The amount to adjust by (in jokers)
     * @param isIncrement Whether to increment (true) or decrement (false)
     * @return true if the jokers were successfully changed, false if it hit limits
     */
    private boolean adjustJokers(int amount, boolean isIncrement) {
        int newJokers;
        if (isIncrement) {
            newJokers = tempJokers + amount;
            if (newJokers > MAX_JOKERS) {
                return false; // Hit maximum limit
            }
        } else {
            newJokers = tempJokers - amount;
            if (newJokers < MIN_JOKERS) {
                return false; // Hit minimum limit
            }
        }
        
        tempJokers = newJokers;
        return true;
    }
    
    /**
     * Handle modifier selection clicks
     * 
     * @param player The player who clicked
     * @param slot The slot that was clicked
     * @param clickedItem The item that was clicked
     */
    private void handleModifierSelection(Player player, int slot, ItemStack clickedItem) {
        switch (slot) {
            case 21:
                // Standard Game button
                if (clickedItem.getType() == Material.GRASS_BLOCK) {
                    selectedModifier = "Standard Game";
                    
                    // Play sound feedback
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    
                    // Refresh the modifiers menu to show updated selection
                    refreshSubMenu(player, "Modifiers");
                }
                break;
                
            case 23:
                // Double Trouble button
                if (clickedItem.getType() == Material.NETHERITE_SCRAP) {
                    selectedModifier = "Double Trouble";
                    
                    // Play sound feedback
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    
                    // Refresh the modifiers menu to show updated selection
                    refreshSubMenu(player, "Modifiers");
                }
                break;
                
            default:
                // Invalid slot for modifier selection
                break;
        }
    }
    
    /**
     * Handle perk selection clicks
     */
    private void handlePerkSelection(Player player, int slot, ItemStack clickedItem) {
        // Check if click is in the perk display area (slots 19-25)
        if (slot >= 19 && slot <= 25) {
            String itemName = null;
            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                itemName = clickedItem.getItemMeta().getDisplayName();
            }
            
            if (itemName != null) {
                // Extract perk name from display name (remove color codes and formatting)
                String perkName = itemName.replaceAll("§[0-9a-fk-or]", "").trim();
                
                // Toggle the perk
                if (plugin.getPerkManager().isPerkActive(perkName)) {
                    plugin.getPerkManager().disablePerk(perkName);
                } else {
                    plugin.getPerkManager().enablePerk(perkName);
                }
                
                // Refresh the perks menu to show updated selections
                refreshSubMenu(player, "Perks");
                
                // Play sound feedback
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            }
        }
    }
    
    /**
     * Handle when start game GUIs are closed
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        
        // Check if this is one of our GUIs
        if (!title.equals(GUI_TITLE) && !title.startsWith(SUB_GUI_TITLE_PREFIX)) {
            return;
        }
        
        // If this is the main GUI, clear the GUI user lock
        if (title.equals(GUI_TITLE) && event.getPlayer() instanceof Player player) {
            if (currentGUIUser != null && currentGUIUser.equals(player.getUniqueId())) {
                currentGUIUser = null;
            }
        }
    }
}
