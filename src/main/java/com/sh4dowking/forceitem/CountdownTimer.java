package com.sh4dowking.forceitem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * CountdownTimer - Handles timed gameplay with action bar display
 * 
 * Manages countdown functionality for ForceItem games, displaying the remaining
 * time in the action bar for all online players and executing callbacks on
 * each tick and when the timer finishes.
 * 
 * @author Sh4dowking
 * @version 1.0
 */
public class CountdownTimer {
    private final Plugin plugin;
    private final Runnable onTick;    // Called every second
    private final Runnable onFinish;  // Called when timer reaches 0

    private int secondsLeft;
    private BukkitRunnable task;

    /**
     * Create a new countdown timer
     * 
     * @param plugin Plugin instance for scheduling tasks
     * @param totalSeconds Duration of the countdown
     * @param onTick Callback executed each second (can be null)
     * @param onFinish Callback executed when timer finishes (can be null)
     */
    public CountdownTimer(Plugin plugin, int totalSeconds, Runnable onTick, Runnable onFinish) {
        this.plugin = plugin;
        this.secondsLeft = totalSeconds;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    /**
     * Start the countdown timer
     * Begins the task that updates every second
     */
    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                // Update action bar display for all players
                displayActionBar();
                
                // Execute tick callback (e.g., inventory checks)
                if (onTick != null) onTick.run();
                
                // Check if timer has finished
                if (secondsLeft <= 0) {
                    cancel();
                    clearActionBar();
                    // Timer finished - execute completion callback
                    if (onFinish != null) onFinish.run();
                    return;
                }
                
                secondsLeft--;
            }
        };
        // Run every second (20 ticks)
        task.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * Cancel the countdown timer
     * Stops the task and clears action bar displays
     */
    public void cancel() {
        if (task != null) {
            task.cancel();
            clearActionBar(); // Remove timer display from all players
        }
    }

    /**
     * Get the remaining time in seconds
     * 
     * @return Current seconds remaining
     */
    public int getSecondsLeft() {
        return secondsLeft;
    }

    /**
     * Display the timer in action bar for all online players
     * Shows time in MM:SS format with clean styling
     */
    private void displayActionBar() {
        String timerMessage = "§f§l" + formatTime(secondsLeft);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(timerMessage);
        }
    }

    /**
     * Clear action bar display for all players
     * Used when timer is cancelled or game ends
     */
    private void clearActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(""); // Send empty message to clear
        }
    }

    /**
     * Format seconds into readable time format
     * 
     * @param seconds Time in seconds
     * @return Formatted time string (HH:MM:SS)
     */
    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}
