# ForceItem - Advanced Competitive Minecraft Minigame Plugin

A feature-rich, competitive minigame for Minecraft servers where players race to collect randomly assigned target items within a time limit. Now featuring a complete GUI system, configurable game modes, and a powerful modifier architecture!

## 🎮 Game Overview

ForceItem challenges players to quickly gather specific items from the survival world. Each player receives unique target items and must collect them before time runs out. With the new GUI system, players can customize game settings, choose modifiers, and enjoy enhanced gameplay experiences!

### 🆕 Major Updates (v2.0)
- **🖱️ Complete GUI System**: Professional 54-slot interface for game configuration
- **⚙️ Game Customization**: Configurable time (1min-24hr) and joker settings (0-64)
- **🔥 Modifier System**: Scalable architecture for custom game modes
- **⚡ Double Trouble Modifier**: Dual-target gameplay with strategic choices
- **📊 Enhanced Leaderboards**: Collection tracking with missed item information
- **🎨 Beautiful UI**: Custom item displays with colored names and descriptive lore
- **🛠️ Admin Tools**: Enhanced /skip and /givejoker commands work in all modes
- **🔊 Audio Feedback**: Consistent sound effects across all interfaces

### 🎯 Game Modes

#### 🎲 Standard Mode
- Single target item per player
- +1 point per collected item
- Jokers give random item and +1 point

#### ⚡ Double Trouble Mode
- **Two simultaneous targets** per player
- Collect **either target** for +1 point
- New dual targets assigned after each collection
- Jokers give **one random** item from current targets
- Custom boss bar showing both targets
- Special leaderboard showing alternative targets

## ✨ Features

### 🖱️ GUI System
- **StartGame Interface**: 54-slot double chest configuration
- **Time Settings**: 1 minute to 24 hours with increment/decrement controls
- **Joker Configuration**: 0-64 jokers with visual feedback
- **Modifier Selection**: Choose between Standard and Double Trouble modes
- **Visual Feedback**: Item highlighting, button states, and sound effects
- **Custom Item GUIs**: Beautiful displays for both standard and modifier targets

### 🎮 Core Gameplay
- **Timed Competition**: Highly configurable game duration
- **Smart Target System**: Anti-duplicate assignments with modifier support
- **Enhanced Joker System**: Strategic item skipping with proper rewards
- **Personal Storage**: 27-slot backpack system during games
- **Real-time Display**: Individual boss bars with mode-specific information
- **Collection Tracking**: Comprehensive history with timestamps and methods

### 🔥 Modifier Architecture
- **Scalable Framework**: Easy addition of new game modes
- **GameModifier Base**: Abstract class for consistent modifier behavior
- **ModifierManager**: Centralized lifecycle and event management
- **DoubleTroubleModifier**: First concrete implementation with dual targets
- **Admin Support**: All commands work seamlessly with modifiers

### 🎨 User Experience
- **Themed Design**: Consistent white/aqua/gold color scheme
- **Sound Integration**: Audio feedback for all interactions
- **Inventory Protection**: Prevents item extraction from custom GUIs
- **Error Handling**: Comprehensive validation and user feedback
- **Accessibility**: Clear visual cues and intuitive navigation

### 📊 Advanced Leaderboard
- **Collection History**: Complete timeline of player achievements
- **Method Tracking**: Normal collection vs joker usage indicators
- **Double Trouble Insights**: Shows alternative targets that weren't collected
- **Pagination**: Browse through all collected items
- **Player Navigation**: Quick switching between player results

## 🚀 Installation

1. Download the latest `ForceItem.jar` from releases
2. Place in your server's `plugins/` directory
3. Start/restart your server
4. Use `/startgame` to open the configuration GUI!

**Requirements:**
- Minecraft Server 1.18+ (Paper/Spigot)
- Java 17+

## 📋 Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/startgame` | `forceitem.admin` | Open game configuration GUI |
| `/stopgame` | `forceitem.admin` | End current game |
| `/skip <playername>` | `forceitem.admin` | Skip player's targets (works in all modes) |
| `/givejoker <player> <amount>` | `forceitem.admin` | Give jokers to player (works in all modes) |
| `/leaderboard` | `forceitem.use` | View game results |
| `/item` | `forceitem.use` | Show current target(s) in custom GUI |

### Command Examples
```bash
/startgame              # Open GUI to configure and start game
/skip PlayerName        # Skip PlayerName's current targets
/givejoker Player 5     # Give 5 jokers to Player
/stopgame              # Emergency stop
```

## 🎯 How to Play

### 🎲 Standard Mode
1. **Configure**: Use `/startgame` GUI to set time, jokers, and select Standard mode
2. **Countdown**: Professional 3-2-1 countdown builds anticipation
3. **Collect**: Find your single target item (+1 point)
4. **Use Jokers**: Skip difficult targets and receive the item (+1 point)
5. **View Info**: Use `/item` for beautiful target display with lore

### ⚡ Double Trouble Mode
1. **Select**: Choose "Double Trouble" modifier in the `/startgame` GUI
2. **Dual Targets**: Receive TWO target items simultaneously
3. **Strategic Choice**: Collect **either target** for +1 point
4. **New Targets**: Get fresh dual targets after each collection
5. **Smart Jokers**: Get ONE random item from your two targets
6. **Enhanced Display**: Boss bar shows both targets, `/item` shows both with lore

### 📊 Leaderboard Features
- **Timeline View**: See exactly when each item was collected
- **Method Indicators**: ⚡ symbol for joker-obtained items
- **Double Trouble Insights**: "Alternative target" shows the item you didn't collect
- **Player Navigation**: Click player heads to view detailed collections

## ⚙️ Configuration

### 🎮 Game Settings (via GUI)
- **Time Range**: 1 minute to 24 hours (increment/decrement buttons)
- **Joker Range**: 0 to 64 jokers per player
- **Modifier Selection**: Visual selection with item highlighting
- **Quick Presets**: Commonly used configurations available

### 🔧 Game Rules
- **Balanced Scoring**: +1 point for all collection methods
- **World Reset**: Time set to day, player stats normalized
- **Keep Inventory**: Enabled during games for seamless play
- **Item Whitelist**: Curated survival-obtainable items only

## 🛠️ Developer Information

### 📁 Project Structure
```
src/main/java/com/sh4dowking/forceitem/
├── Main.java                    # Core plugin and event handling
├── GameManager.java            # Game logic and state management
├── CommandHandler.java         # Command processing with GUI integration
├── StartGameGUI.java           # Complete game configuration interface
├── LeaderboardGUI.java         # Enhanced results display
├── ItemInfoGUI.java           # Legacy item information
├── CountdownTimer.java         # Timer with action bar display
├── CollectionEvent.java        # Event tracking with missed item support
├── SurvivalItems.java          # Item whitelist management
└── modifiers/
    ├── GameModifier.java       # Abstract modifier base class
    ├── ModifierManager.java    # Lifecycle and event management
    └── DoubleTroubleModifier.java # Dual-target implementation
```

### 🔑 Key Features
- **Event-driven Architecture**: Clean separation of concerns
- **Modifier Framework**: Extensible system for custom game modes
- **GUI System**: Complete interface with inventory protection
- **Thread-safe Operations**: Stable multiplayer performance
- **Memory Efficient**: Automatic cleanup and resource management
- **Admin Tools**: Commands work seamlessly across all modes
- **Collection Tracking**: Comprehensive history with metadata
- **Sound Integration**: Consistent audio feedback

### 🔨 Building from Source
```bash
git clone https://github.com/Sh4dowking/ForceItem.git
cd ForceItem
mvn clean package
```

## 📸 Screenshots

*Coming soon - showcase the new GUI system and Double Trouble gameplay*

## 🔄 Roadmap

### 🎯 Upcoming Modifiers
- **Team Mode**: Collaborative target collection
- **Speed Run**: Progressively faster target assignments
- **Challenge Mode**: Difficult-only items with bonus points
- **Multiplier Mode**: Combo system with increasing rewards

### 🛠️ Technical Improvements
- **Configuration File**: Persistent game settings
- **Database Support**: Long-term statistics tracking
- **API Expansion**: Developer hooks for custom modifiers
- **Performance Optimization**: Large server support

## 📈 Compatibility

- **Minecraft Versions**: 1.18, 1.19, 1.20, 1.21+
- **Server Software**: Paper (recommended), Spigot, Purpur
- **Java Versions**: 17, 18, 19, 20, 21

## 🤝 Contributing

Contributions are welcome! The modifier system makes it easy to add new game modes.

### 🔧 Development Setup
1. Fork this repository
2. Set up Java 17+ and Maven environment
3. Check out the `GameModifier` abstract class for creating new modifiers
4. Test thoroughly with the GUI system
5. Submit a pull request

### 📝 Creating Custom Modifiers
```java
public class CustomModifier extends GameModifier {
    // Implement abstract methods for your game mode
    // See DoubleTroubleModifier for reference
}
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Made with ❤️ for the Minecraft community by Sh4dowking**

*ForceItem v2.0 - Now with complete GUI system and modifier architecture!*
