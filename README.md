# ForceItem - Competitive Minecraft Minigame Plugin

A fast-paced, competitive minigame for Minecraft servers where players race to collect randomly assigned target items within a time limit.

## 🎮 Game Overview

ForceItem challenges players to quickly gather specific items from the survival world. Each player receives a unique target item and must collect it before time runs out. The twist? Players can use "jokers" to skip difficult targets, but strategic use is key to victory!

## ✨ Features

### Core Gameplay
- **Timed Competition**: Customizable game duration (seconds)
- **Random Targets**: Each player gets different items to collect
- **Joker System**: Skip difficult targets at the cost of strategy
- **Real-time Scoring**: Points awarded for successful collections

### User Interface
- **Boss Bar Display**: Shows current target and score
- **Action Bar Timer**: Live countdown visible to all players
- **Interactive Leaderboard**: Post-game results with detailed views
- **Item Information GUI**: Quick target item reference

### Visual & Audio
- **Themed Color Scheme**: Clean white/aqua/gold design
- **Sound Effects**: Audio feedback for all actions
- **Professional Styling**: Polished user experience

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
| `/leaderboard` | `forceitem.use` | View game results |
| `/item` | `forceitem.use` | Show target item info |

### Command Examples
```
/startgame 300 3    # 5-minute game, 3 jokers per player
/startgame 120 0    # 2-minute game, no jokers
/stopgame           # Emergency stop
```

## 🎯 How to Play

1. **Game Start**: Admin runs `/startgame <time> <jokers>`
2. **Get Target**: Each player receives a random survival item to collect
3. **Collect Items**: Find and pick up your target item from the world
4. **Use Jokers**: Right-click joker items to skip difficult targets (+1 point)
5. **Race Against Time**: Action bar shows remaining time
6. **View Results**: Leaderboard GUI opens automatically when game ends

### Scoring System
- **+2 points** for collecting your target item
- **+1 point** for using a joker to skip
- **New target** assigned after each collection/skip

## 🔧 Configuration

The plugin works out-of-the-box with sensible defaults. All items are sourced from a curated survival-obtainable whitelist including:

- Basic blocks (stone, dirt, ores)
- Wood types and variants  
- Tools and weapons
- Food items
- Decorative blocks
- Redstone components

*Creative-only and unobtainable items are excluded for fair gameplay.*

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
- **Event-driven architecture** with proper cleanup
- **Thread-safe operations** for multiplayer stability
- **Memory efficient** with automatic resource management
- **Extensible design** for easy customization

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

## 🙏 Acknowledgments

- Built with the Bukkit/Spigot API
- Inspired by competitive Minecraft minigames
- Thanks to the Minecraft server community for testing and feedback

## 📞 Support

Having issues? Here are your options:

1. Check the [Issues](https://github.com/Sh4dowking/ForceItem/issues) page
2. Join our Discord server: [Coming Soon]
3. Read the [Wiki](https://github.com/Sh4dowking/ForceItem/wiki) for detailed guides

---

**Made with ❤️ for the Minecraft community by Sh4dowking**
