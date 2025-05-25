package me.dev_dio.chatnova;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe; // Import LiteBans API
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import litebans.api.Database;
import me.dev_dio.chatnova.commands.GcBlacklistCommand;
import me.dev_dio.chatnova.commands.GcListBlacklistCommand;
import me.dev_dio.chatnova.commands.GcMuteCommand;
import me.dev_dio.chatnova.commands.GcReloadCommand;
import me.dev_dio.chatnova.commands.GcToggleCommand;
import me.dev_dio.chatnova.filter.ChatFilter;
import me.dev_dio.chatnova.manager.ConfigManager;
import me.dev_dio.chatnova.manager.MessageManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

@Plugin(id = "chatnova", name = "chatnova", version = "1.0", authors = {"Dev_Dio"})
public class ChatNova {
    @Inject
    protected Logger logger;

    @Inject
    private ProxyServer server;

    @Inject
    @DataDirectory
    private Path dataDirectory;
    
    @Inject
    private Metrics.Factory metricsFactory;

    // Core managers
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ChatFilter chatFilter;
    private Metrics metrics;
    
    // Plugin state
    private Set<String> blacklistedPlayers;
    private LuckPerms luckPerms;
    private Map<UUID, Long> cooldowns;
    private boolean globalChatMuted;
    private Set<UUID> hiddenGlobalChat;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        initializeManagers();
        initializeData();
        loadAllData();
        registerCommands();
        initializeBStats();
        
        logger.info(Constants.PLUGIN_NAME + " has been initialized!");
    }
    
    private void initializeManagers() {
        // Initialize LuckPerms API
        luckPerms = LuckPermsProvider.get();
        
        // Initialize managers
        configManager = new ConfigManager(dataDirectory, logger);
        messageManager = new MessageManager(configManager, luckPerms, server, logger);
        
        // Load configuration first
        configManager.loadConfig();
        
        // Initialize chat filter with configured max length
        int maxLength = configManager.getInt(Constants.Config.CHAT_MAX_LENGTH, Constants.Defaults.MAX_MESSAGE_LENGTH);
        chatFilter = new ChatFilter(maxLength);
    }
    
    private void initializeData() {
        blacklistedPlayers = new HashSet<>();
        cooldowns = new ConcurrentHashMap<>();
        globalChatMuted = false;
        hiddenGlobalChat = new HashSet<>();
    }
    
    private void loadAllData() {
        loadBlacklist();
        loadBannedWords();
        loadHiddenGlobalChat();
    }
    
    private void registerCommands() {
        CommandManager commandManager = server.getCommandManager();
        
        // Register main GC command
        CommandMeta gcMeta = commandManager.metaBuilder("gc")
                .aliases("globalchat")
                .plugin(this)
                .build();
        commandManager.register(gcMeta, new GlobalChatCommand());
        
        // Register admin commands
        commandManager.register("gcmute", new GcMuteCommand(this));
        commandManager.register("gcblacklist", new GcBlacklistCommand(this));
        commandManager.register("gcreload", new GcReloadCommand(this));
        commandManager.register("gclistblacklist", new GcListBlacklistCommand(this));
        commandManager.register("gctoggle", new GcToggleCommand(this));
    }
    
    private void initializeBStats() {
        // BStats Plugin ID: 25977
        int pluginId = 25977;
        metrics = metricsFactory.make(this, pluginId);
        
        // Add custom charts
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> server.getAllServers().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("online_players", () -> server.getPlayerCount()));
        
        // Track if MiniMessage is enabled
        metrics.addCustomChart(new Metrics.SimplePie("minimessage_enabled", () -> 
            configManager.getBoolean(Constants.Config.USE_MINI_MESSAGE, Constants.Defaults.USE_MINI_MESSAGE) ? "Enabled" : "Disabled"
        ));
        
        // Track blacklisted players count
        metrics.addCustomChart(new Metrics.SingleLineChart("blacklisted_players", () -> blacklistedPlayers.size()));
        
        logger.info("BStats metrics initialized successfully!");
    }

    // Getters for managers
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public ChatFilter getChatFilter() {
        return chatFilter;
    }
    
    public ProxyServer getServer() {
        return server;
    }

    // Player management methods
    public void blacklistPlayer(String playerName) {
        blacklistedPlayers.add(playerName.toLowerCase());
        saveBlacklist();
    }

    public void unblacklistPlayer(String playerName) {
        blacklistedPlayers.remove(playerName.toLowerCase());
        saveBlacklist();
    }

    public boolean isBlacklisted(String playerName) {
        return blacklistedPlayers.contains(playerName.toLowerCase());
    }

    public Set<String> getBlacklistedPlayers() {
        return new HashSet<>(blacklistedPlayers);
    }

    // Global chat state management
    public boolean isGlobalChatMuted() {
        return globalChatMuted;
    }

    public void toggleGlobalChatMute() {
        globalChatMuted = !globalChatMuted;
    }

    public boolean isGlobalChatHidden(UUID playerUuid) {
        return hiddenGlobalChat.contains(playerUuid);
    }

    public boolean toggleGlobalChatVisibility(UUID playerUuid) {
        if (hiddenGlobalChat.contains(playerUuid)) {
            hiddenGlobalChat.remove(playerUuid);
            saveHiddenGlobalChat();
            return false; // Now showing global chat
        } else {
            hiddenGlobalChat.add(playerUuid);
            saveHiddenGlobalChat();
            return true; // Now hiding global chat
        }
    }

    // Cooldown management
    public boolean hasCooldown(UUID playerUuid) {
        if (!cooldowns.containsKey(playerUuid)) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.get(playerUuid);
        long cooldownTime = getCooldownTime(playerUuid) * 1000L;
        
        return (currentTime - lastUsed) < cooldownTime;
    }
    
    public long getRemainingCooldown(UUID playerUuid) {
        if (!cooldowns.containsKey(playerUuid)) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.get(playerUuid);
        long cooldownTime = getCooldownTime(playerUuid) * 1000L;
        long remaining = cooldownTime - (currentTime - lastUsed);
        
        return Math.max(0, remaining / 1000);
    }
    
    public void setCooldown(UUID playerUuid) {
        cooldowns.put(playerUuid, System.currentTimeMillis());
    }

    private int getCooldownTime(UUID playerUuid) {
        Player player = server.getPlayer(playerUuid).orElse(null);
        if (player == null) {
            return configManager.getInt(Constants.Config.COOLDOWN_DEFAULT, Constants.Defaults.COOLDOWN_DEFAULT);
        }
        
        if (player.hasPermission(Constants.Permissions.GC_COOLDOWN_VIP)) {
            return configManager.getInt(Constants.Config.COOLDOWN_VIP, Constants.Defaults.COOLDOWN_VIP);
        } else if (player.hasPermission(Constants.Permissions.GC_COOLDOWN_PREMIUM)) {
            return configManager.getInt(Constants.Config.COOLDOWN_PREMIUM, Constants.Defaults.COOLDOWN_PREMIUM);
        } else {
            return configManager.getInt(Constants.Config.COOLDOWN_DEFAULT, Constants.Defaults.COOLDOWN_DEFAULT);
        }
    }

    // Utility methods
    public boolean isPlayerMuted(UUID uuid) {
        try {
            return Database.get().isPlayerMuted(uuid, null);
        } catch (Exception e) {
            logger.warn("Could not check mute status for player {}: {}", uuid, e.getMessage());
            return false;
        }
    }

    public void reloadConfig() {
        configManager.reloadConfig();
        
        // Reload chat filter with new max length
        int maxLength = configManager.getInt(Constants.Config.CHAT_MAX_LENGTH, Constants.Defaults.MAX_MESSAGE_LENGTH);
        chatFilter = new ChatFilter(maxLength);
        loadBannedWords();
        
        loadBlacklist();
        loadHiddenGlobalChat();
    }

    // Data loading methods
    private void loadBlacklist() {
        try {
            Path blacklistPath = dataDirectory.resolve(Constants.BLACKLIST_FILE);
            createFileIfNotExists(blacklistPath, Constants.YamlKeys.BLACKLISTED_PLAYERS + ": []");
            
            Yaml yaml = new Yaml();
            try (Reader reader = Files.newBufferedReader(blacklistPath)) {
                Map<String, Object> data = yaml.load(reader);
                
                if (data != null && data.containsKey(Constants.YamlKeys.BLACKLISTED_PLAYERS)) {
                    @SuppressWarnings("unchecked")
                    List<String> loadedBlacklist = (List<String>) data.get(Constants.YamlKeys.BLACKLISTED_PLAYERS);
                    
                    blacklistedPlayers = new HashSet<>(loadedBlacklist.stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet()));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load blacklist", e);
        }
    }

    private void loadHiddenGlobalChat() {
        try {
            Path hiddenGcPath = dataDirectory.resolve(Constants.HIDDEN_GC_FILE);
            createFileIfNotExists(hiddenGcPath, Constants.YamlKeys.HIDDEN_PLAYERS + ": []");
            
            Yaml yaml = new Yaml();
            try (Reader reader = Files.newBufferedReader(hiddenGcPath)) {
                Map<String, Object> data = yaml.load(reader);
                
                if (data != null && data.containsKey(Constants.YamlKeys.HIDDEN_PLAYERS)) {
                    @SuppressWarnings("unchecked")
                    List<String> hiddenPlayerStrings = (List<String>) data.get(Constants.YamlKeys.HIDDEN_PLAYERS);
                    
                    hiddenGlobalChat = new HashSet<>(hiddenPlayerStrings.stream()
                            .map(UUID::fromString)
                            .collect(Collectors.toSet()));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load hidden global chat data", e);
        }
    }

    private void loadBannedWords() {
        try {
            Path bannedWordsPath = dataDirectory.resolve(Constants.BANNED_WORDS_FILE);
            createFileIfNotExists(bannedWordsPath, Constants.YamlKeys.BANNED_WORDS + ": []");
            
            Yaml yaml = new Yaml();
            try (Reader reader = Files.newBufferedReader(bannedWordsPath)) {
                Map<String, Object> data = yaml.load(reader);
                if (data != null && data.containsKey(Constants.YamlKeys.BANNED_WORDS)) {
                    @SuppressWarnings("unchecked")
                    List<String> words = (List<String>) data.get(Constants.YamlKeys.BANNED_WORDS);
                    words.forEach(chatFilter::addBannedWord);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load banned words", e);
        }
    }

    // Data saving methods
    private void saveBlacklist() {
        try {
            Path blacklistPath = dataDirectory.resolve(Constants.BLACKLIST_FILE);
            Map<String, Object> data = new HashMap<>();
            data.put(Constants.YamlKeys.BLACKLISTED_PLAYERS, new ArrayList<>(blacklistedPlayers));
            
            saveYamlFile(blacklistPath, data);
        } catch (IOException e) {
            logger.error("Failed to save blacklist", e);
        }
    }

    private void saveHiddenGlobalChat() {
        try {
            Path hiddenGcPath = dataDirectory.resolve(Constants.HIDDEN_GC_FILE);
            Map<String, Object> data = new HashMap<>();
            
            List<String> hiddenPlayerStrings = hiddenGlobalChat.stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            data.put(Constants.YamlKeys.HIDDEN_PLAYERS, hiddenPlayerStrings);
            
            saveYamlFile(hiddenGcPath, data);
        } catch (IOException e) {
            logger.error("Failed to save hidden global chat data", e);
        }
    }

    // Utility methods for file operations
    private void createFileIfNotExists(Path filePath, String defaultContent) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createDirectories(dataDirectory);
            try (Writer writer = Files.newBufferedWriter(filePath)) {
                writer.write(defaultContent);
            }
        }
    }

    private void saveYamlFile(Path filePath, Map<String, Object> data) throws IOException {
        Yaml yaml = new Yaml();
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            yaml.dump(data, writer);
        }
    }

    // Inner command classes
    private class GlobalChatCommand implements SimpleCommand {
        @Override
        public void execute(SimpleCommand.Invocation invocation) {
            String[] args = invocation.arguments();
            
            // Handle console usage
            if (!(invocation.source() instanceof Player)) {
                if (args.length == 0) {
                    invocation.source().sendMessage(messageManager.formatText("&cUsage: /gc <message>"));
                    return;
                }
                
                String message = String.join(" ", args);
                messageManager.sendConsoleGlobalMessage(message, hiddenGlobalChat);
                return;
            }
            
            Player player = (Player) invocation.source();
            
            // Permission check
            if (!player.hasPermission(Constants.Permissions.GC_USE)) {
                messageManager.sendConfigMessage(player, Constants.Messages.NO_PERMISSION, Constants.Defaults.NO_PERMISSION_MSG);
                return;
            }

            // Blacklist check
            if (isBlacklisted(player.getUsername().toLowerCase())) {
                messageManager.sendConfigMessage(player, Constants.Messages.BLACKLISTED, Constants.Defaults.BLACKLISTED_MSG);
                return;
            }

            // Mute check
            if (isPlayerMuted(player.getUniqueId())) {
                messageManager.sendConfigMessage(player, Constants.Messages.MUTED, Constants.Defaults.MUTED_MSG);
                return;
            }

            // Global mute check
            if (globalChatMuted && !player.hasPermission(Constants.Permissions.GC_BYPASS_MUTE)) {
                messageManager.sendConfigMessage(player, Constants.Messages.GLOBAL_MUTED, Constants.Defaults.GLOBAL_MUTED_MSG);
                return;
            }

            if (args.length == 0) {
                messageManager.sendConfigMessage(player, Constants.Messages.USAGE, Constants.Defaults.USAGE_MSG);
                return;
            }

            String message = String.join(" ", args);

            // Cooldown check
            if (hasCooldown(player.getUniqueId()) && !player.hasPermission(Constants.Permissions.GC_BYPASS_COOLDOWN)) {
                long remainingTime = getRemainingCooldown(player.getUniqueId());
                String cooldownMessage = messageManager.getFormattedMessage(
                        Constants.Messages.COOLDOWN, 
                        Constants.Defaults.COOLDOWN_MSG,
                        "{time}", String.valueOf(remainingTime)
                );
                messageManager.sendMessage(player, cooldownMessage);
                return;
            }

            // Message length check
            if (chatFilter.exceedsMaxLength(message)) {
                String lengthMessage = messageManager.getFormattedMessage(
                        Constants.Messages.MESSAGE_TOO_LONG,
                        Constants.Defaults.MESSAGE_TOO_LONG_MSG,
                        "{max}", String.valueOf(chatFilter.getMaxMessageLength())
                );
                messageManager.sendMessage(player, lengthMessage);
                return;
            }

            // Banned words check
            if (chatFilter.containsBannedWords(message)) {
                messageManager.sendConfigMessage(player, Constants.Messages.BANNED_WORDS, Constants.Defaults.BANNED_WORDS_MSG);
                return;
            }

            // URL check
            if (chatFilter.containsUrl(message) && !player.hasPermission(Constants.Permissions.GC_BYPASS_URL)) {
                messageManager.sendConfigMessage(player, Constants.Messages.NO_URLS, Constants.Defaults.NO_URLS_MSG);
                return;
            }

            // Send the message
            messageManager.sendGlobalMessage(player, message, hiddenGlobalChat);
            setCooldown(player.getUniqueId());
        }
    }


} 