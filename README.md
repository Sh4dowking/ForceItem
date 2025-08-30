# ForceItem - Advanced Competitive Minecraft Minigame Plugin

A feature-rich, competitive minigame for Minecraft servers where players race to collect randomly assigned target items within a time limit. Now featuring a complete GUI system, configurable game modes, a powerful modifier architecture, and an extensive perk system!

## 🎮 Game Overview

ForceItem challenges players to quickly gather specific items from the survival world. Each player receives unique target items and must collect them before time runs out. With the comprehensive GUI system, players can customize game settings, choose modifiers, enable perks, and enjoy enhanced gameplay experiences!

### 🆕 Latest Updates (v2.3.0)
- **⚡ Speed Perk**: Grants all players Speed II effect for enhanced movement throughout the game
- **👁️ Night Vision Perk**: Provides Night Vision effect to all players for perfect visibility in darkness  
- **🔥 Fast Smelting Perk**: 20x faster furnace smelting speed for all furnace types globally
- **⛏️ Vein Miner Perk**: Automatically mines connected blocks of the same type, including diagonal connections
- **🎯 Improved Balance**: All potion effect perks use timed duration matching game length (no infinite effects)
- **🔇 Hidden Effects**: Potion effects from perks don't show in the UI for cleaner gameplay experience
- **🚀 Performance**: Optimized vein mining with flood-fill algorithm and configurable limits (64 blocks max)

### 🆕 Previous Updates (v2.2.0)
- **🎒 Backpack Perk**: Personal 27-slot inventory for each player with custom GUI access
- **🎵 Enhanced Audio**: UI button click sounds for all GUI navigation and interactions
- **🔒 GUI Safety**: Only one player can open start game GUI at a time to prevent conflicts
- **💾 Save/Discard System**: GUI changes can be saved with lime dye or discarded with red dye
- **🛡️ Double Trouble Fixes**: Points properly preserved when players leave and rejoin games
- **🚫 Anti-Exploitation**: Prevents item duplication abuse through sophisticated player tracking
- **🔄 Smart Player Management**: Enhanced mid-game joining with original vs new player distinction

### 🆕 Previous Updates (v2.1)
- **🎁 Perk System**: Stackable gameplay enhancements that work with any modifier
- **⭐ Saturation Perk**: Infinite saturation effect throughout the entire game
- **⚒️ Tools Perk**: Unbreakable netherite tools with maximum efficiency enchantments
- **🍫 Recipe Unlocking**: All players receive every Minecraft crafting recipe at game start
- **🔄 Effect Management**: All potion effects cleared when starting new games
- **🎛️ Enhanced GUI**: Complete perk selection interface with toggle functionality

### 🆕 Previous Updates (v2.0)
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

### 🎁 Perk System
- **Stackable Enhancements**: Enable multiple perks simultaneously for combined effects
- **Universal Compatibility**: Works with all game modes and modifiers
- **Dynamic Duration**: Perk effects automatically match game duration settings
- **GUI Integration**: Easy toggle interface in StartGame menu
- **Lifecycle Management**: Automatic activation/deactivation with game events

#### Available Perks
- **⭐ Saturation Perk**: 
  - Permanent saturation effect for all players
  - No hunger loss throughout the game
  - Enhanced health regeneration capabilities
- **⚒️ Tools Perk**: 
  - Unbreakable Netherite Axe (Efficiency V, Sharpness V)
  - Unbreakable Netherite Pickaxe (Efficiency V)
  - Unbreakable Netherite Shovel (Efficiency V)
  - Perfect for resource gathering and combat
- **🎒 Backpack Perk**:
  - Personal 27-slot inventory for each player
  - Access via right-clicking the backpack item
  - Beautiful custom GUI with player name
  - Items persist throughout the entire game
  - Perfect for storing extra resources and organization
- **⚡ Speed Perk**:
  - Speed II effect for all players
  - Enhanced movement speed for faster gameplay
  - Effect duration matches game length perfectly
- **👁️ Night Vision Perk**:
  - Night Vision effect for all players
  - Perfect visibility in caves and darkness
  - Great for underground resource gathering
- **🔥 Fast Smelting Perk**:
  - 20x faster smelting speed for all furnace types
  - Works with furnaces, blast furnaces, and smokers
  - Global effect that benefits all players
- **⛏️ Vein Miner Perk**:
  - Automatically mines connected blocks of the same type
  - Works on all ore types and log types
  - Detects diagonal connections for maximum efficiency
  - Limited to 64 blocks per vein to prevent server lag
  - Uses advanced flood-fill algorithm for performance

### 🖱️ GUI System
- **StartGame Interface**: 54-slot double chest configuration with exclusive access control
- **Safety Feature**: Only one player can open the configuration GUI at a time
- **Save/Discard Controls**: Lime dye saves changes, red dye discards and reverts to backup
- **Time Settings**: 1 minute to 24 hours with increment/decrement controls
- **Joker Configuration**: 0-64 jokers with visual feedback
- **Modifier Selection**: Choose between Standard and Double Trouble modes
- **Perk Selection**: Toggle multiple perks including new Backpack perk
- **Enhanced Audio**: UI button click sounds for all navigation buttons
- **Visual Feedback**: Item highlighting, button states, and comprehensive sound effects
- **Custom Item GUIs**: Beautiful displays for both standard and modifier targets

### 🎮 Core Gameplay
- **Timed Competition**: Highly configurable game duration
- **Smart Target System**: Anti-duplicate assignments with modifier support
- **Enhanced Joker System**: Strategic item skipping with proper rewards
- **Personal Storage**: 27-slot backpack system during games
- **Real-time Display**: Individual boss bars with mode-specific information
- **Collection Tracking**: Comprehensive history with timestamps and methods
- **Recipe Knowledge**: All Minecraft recipes unlocked at game start
- **Clean Slate**: All potion effects cleared when games begin

### 🔥 Modifier & Perk Architecture
- **Scalable Framework**: Easy addition of new game modes and enhancements
- **GameModifier Base**: Abstract class for consistent modifier behavior
- **ModifierManager**: Centralized lifecycle and event management
- **DoubleTroubleModifier**: First concrete implementation with dual targets
- **GamePerk System**: Abstract perk architecture for stackable enhancements
- **PerkManager**: Handles multiple active perks with lifecycle integration
- **Admin Support**: All commands work seamlessly with modifiers and perks

### 🎨 User Experience
- **Themed Design**: Consistent white/aqua/gold color scheme throughout all interfaces
- **Enhanced Audio**: UI button click sounds for GUI navigation and leaderboard interactions
- **Sound Feedback**: Villager NO for cancellations, experience orb for confirmations
- **Inventory Protection**: Prevents item extraction from all custom GUIs
- **Exclusive Access**: Start game GUI safety prevents admin conflicts
- **Smart Controls**: Save/discard system with visual feedback and sound cues
- **Error Handling**: Comprehensive validation and user feedback
- **Accessibility**: Clear visual cues and intuitive navigation with audio support

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
1. **Configure**: Use `/startgame` GUI to set time, jokers, select Standard mode, and enable perks
2. **Perk Benefits**: Enjoy infinite saturation, powerful tools, and full recipe knowledge
3. **Countdown**: Professional 3-2-1 countdown builds anticipation  
4. **Collect**: Find your single target item (+1 point)
5. **Use Jokers**: Skip difficult targets and receive the item (+1 point)
6. **View Info**: Use `/item` for beautiful target display with lore

### ⚡ Double Trouble Mode
1. **Select**: Choose "Double Trouble" modifier and enable perks in the `/startgame` GUI
2. **Enhanced Gameplay**: Combine dual targets with perk benefits for ultimate challenge
3. **Dual Targets**: Receive TWO target items simultaneously
4. **Strategic Choice**: Collect **either target** for +1 point
5. **New Targets**: Get fresh dual targets after each collection
6. **Smart Jokers**: Get ONE random item from your two targets
7. **Enhanced Display**: Boss bar shows both targets, `/item` shows both with lore

### 🎁 Using Perks
1. **Access Perks**: Click the diamond "Perks" button in `/startgame` GUI
2. **Toggle Perks**: Click any perk to enable/disable (multiple perks allowed)
3. **Visual Feedback**: Green checkmark shows active perks, red dye to cancel
4. **Save Changes**: Use lime dye to save perk selections, red dye to discard
5. **Audio Feedback**: UI button clicks for all perk interactions
6. **Game Integration**: Perks automatically activate when game starts
7. **Smart Duration**: All perks last exactly as long as your game duration
8. **Backpack Access**: Right-click the backpack item to open your personal inventory

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
├── modifiers/
│   ├── GameModifier.java       # Abstract modifier base class
│   ├── ModifierManager.java    # Lifecycle and event management
│   └── DoubleTroubleModifier.java # Dual-target implementation
└── perks/
    ├── GamePerk.java           # Abstract perk base class
    ├── PerkManager.java        # Multi-perk lifecycle management
    ├── SaturationPerk.java     # Infinite saturation implementation
    ├── ToolsPerk.java          # Netherite tools implementation
    └── BackpackPerk.java       # Personal inventory implementation
```

### 🔨 Building from Source
```bash
git clone https://github.com/Sh4dowking/ForceItem.git
cd ForceItem
mvn clean package
```

## 📸 Screenshots

*Coming soon - showcase the new GUI system and gameplay*

## 📈 Compatibility

- **Minecraft Versions**: 1.18, 1.19, 1.20, 1.21+
- **Server Software**: Paper (recommended), Spigot, Purpur
- **Java Versions**: 17, 18, 19, 20, 21

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Made with ❤️ for the Minecraft community by Sh4dowking**

*ForceItem v2.2.0 - Now with comprehensive perk system, enhanced GUI safety, and superior user experience!*
