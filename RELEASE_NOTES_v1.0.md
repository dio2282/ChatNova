# 🚀 ChatNova v1.0 - First Release

## 🎉 Welcome to ChatNova!

We're excited to announce the **first release** of ChatNova, a powerful and feature-rich global chat plugin designed specifically for Velocity proxy servers. This initial release brings a comprehensive set of features that make cross-server communication both elegant and manageable.

## ✨ What's New in v1.0

### 🎨 **Per-Server Formatting System**
- **Unique Visual Identity**: Each server in your network can have its own distinctive chat format
- **Hex Color Support**: Full support for hex colors using `&#rrggbb` format
- **Smart Server Detection**: Automatic detection of player's current server
- **Fallback Formatting**: Graceful fallback for unknown or misconfigured servers
- **LuckPerms Integration**: Seamless rank prefix integration from LuckPerms

**Example Formats:**
```yaml
format:
  lobby-1: "&b&lLOBBY-1 &f{rank}{player}&7: &b{message}"
  survival-1: "&a&lSURVIVAL-1 &f{rank}{player}&7: &a{message}"
  factions: "&4&lFACTIONS &f{rank}{player}&7: &4{message}"
```

### 🖥️ **Console Global Chat**
- **Administrator Broadcasting**: Send global messages directly from server console
- **Special Console Format**: Distinctive formatting for administrative announcements
- **Full Command Support**: All chat commands work from console interface
- **Professional Appearance**: Console messages appear with administrator branding

### 🛡️ **Advanced Content Filtering**
- **Message Length Control**: Configurable maximum message length limits
- **Banned Words System**: Comprehensive word filtering with persistent storage
- **URL/IP Detection**: Automatic detection and blocking of URLs and IP addresses
- **Smart Permission Bypass**: Granular permission system for bypassing filters

### 👤 **Comprehensive Player Management**
- **Blacklist System**: Permanent blacklist with persistent YAML storage
- **Individual Visibility Toggle**: Players can hide/show global chat per preference
- **LiteBans Integration**: Automatic mute status checking via LiteBans API
- **Flexible Cooldown System**: Rank-based cooldown timing (VIP, Premium, Default)

### 🔧 **Professional Administration Tools**
- **Real-time Config Reload**: Update settings without server restart via `/gcreload`
- **Global Chat Muting**: Emergency mute capability for all global chat
- **Blacklist Management**: Easy add/remove commands for player blacklisting
- **Permission-based Access**: Granular permission system for all features

### 📊 **Analytics & Performance Monitoring**
- **BStats Integration**: Anonymous usage statistics (Plugin ID: 25977)
- **Performance Tracking**: Monitor server count, player count, and feature usage
- **Privacy Compliant**: No personal data, IP addresses, or chat content collected
- **Opt-out Available**: Server administrators can disable analytics if desired

## 🏗️ **Technical Excellence**

### **Clean Architecture**
- **Modular Design**: Separated managers for configuration, messaging, and filtering
- **Abstract Command Base**: Consistent command handling with shared functionality
- **Dependency Injection**: Proper Velocity plugin integration patterns
- **Error Handling**: Comprehensive error handling and logging throughout

### **Performance Optimized**
- **Efficient Caching**: Smart caching of server formats and configurations
- **Async Operations**: Non-blocking operations for better server performance
- **Memory Management**: Efficient use of memory with proper cleanup
- **Thread Safety**: Concurrent-safe operations for multi-threaded environments

### **Integration Ready**
- **LuckPerms Support**: Automatic rank prefix detection and formatting
- **LiteBans Compatibility**: Seamless mute status checking integration
- **MiniMessage Support**: Modern text formatting with Adventure library
- **Legacy Support**: Backward compatibility with traditional color codes

## 📋 **Complete Command Reference**

### **Player Commands**
| Command | Description | Permission |
|---------|-------------|------------|
| `/gc <message>` | Send global chat message | `gc.use` |
| `/gctoggle` | Toggle global chat visibility | `gc.toggle` |

### **Administration Commands**
| Command | Description | Permission |
|---------|-------------|------------|
| `/gcmute` | Toggle global chat mute | `gc.mute` |
| `/gcreload` | Reload plugin configuration | `gc.reload` |
| `/gcblacklist <add/remove> <player>` | Manage blacklist | `gc.blacklist` |
| `/gclistblacklist` | List blacklisted players | `gc.blacklist` |

## 🔐 **Comprehensive Permission System**

### **Basic Permissions**
- `gc.use` - Access to global chat functionality
- `gc.toggle` - Toggle personal global chat visibility

### **Administrative Permissions**
- `gc.mute` - Global chat mute/unmute capability
- `gc.blacklist` - Player blacklist management
- `gc.reload` - Configuration reload access

### **Bypass Permissions**
- `gc.bypass.mute` - Bypass global chat mute restrictions
- `gc.bypass.cooldown` - Skip message cooldown timers
- `gc.bypass.url` - Send URLs in global chat messages

### **Cooldown Permissions**
- `gc.cooldown.vip` - Reduced cooldown for VIP players
- `gc.cooldown.premium` - Reduced cooldown for Premium players

## 📦 **Easy Installation & Configuration**

### **Quick Setup**
1. Download ChatNova v1.0 JAR file
2. Place in your Velocity `plugins` directory
3. Start/restart your Velocity proxy
4. Configure in `plugins/chatnova/config.yml`
5. Reload with `/gcreload` command

### **Smart Defaults**
- **Auto-generated Configuration**: Default config created automatically
- **Sensible Defaults**: Production-ready settings out of the box
- **Example Formats**: Pre-configured server formats for common setups
- **Documentation Included**: Comprehensive inline documentation

## 🌟 **Why Choose ChatNova?**

### **🚀 Performance First**
- Optimized for high-traffic networks
- Minimal resource footprint
- Efficient concurrent processing
- Smart caching mechanisms

### **🎨 Highly Customizable**
- Per-server visual identity
- Flexible permission system
- Configurable filtering rules
- Extensive format options

### **🛡️ Production Ready**
- Comprehensive error handling
- Professional logging system
- Graceful fallback mechanisms
- Battle-tested architecture

### **🤝 Community Friendly**
- Open source MIT License
- Well-documented codebase
- Extensible architecture
- Active development

## 📊 **Usage Analytics Dashboard**

Track your ChatNova usage and network statistics:
**[View BStats Dashboard](https://bstats.org/plugin/velocity/ChatNova/25977)**

## 🔗 **Requirements**

- **Velocity**: 3.4.0 or higher
- **Java**: 17 or higher
- **LuckPerms**: 5.4+ (optional but recommended)
- **LiteBans**: Any version (optional)

## 📝 **License Information**

ChatNova is released under the **MIT License**, making it free for both personal and commercial use. Feel free to fork, modify, and contribute to the project!

## 🙏 **Special Thanks**

- **Velocity Team** - For the excellent proxy platform
- **LuckPerms** - For the powerful permission system
- **Adventure Team** - For modern text formatting capabilities
- **BStats** - For anonymous usage analytics

## 🚀 **Get Started Today!**

Ready to enhance your network's communication? Download ChatNova v1.0 and experience the difference of professional global chat management!

### **Download Links**
- [GitHub Releases](https://github.com/username/chatnova/releases/tag/v1.0)
- [Direct Download](https://github.com/username/chatnova/releases/download/v1.0/chatnova-1.0.jar)

### **Documentation**
- [Installation Guide](README.md#installation)
- [Configuration Reference](README.md#configuration)
- [Command Reference](README.md#commands)
- [Permission Guide](README.md#permissions)

---

**ChatNova v1.0** - Revolutionizing cross-server communication, one message at a time! 🌟

*Released with ❤️ by Dev_Dio* 