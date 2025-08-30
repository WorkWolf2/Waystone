# WayStone

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Paper](https://img.shields.io/badge/Paper-1.21-blue.svg)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Gradle](https://img.shields.io/badge/Gradle-8.0+-green.svg)](https://gradle.org/)

A powerful and feature-rich Minecraft Paper plugin that allows players to create and manage teleportation waypoints throughout the world.

## 🚀 Features

- **Waystone Creation**: Players can place waystones to create teleportation points
- **Custom Crafting**: Configurable crafting recipe for waystones
- **Database Storage**: MySQL/MariaDB support for persistent waystone data
- **World Restrictions**: Configurable world blacklist for waystone placement
- **Player Limits**: Per-player waystone limit system
- **Interactive GUI**: User-friendly interface for waystone management
- **Animation System**: Smooth teleportation animations
- **Permission System**: Comprehensive permission-based access control
- **MiniMessage Support**: Rich text formatting for all messages

## 📋 Requirements

- **Java**: 21 or higher
- **Minecraft**: 1.21
- **Paper**: 1.21-R0.1-SNAPSHOT or higher
- **Database**: MySQL or MariaDB (optional, can be configured)

## 🛠️ Installation

### For Server Administrators

1. **Download** the latest release JAR file from the releases page
2. **Place** the JAR file in your server's `plugins` folder
3. **Start** your server to generate the default configuration
4. **Configure** the plugin by editing `plugins/WayStone/config.yml`
5. **Restart** your server

### For Developers

1. **Clone** the repository:
   ```bash
   git clone https://github.com/yourusername/WayStone.git
   cd WayStone
   ```

2. **Build** the project:
   ```bash
   ./gradlew build
   ```

3. **Find** the built JAR in `build/libs/WayStone-1.0.jar`

## ⚙️ Configuration

### Database Configuration

```yaml
database:
  host: 'localhost'
  port: 3306
  database: 'waystone_db'
  username: 'your_username'
  password: 'your_password'
```

### World Restrictions

```yaml
disabled-worlds:
  - world_nether
  - world_the_end
```

### Player Limits

```yaml
max-limit: 2  # Maximum waystones per player
```

### Crafting Recipe

```yaml
crafting:
  A: NOTHING
  B: NOTHING
  C: NOTHING
  D: NOTHING
  E: STICK
  F: NOTHING
  G: NOTHING
  H: NOTHING
  I: NOTHING
  shape:
    - "ABC"
    - "DEF"
    - "GHI"
```

## 🎮 Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/waystonesee <player>` | `waystone.admin.see` | View another player's waystones |
| `/waystonegive` | `waystone.admin.give` | Give waystones to players |

## 🔐 Permissions

- `waystone.admin.see` - View other players' waystones
- `waystone.admin.give` - Give waystones to players
- `waystone.use` - Use waystones (default: all players)

## 📁 Project Structure

```
src/main/java/com/minegolem/wayStone/
├── commands/          # Command implementations
├── craftings/         # Crafting recipe logic
├── data/             # Data models and structures
├── database/         # Database connection and operations
├── listeners/        # Event listeners
├── managers/         # Plugin managers
├── menu/             # GUI and menu systems
├── settings/         # Configuration management
├── utils/            # Utility classes
└── WayStone.java     # Main plugin class
```

## 🛠️ Development

### Building

```bash
# Build the project
./gradlew build

# Run the development server
./gradlew runServer

# Create shadow JAR (includes dependencies)
./gradlew shadowJar
```

### Dependencies

- **Paper API**: Minecraft server API
- **HikariCP**: Database connection pooling
- **MariaDB JDBC**: Database driver
- **Custom Block Data**: Persistent block data storage
- **Lombok**: Code generation utilities
- **JetBrains Annotations**: Code annotations

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**WorkWolf_2** - *Initial work*

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/WayStone/issues) page
2. Create a new issue with detailed information
3. Include your server version, plugin version, and error logs

## 🔄 Version History

- **v1.0** - Initial release with core waystone functionality

## ⚠️ Disclaimer

This plugin is provided "as is" without warranty of any kind. Use at your own risk.

---

**Made with ❤️ by Minegolem**
