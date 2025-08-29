# ForceItem - Competitive Minecraft Minigame Plugin

A fast-paced, competitive minigame for Minecraft servers where players race to collect randomly assigned target items within a time limit.

## 🎮 Game Overview

ForceItem challenges players to quickly gather specific items from the survival world. Each player receives a unique target item and must collect it before time runs out. The twist? Players can use "jokers" to skip difficult targets, but strategic use is key to victory!

### 🆕 Latest Updates (v1.1)
- **Personal Backpack System**: Each player gets a 27-slot personal storage accessible during games
- **Enhanced Joker Rewards**: Using jokers now gives you the skipped item as a physical reward
- **Admin Skip Command**: Administrators can skip any player's target with `/skip <player>`
- **Smart Target Assignment**: No duplicate targets per player in a single game
- **Immersive Game Start**: Professional 3-2-1 countdown with sound effects
- **Mid-Game Join Support**: Players can join ongoing games seamlessly
- **Individual Boss Bars**: Personal display system for each player
- **Collection History**: Detailed tracking of when and how items were collected

## ✨ Features

### Core Gameplay
- **Timed Competition**: Customizable game duration (seconds)
- **Random Targets**: Each player gets different, non-repeating items to collect
- **Joker System**: Skip difficult targets and receive the item as reward (+1 point)
- **Personal Storage**: 27-slot backpack system for item management during games
- **Real-time Scoring**: Points awarded for successful collections
- **Smart Assignment**: Anti-duplicate system prevents repeated targets per game

### User Interface
- **Individual Boss Bars**: Personal display shows current target and score
- **Action Bar Timer**: Live countdown visible to all players
- **Interactive Leaderboard**: Post-game results with collection timestamps and methods
- **Item Information GUI**: Quick target item reference
- **Backpack System**: Right-click bundle item to access personal 27-slot storage

### Visual & Audio
- **Themed Color Scheme**: Clean white/aqua/gold design with enhanced contrast
- **Sound Effects**: Audio feedback for all actions including countdown
- **Professional Styling**: Polished user experience with 3-2-1 game start
- **Collection Feedback**: Visual indicators for normal vs joker collections

## 🚀 Installation

1. Download the latest `ForceItem.jar` from releases
2. Place in your server's `plugins/` directory
3. Start/restart your server
4. Plugin is ready to use!

**Requirements:**
- Minecraft Server 1.18+ (Paper/Spigot)
- Java 17+

## 📋 Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/startgame <seconds> <jokers>` | `forceitem.admin` | Start a new game |
| `/stopgame` | `forceitem.admin` | End current game |
| `/skip <playername>` | `forceitem.admin` | Skip a player's current target (admin only) |
| `/leaderboard` | `forceitem.use` | View game results |
| `/item` | `forceitem.use` | Show target item info |

### Command Examples
```
/startgame 1800 3    # 30-minute game, 3 jokers per player
/startgame 900 0    # 15-minute game, no jokers
/skip PlayerName    # Admin skips PlayerName's current target
/stopgame           # Emergency stop
```

## 🎯 How to Play

1. **Game Preparation**: Admin runs `/startgame <time> <jokers>` - world resets to day, players get full health
2. **Countdown**: Professional 3-2-1 countdown with sound effects builds anticipation
3. **Get Target**: Each player receives a unique random survival item to collect (no duplicates per game)
4. **Collect Items**: Find and pick up your target item from the world
5. **Use Storage**: Right-click your backpack (bundle item) to access 27-slot personal storage
6. **Use Jokers**: Right-click joker items to skip difficult targets (+1 point + receive the skipped item)
7. **Race Against Time**: Action bar shows remaining time for all players
8. **View Results**: Leaderboard GUI opens automatically showing detailed collection history
9. **Mid-Game Joins**: New players can join ongoing games and participate immediately

### Scoring System
- **+2 points** for collecting your target item
- **+1 point** for using a joker to skip (plus you receive the skipped item as reward)
- **New target** assigned after each collection/skip (guaranteed different from previous targets)

### Special Items
- **Jokers** (Red Barrier): Right-click to skip current target
- **Backpack** (Bundle): Right-click to open 27-slot personal storage
- Both items cannot be dropped and are automatically provided

## 🔧 Configuration

The plugin works out-of-the-box with sensible defaults. All items are sourced from a carefully curated survival-obtainable whitelist including:

- Basic blocks (stone, dirt, ores)
- Wood types and variants  
- Tools and weapons (including chainmail armor)
- Food items and farming materials
- Decorative blocks
- Redstone components
- Nether materials

*Creative-only and unobtainable items are excluded for fair gameplay. Problem items like grass blocks and tridents have been removed to ensure all targets are reasonably obtainable.*

### Game Rules & World Setup
- World time automatically resets to day (0 ticks) at game start
- Keep Inventory enabled during games to prevent item loss on death
- Immediate respawn enabled for seamless gameplay
- Player health, hunger, and status effects reset at game start

## 🛠️ Developer Information

### Project Structure
```
src/main/java/com/sh4dowking/forceitem/
├── Main.java              # Core plugin logic and commands
├── CountdownTimer.java    # Game timer with action bar display
├── LeaderboardGUI.java    # Interactive results interface
├── ItemInfoGUI.java       # Target item display
├── SurvivalItems.java     # Item whitelist management
└── PlayerResult.java      # Result data structure
```

### Key Features
- **Event-driven architecture** with proper cleanup and resource management
- **Individual player systems** for boss bars, backpacks, and target tracking
- **Thread-safe operations** for multiplayer stability with concurrent access
- **Memory efficient** with automatic resource management and cleanup on disconnect
- **Anti-duplicate target system** prevents repeated assignments within games
- **Smart inventory management** with overflow protection and item distribution
- **Extensible design** for easy customization and future enhancements
- **Comprehensive collection tracking** with timestamps and method recording

### Building from Source
```bash
git clone https://github.com/Sh4dowking/ForceItem.git
cd ForceItem
mvn clean package
```

## 🎨 Screenshots

*Coming soon - showcase the beautiful GUI interfaces and gameplay*

## 📈 Compatibility

- **Minecraft Versions**: 1.18, 1.19, 1.20, 1.21
- **Server Software**: Paper (recommended), Spigot, Purpur
- **Java Versions**: 17, 18, 19, 20, 21

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests, report bugs, or suggest features.

### Development Setup
1. Fork this repository
2. Set up your development environment with Java 17+ and Maven
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Made with ❤️ for the Minecraft community by Sh4dowking**
