package me.dev_dio.chatnova.manager;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import me.dev_dio.chatnova.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

public class MessageManager {
    private final ConfigManager configManager;
    private final LuckPerms luckPerms;
    private final ProxyServer server;
    private final Logger logger;
    private final MiniMessage miniMessage;
    
    public MessageManager(ConfigManager configManager, LuckPerms luckPerms, ProxyServer server, Logger logger) {
        this.configManager = configManager;
        this.luckPerms = luckPerms;
        this.server = server;
        this.logger = logger;
        this.miniMessage = MiniMessage.miniMessage();
    }
    
    /**
     * Send a formatted message to a command source
     */
    public void sendMessage(CommandSource source, String message) {
        source.sendMessage(formatText(message));
    }
    
    /**
     * Send a formatted message from config to a command source
     */
    public void sendConfigMessage(CommandSource source, String configPath, String defaultMessage) {
        String message = configManager.getString(configPath, defaultMessage);
        sendMessage(source, message);
    }
    
    /**
     * Send a global chat message to all eligible players
     */
    public void sendGlobalMessage(Player player, String message, Set<UUID> hiddenPlayers) {
        String serverName = player.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("Unknown");
        
        String rankPrefix = getRankPrefixFromLuckPerms(player.getUniqueId());
        String formatTemplate = getServerFormat(serverName);
        
        // Replace placeholders one by one to avoid conflicts
        String formattedMessage = formatTemplate;
        formattedMessage = formattedMessage.replace("{server}", serverName);
        formattedMessage = formattedMessage.replace("{rank}", rankPrefix != null ? rankPrefix : "");
        formattedMessage = formattedMessage.replace("{prefix}", rankPrefix != null ? rankPrefix : "");
        formattedMessage = formattedMessage.replace("{player}", player.getUsername());
        formattedMessage = formattedMessage.replace("{message}", message);
        
        // Also replace any Velocity internal placeholders that might have leaked through
        formattedMessage = formattedMessage.replace("{MINECRAFT_USERNAME}", player.getUsername());
        
        Component finalMessage = formatText(formattedMessage);
        
        // Send to all players except those who have hidden global chat
        server.getAllPlayers().stream()
                .filter(p -> !hiddenPlayers.contains(p.getUniqueId()))
                .forEach(p -> p.sendMessage(finalMessage));
        
        // Log to console
        logger.info("[Global Chat] [{}] {}: {}", serverName, player.getUsername(), stripFormatting(message));
    }
    
    /**
     * Send a global chat message from console
     */
    public void sendConsoleGlobalMessage(String message, Set<UUID> hiddenPlayers) {
        String formatTemplate = configManager.getString(Constants.Format.CONSOLE, Constants.Defaults.CONSOLE_FORMAT_MSG);
        
        String formattedMessage = formatTemplate.replace("{message}", message);
        Component finalMessage = formatText(formattedMessage);
        
        // Send to all players except those who have hidden global chat
        server.getAllPlayers().stream()
                .filter(p -> !hiddenPlayers.contains(p.getUniqueId()))
                .forEach(p -> p.sendMessage(finalMessage));
        
        // Log to console
        logger.info("[Global Chat] Administrator: {}", stripFormatting(message));
    }
    
    /**
     * Get the appropriate format for a specific server
     */
    private String getServerFormat(String serverName) {
        String serverFormatPath = Constants.Format.SERVER_PREFIX + serverName.toLowerCase();
        String serverFormat = configManager.getString(serverFormatPath, null);
        
        if (serverFormat != null) {
            return serverFormat;
        }
        
        // Fallback to default player format
        return configManager.getString(Constants.Format.PLAYER, Constants.Defaults.FORMAT_MSG);
    }
    
    /**
     * Format text to Component, handling both legacy and MiniMessage formats
     */
    public Component formatText(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        
        if (isUsingMiniMessage()) {
            return miniMessage.deserialize(convertLegacyToMiniMessage(text));
        } else {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        }
    }
    
    /**
     * Strip all formatting codes from text for console output
     */
    public String stripFormatting(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replaceAll("(?i)&[0-9a-fk-or]", "");
    }
    
    /**
     * Convert legacy color codes to MiniMessage format
     */
    public String convertLegacyToMiniMessage(String legacy) {
        if (legacy == null || legacy.isEmpty()) {
            return "";
        }
        
        // Convert legacy color codes to MiniMessage format
        String result = legacy
            .replace("&0", "<black>")
            .replace("&1", "<dark_blue>")
            .replace("&2", "<dark_green>")
            .replace("&3", "<dark_aqua>")
            .replace("&4", "<dark_red>")
            .replace("&5", "<dark_purple>")
            .replace("&6", "<gold>")
            .replace("&7", "<gray>")
            .replace("&8", "<dark_gray>")
            .replace("&9", "<blue>")
            .replace("&a", "<green>")
            .replace("&b", "<aqua>")
            .replace("&c", "<red>")
            .replace("&d", "<light_purple>")
            .replace("&e", "<yellow>")
            .replace("&f", "<white>")
            .replace("&k", "<obfuscated>")
            .replace("&l", "<bold>")
            .replace("&m", "<strikethrough>")
            .replace("&n", "<underlined>")
            .replace("&o", "<italic>")
            .replace("&r", "<reset>");
            
        return result;
    }
    
    /**
     * Get formatted message with placeholder replacement
     */
    public String getFormattedMessage(String configPath, String defaultMessage, String placeholder, String replacement) {
        String message = configManager.getString(configPath, defaultMessage);
        return message.replace(placeholder, replacement);
    }
    
    /**
     * Get formatted message with multiple placeholder replacements
     */
    public String getFormattedMessage(String configPath, String defaultMessage, String... replacements) {
        String message = configManager.getString(configPath, defaultMessage);
        
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        
        return message;
    }
    
    /**
     * Check if MiniMessage format is enabled
     */
    private boolean isUsingMiniMessage() {
        return configManager.getBoolean(Constants.Config.USE_MINI_MESSAGE, Constants.Defaults.USE_MINI_MESSAGE);
    }
    
    /**
     * Get rank prefix from LuckPerms
     */
    private String getRankPrefixFromLuckPerms(UUID uuid) {
        try {
            User user = luckPerms.getUserManager().getUser(uuid);
            
            if (user != null) {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                return prefix != null ? prefix : "";
            }
        } catch (Exception e) {
            logger.error("Error getting prefix for player " + uuid, e);
        }
        return "";
    }
} 