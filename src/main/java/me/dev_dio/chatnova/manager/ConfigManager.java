package me.dev_dio.chatnova.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import me.dev_dio.chatnova.Constants;

public class ConfigManager {
    private final Path dataDirectory;
    private final Logger logger;
    private Map<String, Object> config;
    
    public ConfigManager(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.config = new HashMap<>();
    }
    
    public void loadConfig() {
        try {
            Path configPath = dataDirectory.resolve(Constants.CONFIG_FILE);
            
            if (!Files.exists(configPath)) {
                Files.createDirectories(dataDirectory);
                // Copy default config from resources
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    } else {
                        saveDefaultConfig(configPath);
                    }
                }
            }
            
            Yaml yaml = new Yaml();
            try (Reader reader = Files.newBufferedReader(configPath)) {
                config = yaml.load(reader);
                if (config == null) {
                    config = new HashMap<>();
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load config", e);
            config = new HashMap<>();
        }
    }
    
    public void reloadConfig() {
        loadConfig();
        logger.info("Configuration reloaded successfully!");
    }
    
    public String getString(String path, String defaultValue) {
        if (config == null) return defaultValue;
        
        String[] keys = path.split("\\.");
        Object current = config;
        
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return defaultValue;
            }
        }
        
        return current != null ? current.toString() : defaultValue;
    }
    
    public int getInt(String path, int defaultValue) {
        if (config == null) return defaultValue;
        
        String[] keys = path.split("\\.");
        Object current = config;
        
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return defaultValue;
            }
        }
        
        return current instanceof Number ? ((Number) current).intValue() : defaultValue;
    }
    
    public boolean getBoolean(String path, boolean defaultValue) {
        String value = getString(path, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    private void saveDefaultConfig(Path configPath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            writer.write("# " + Constants.PLUGIN_NAME + " Configuration\n");
            writer.write("# Global chat plugin for Velocity\n\n");
            writer.write("# Message format settings\n");
            writer.write("messages:\n");
            writer.write("  format: \"" + Constants.Defaults.FORMAT_MSG + "\"\n");
            writer.write("  no-permission: \"" + Constants.Defaults.NO_PERMISSION_MSG + "\"\n");
            writer.write("  blacklisted: \"" + Constants.Defaults.BLACKLISTED_MSG + "\"\n");
            writer.write("  muted: \"" + Constants.Defaults.MUTED_MSG + "\"\n");
            writer.write("  global-muted: \"" + Constants.Defaults.GLOBAL_MUTED_MSG + "\"\n");
            writer.write("  usage: \"" + Constants.Defaults.USAGE_MSG + "\"\n");
            writer.write("  cooldown: \"" + Constants.Defaults.COOLDOWN_MSG + "\"\n");
            writer.write("  message-too-long: \"" + Constants.Defaults.MESSAGE_TOO_LONG_MSG + "\"\n");
            writer.write("  banned-words: \"" + Constants.Defaults.BANNED_WORDS_MSG + "\"\n");
            writer.write("  no-urls: \"" + Constants.Defaults.NO_URLS_MSG + "\"\n\n");
            writer.write("# Cooldown settings (in seconds)\n");
            writer.write("cooldown:\n");
            writer.write("  default: " + Constants.Defaults.COOLDOWN_DEFAULT + "\n");
            writer.write("  premium: " + Constants.Defaults.COOLDOWN_PREMIUM + "\n");
            writer.write("  vip: " + Constants.Defaults.COOLDOWN_VIP + "\n\n");
            writer.write("# Chat filter settings\n");
            writer.write("chat:\n");
            writer.write("  max-length: " + Constants.Defaults.MAX_MESSAGE_LENGTH + "\n\n");
            writer.write("# Enable MiniMessage format (true) or legacy format (false)\n");
            writer.write("use-minimessage: " + Constants.Defaults.USE_MINI_MESSAGE + "\n");
        }
    }
} 