package com.sh4dowking.forceitem;

import org.bukkit.Material;

/**
 * Represents a collection event during the Force Item game
 * Tracks when an item was collected and whether it was obtained through normal collection or joker use
 * 
 * @author Sh4dowking
 * @version 1.1.0
 */
public class CollectionEvent {
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
