package me.dev_dio.chatnova;

public final class Constants {
    
    // Plugin Information
    public static final String PLUGIN_NAME = "ChatNova";
    
    // File Names
    public static final String CONFIG_FILE = "config.yml";
    public static final String BLACKLIST_FILE = "blacklist.yml";
    public static final String BANNED_WORDS_FILE = "banned-words.yml";
    public static final String HIDDEN_GC_FILE = "hidden_gc.yml";
    
    // Configuration Paths
    public static final class Config {
        public static final String CHAT_MAX_LENGTH = "chat.max-length";
        public static final String USE_MINI_MESSAGE = "use-minimessage";
        public static final String COOLDOWN_DEFAULT = "cooldown.default";
        public static final String COOLDOWN_PREMIUM = "cooldown.premium";
        public static final String COOLDOWN_VIP = "cooldown.vip";
    }
    
    // Format Paths
    public static final class Format {
        public static final String PLAYER = "format.player";
        public static final String CONSOLE = "format.console";
        public static final String SERVER_PREFIX = "format.";
    }
    
    // Message Paths
    public static final class Messages {
        public static final String FORMAT = "messages.format";
        public static final String NO_PERMISSION = "messages.no-permission";
        public static final String BLACKLISTED = "messages.blacklisted";
        public static final String MUTED = "messages.muted";
        public static final String GLOBAL_MUTED = "messages.global-muted";
        public static final String USAGE = "messages.usage";
        public static final String COOLDOWN = "messages.cooldown";
        public static final String MESSAGE_TOO_LONG = "messages.message-too-long";
        public static final String BANNED_WORDS = "messages.banned-words";
        public static final String NO_URLS = "messages.no-urls";
        public static final String GC_HIDDEN = "messages.gc-hidden";
        public static final String GC_SHOWN = "messages.gc-shown";
    }
    
    // Permissions
    public static final class Permissions {
        public static final String GC_USE = "gc.use";
        public static final String GC_MUTE = "gc.mute";
        public static final String GC_BLACKLIST = "gc.blacklist";
        public static final String GC_RELOAD = "gc.reload";
        public static final String GC_TOGGLE = "gc.toggle";
        public static final String GC_BYPASS_MUTE = "gc.bypass.mute";
        public static final String GC_BYPASS_COOLDOWN = "gc.bypass.cooldown";
        public static final String GC_BYPASS_URL = "gc.bypass.url";
        public static final String GC_COOLDOWN_VIP = "gc.cooldown.vip";
        public static final String GC_COOLDOWN_PREMIUM = "gc.cooldown.premium";
    }
    
    // Default Values
    public static final class Defaults {
        public static final int MAX_MESSAGE_LENGTH = 100;
        public static final int COOLDOWN_DEFAULT = 10;
        public static final int COOLDOWN_PREMIUM = 5;
        public static final int COOLDOWN_VIP = 2;
        public static final boolean USE_MINI_MESSAGE = false;
        
        // Default Messages
        public static final String NO_PERMISSION_MSG = "&cYou don't have permission to use this command!";
        public static final String BLACKLISTED_MSG = "&cYou are blacklisted from using global chat!";
        public static final String MUTED_MSG = "&cYou are muted and cannot use global chat!";
        public static final String GLOBAL_MUTED_MSG = "&cGlobal chat is currently muted!";
        public static final String USAGE_MSG = "&eUsage: /gc <message>";
        public static final String COOLDOWN_MSG = "&cYou must wait {time} seconds before using global chat again!";
        public static final String MESSAGE_TOO_LONG_MSG = "&cYour message is too long! Maximum length is {max} characters.";
        public static final String BANNED_WORDS_MSG = "&cYour message contains banned words!";
        public static final String NO_URLS_MSG = "&cURLs are not allowed in global chat!";
        public static final String FORMAT_MSG = "&f{rank}{player}&7: &f{message}";
        public static final String CONSOLE_FORMAT_MSG = "Administrator: &c{message}";
        public static final String GC_HIDDEN_MSG = "&aGlobal chat has been hidden. You will not see global chat messages.";
        public static final String GC_SHOWN_MSG = "&aGlobal chat has been shown. You will now see global chat messages.";
    }
    
    // YAML Keys
    public static final class YamlKeys {
        public static final String BLACKLISTED_PLAYERS = "blacklisted-players";
        public static final String HIDDEN_PLAYERS = "hidden-players";
        public static final String BANNED_WORDS = "banned-words";
    }
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
} 