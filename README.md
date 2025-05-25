# ChatNova

[![Velocity](https://img.shields.io/badge/Platform-Velocity-blue.svg)](https://velocitypowered.com/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#license)
[![BStats](https://img.shields.io/badge/BStats-25977-brightgreen.svg)](https://bstats.org/plugin/velocity/ChatNova/25977)

A powerful and feature-rich global chat plugin for Velocity proxy servers with per-server formatting, advanced filtering, and comprehensive administration tools.

## ⚠️ Important Notice

**This plugin is provided "as-is" under the MIT License. While the source code is available, support may be limited. Please refer to the documentation and issues section for help.**

## ✨ Features

### 🎨 **Per-Server Formatting**
- Unique chat formats for each server in your network
- Hex color support (`&#rrggbb`)
- LuckPerms integration for rank prefixes
- Automatic server detection and format application
- Fallback formatting for unknown servers

### 🖥️ **Console Integration**
- Send global messages directly from console
- Special administrator format for console messages
- Full command support from server console

### 🛡️ **Advanced Filtering**
- Configurable message length limits
- Banned words filtering system
- URL/IP address detection and blocking
- Comprehensive permission bypass system

### 👤 **Player Management**
- Blacklist system with persistent storage
- Individual player visibility toggle
- LiteBans integration for mute checking
- Flexible cooldown system with rank-based timing

### 🔧 **Administration Tools**
- Real-time configuration reloading
- Global chat muting capability
- Blacklist management commands
- Comprehensive permission system

### 📊 **Analytics & Monitoring**
- BStats integration (Plugin ID: 25977)
- Anonymous usage statistics
- Performance monitoring
- Server count and player tracking

## 📋 Requirements

- **Velocity**: 3.4.0 or higher
- **Java**: 17 or higher
- **LuckPerms**: 5.4 or higher (optional)
- **LiteBans**: Any version (optional)

## 🚀 Installation

1. Download the ChatNova JAR file
2. Place it in your Velocity `plugins` folder
3. Start/restart your Velocity proxy
4. Configure the plugin in `plugins/chatnova/config.yml`
5. Reload with `/gcreload` command

## ⚙️ Configuration

### Basic Configuration

```yaml
# Server-specific chat formats
format:
  # Default format for unknown servers
  player: "&f{rank}{player}&7: &f{message}"
  
  # Console format
  console: "&c&lCONSOLE&7: &c{message}"
  
  # Server-specific formats
  lobby-1: "&b&lLOBBY-1 &f{rank}{player}&7: &b{message}"
  survival-1: "&a&lSURVIVAL-1 &f{rank}{player}&7: &a{message}"
  factions: "&4&lFACTIONS &f{rank}{player}&7: &4{message}"

# Cooldown settings (in seconds)
cooldown:
  default: 60  # Default cooldown for regular players
  vip: 30     # Reduced cooldown for VIP players (gc.cooldown.vip)
  premium: 15 # Reduced cooldown for Premium players (gc.cooldown.premium)

# Chat settings
chat:
  max-length: 100

# Enable MiniMessage format
use-minimessage: true

# Messages configuration
messages:
  usage: "&eUsage: /gc <message>"
  no-permission: "&cYou don't have permission to use this command!"
  # ... more messages
```

### Available Placeholders

| Placeholder | Description |
|-------------|-------------|
| `{server}` | Current server name |
| `{rank}` | LuckPerms rank prefix |
| `{prefix}` | Alias for `{rank}` |
| `{player}` | Player username |
| `{message}` | Chat message content |

### Server-Specific Formats

Add custom formats for each server by using the server name as the key:

```yaml
format:
  hub: "&6&lHUB &f{rank}{player}&7: &6{message}"
  survival: "&2&lSURVIVAL &f{rank}{player}&7: &2{message}"
  creative: "&d&lCREATIVE &f{rank}{player}&7: &d{message}"
```

## 🎮 Commands

### Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gc <message>` | Send a global chat message | `gc.use` |
| `/gctoggle` | Toggle global chat visibility | `gc.toggle` |

### Administration Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gcmute` | Toggle global chat mute | `gc.mute` |
| `/gcreload` | Reload plugin configuration | `gc.reload` |
| `/gcblacklist <add/remove> <player>` | Manage blacklisted players | `gc.blacklist` |
| `/gclistblacklist` | List all blacklisted players | `gc.blacklist` |

### Console Commands

All commands can be executed from the server console. The `/gc` command from console will broadcast as administrator.

## 🔐 Permissions

### Basic Permissions
- `gc.use` - Use global chat command
- `gc.toggle` - Toggle global chat visibility

### Administration Permissions
- `gc.mute` - Mute/unmute global chat
- `gc.blacklist` - Manage player blacklist
- `gc.reload` - Reload plugin configuration

### Bypass Permissions
- `gc.bypass.mute` - Bypass global chat mute
- `gc.bypass.cooldown` - Bypass message cooldown
- `gc.bypass.url` - Send URLs in global chat

### Cooldown Permissions
- `gc.cooldown.vip` - Use VIP cooldown timing
- `gc.cooldown.premium` - Use premium cooldown timing

## 🏗️ Building

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build Steps
```bash
git clone <repository-url>
cd chatnova
mvn clean package
```

The compiled JAR will be available in the `target` directory.

## 🔌 Dependencies

### Required Dependencies
- **Velocity API** (3.4.0-SNAPSHOT) - Core platform
- **SnakeYAML** - Configuration management

### Optional Dependencies
- **LuckPerms API** (5.4) - Rank prefix integration
- **LiteBans API** (0.5.0) - Mute status checking
- **Adventure MiniMessage** (4.15.0) - Advanced text formatting

### Integrated Libraries
- **BStats** (3.1.0) - Usage analytics (manually integrated)

## 📊 Analytics

This plugin uses bStats to collect anonymous usage statistics. This helps understand how the plugin is used and can guide future development.

**Collected Data:**
- Server count and player count
- Plugin version usage
- MiniMessage usage statistics
- Blacklisted player count

**Privacy:** All data is anonymous and aggregated. No personal information, IP addresses, or chat content is collected.

**Opt-out:** Server administrators can disable analytics in the Velocity configuration.

**Dashboard:** [View statistics](https://bstats.org/plugin/velocity/ChatNova/25977)

## 🏗️ Architecture

### Package Structure
```
me.dev_dio.chatnova/
├── Constants.java           # Configuration constants
├── ChatNova.java           # Main plugin class
├── Metrics.java            # BStats analytics
├── commands/               # Command implementations
│   ├── BaseCommand.java
│   ├── GcBlacklistCommand.java
│   ├── GcListBlacklistCommand.java
│   ├── GcMuteCommand.java
│   ├── GcReloadCommand.java
│   └── GcToggleCommand.java
├── filter/                 # Message filtering
│   └── ChatFilter.java
├── manager/                # Core managers
│   ├── ConfigManager.java
│   └── MessageManager.java
└── util/                   # Utility classes
    └── FormatUtil.java
```

### Key Components

- **ConfigManager**: Handles configuration loading and management
- **MessageManager**: Manages message formatting and distribution
- **ChatFilter**: Implements content filtering and validation
- **BaseCommand**: Abstract base for all commands with common functionality

## 🐛 Known Issues

- None currently known

## 📝 License

**MIT License**

Copyright (c) 2024 Dev_Dio

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 👨‍💻 Author

**Dev_Dio**
- Original plugin developer
- Feel free to fork, modify, and contribute
- Issues and pull requests are welcome

## 🔄 Version History

### Version 1.0
- Initial release
- Per-server formatting system
- Console global chat support
- Advanced filtering capabilities
- LuckPerms and LiteBans integration
- BStats analytics integration
- Comprehensive administration tools

---

**Note:** This plugin is open source under MIT License. Feel free to use, modify, and distribute according to the license terms. Contributions, bug reports, and feature requests are welcome through GitHub issues and pull requests. 