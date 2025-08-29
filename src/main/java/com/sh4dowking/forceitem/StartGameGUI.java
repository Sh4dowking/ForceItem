package com.sh4dowking.forceitem;

import java.util.Arrays;

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
    
    public StartGameGUI(Main plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the start game GUI for a player
     * 
     * @param player The player to show the GUI to
     */
    public void openStartGameGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        
        // Fill the entire GUI with gray stained glass panes
        fillWithGrayGlass(gui);
        
        // Add the 4 menu items and control buttons
        addMenuItems(gui);
        addControlButtons(gui);
        
        // Open the GUI for the player
        player.openInventory(gui);
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
                "§7Special player abilities",
                "§7Extra lives, bonuses, etc.",
                "",
                "§aClick to configure!"
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
            meta.setLore(Arrays.asList(
                "§7Begin ForceItem game",
                "§7Duration: §6" + formatTime(currentTimeMinutes),
                "§7Jokers: §6" + currentJokers + " per player",
                "§7Modifier: §6" + selectedModifier,
                "",
                "§aClick to start!"
            ));
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
        } else {
            // For other menus, just add navigation for now
            addNavigationItems(subGui);
        }
        
        // Open the sub-menu for the player
        player.openInventory(subGui);
    }
    
    /**
     * Add navigation items (back arrow and confirm button) to a sub-menu
     * 
     * @param inventory The inventory to add navigation items to
     */
    private void addNavigationItems(Inventory inventory) {
        // Back arrow in bottom left (slot 45)
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backArrow.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§c§lBack");
            backMeta.setLore(java.util.Arrays.asList("§7Return to main menu"));
            backArrow.setItemMeta(backMeta);
        }
        inventory.setItem(45, backArrow);
        
        // Confirm button in bottom right (slot 53)
        ItemStack confirmButton = new ItemStack(Material.LIME_DYE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName("§a§lConfirm");
            confirmMeta.setLore(java.util.Arrays.asList("§7Apply settings and return"));
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
                "§7Current time: §6" + formatTime(currentTimeMinutes)
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
                "§7Current setting: §6" + formatTime(currentTimeMinutes),
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
                "§7Current jokers: §6" + currentJokers + " per player"
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
                "§7Current setting: §6" + currentJokers + " joker" + (currentJokers == 1 ? "" : "s"),
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
                    openSubMenu(player, "Time Settings");
                }
                break;
            case 14: // Joker Settings
                if (clickedItem.getType() == Material.BARRIER) {
                    openSubMenu(player, "Joker Settings");
                }
                break;
            case 19: // Modifiers
                if (clickedItem.getType() == Material.ENCHANTED_BOOK) {
                    openSubMenu(player, "Modifiers");
                }
                break;
            case 25: // Perks
                if (clickedItem.getType() == Material.DIAMOND) {
                    openSubMenu(player, "Perks");
                }
                break;
            case 38: // Cancel button
                if (clickedItem.getType() == Material.RED_TERRACOTTA) {
                    player.closeInventory();
                }
                break;
            case 42: // Start Game button
                if (clickedItem.getType() == Material.LIME_TERRACOTTA) {
                    // Start game with configured settings including modifier
                    player.closeInventory();
                    int seconds = currentTimeMinutes * 60; // Convert minutes to seconds
                    String modifierToUse = selectedModifier.equals("Standard Game") ? null : selectedModifier;
                    plugin.startGame(seconds, currentJokers, modifierToUse); // Use configured settings with modifier
                    player.sendMessage("§a§lStarting ForceItem game!");
                    player.sendMessage("§f§lDuration: §b" + formatTime(currentTimeMinutes) + " §7| §f§lJokers: §b" + currentJokers + " §7| §f§lModifier: §b" + selectedModifier);
                }
                break;
        }
    }
    
    /**
     * Handle clicks in sub-menus
     */
    private void handleSubMenuClick(Player player, int slot, ItemStack clickedItem, String menuTitle) {
        if (slot == 45 && clickedItem.getType() == Material.ARROW) {
            // Back button clicked - return to main menu
            openStartGameGUI(player);
        } else if (slot == 53 && clickedItem.getType() == Material.LIME_DYE) {
            // Confirm button clicked - apply settings and return to main menu
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
        }
        
        // TODO: Handle other sub-menu specific items here
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
        if (isIncrement && (currentTimeMinutes + timeChange) > MAX_TIME_MINUTES) {
            wouldExceedLimits = true;
        } else if (!isIncrement && (currentTimeMinutes - timeChange) < MIN_TIME_MINUTES) {
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
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.3f); // High pitch pling for joker increment
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.7f); // Lower pitch pling for joker decrement
                }
                openSubMenu(player, "Time Settings");
            }
        }
    }
    
    /**
     * Adjust the current time setting
     * 
     * @param amount The amount to adjust by (in minutes)
     * @param isIncrement Whether to increment (true) or decrement (false)
     * @return true if the time was successfully changed, false if it hit limits
     */
    private boolean adjustTime(int amount, boolean isIncrement) {
        int newTime;
        if (isIncrement) {
            newTime = currentTimeMinutes + amount;
            if (newTime > MAX_TIME_MINUTES) {
                return false; // Hit maximum limit
            }
        } else {
            newTime = currentTimeMinutes - amount;
            if (newTime < MIN_TIME_MINUTES) {
                return false; // Hit minimum limit
            }
        }
        
        currentTimeMinutes = newTime;
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
        if (isIncrement && (currentJokers + jokerChange) > MAX_JOKERS) {
            wouldExceedLimits = true;
        } else if (!isIncrement && (currentJokers - jokerChange) < MIN_JOKERS) {
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
                openSubMenu(player, "Joker Settings");
            }
        }
    }
    
    /**
     * Adjust the current joker setting
     * 
     * @param amount The amount to adjust by (in jokers)
     * @param isIncrement Whether to increment (true) or decrement (false)
     * @return true if the jokers were successfully changed, false if it hit limits
     */
    private boolean adjustJokers(int amount, boolean isIncrement) {
        int newJokers;
        if (isIncrement) {
            newJokers = currentJokers + amount;
            if (newJokers > MAX_JOKERS) {
                return false; // Hit maximum limit
            }
        } else {
            newJokers = currentJokers - amount;
            if (newJokers < MIN_JOKERS) {
                return false; // Hit minimum limit
            }
        }
        
        currentJokers = newJokers;
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
                    
                    // Refresh the modifiers menu to show updated selection
                    Inventory currentInv = player.getOpenInventory().getTopInventory();
                    setupModifiersMenu(currentInv);
                }
                break;
                
            case 23:
                // Double Trouble button
                if (clickedItem.getType() == Material.NETHERITE_SCRAP) {
                    selectedModifier = "Double Trouble";
                    
                    // Refresh the modifiers menu to show updated selection
                    Inventory currentInv = player.getOpenInventory().getTopInventory();
                    setupModifiersMenu(currentInv);
                }
                break;
                
            default:
                // Invalid slot for modifier selection
                break;
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
        
        // TODO: Add any cleanup logic if needed
        // For now, no cleanup is necessary
    }
}
