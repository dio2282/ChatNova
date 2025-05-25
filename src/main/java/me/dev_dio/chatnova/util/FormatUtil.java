package me.dev_dio.chatnova.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for handling text formatting with support for both legacy and MiniMessage formats
 */
public final class FormatUtil {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    
    // Patterns for format detection and conversion
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&[0-9a-fk-orA-FK-OR]");
    private static final Pattern MINI_MESSAGE_PATTERN = Pattern.compile("<[^>]+>");
    
    // Color code mappings for legacy to MiniMessage conversion
    private static final Map<String, String> COLOR_MAPPINGS = new HashMap<>();
    static {
        COLOR_MAPPINGS.put("&0", "<black>");
        COLOR_MAPPINGS.put("&1", "<dark_blue>");
        COLOR_MAPPINGS.put("&2", "<dark_green>");
        COLOR_MAPPINGS.put("&3", "<dark_aqua>");
        COLOR_MAPPINGS.put("&4", "<dark_red>");
        COLOR_MAPPINGS.put("&5", "<dark_purple>");
        COLOR_MAPPINGS.put("&6", "<gold>");
        COLOR_MAPPINGS.put("&7", "<gray>");
        COLOR_MAPPINGS.put("&8", "<dark_gray>");
        COLOR_MAPPINGS.put("&9", "<blue>");
        COLOR_MAPPINGS.put("&a", "<green>");
        COLOR_MAPPINGS.put("&b", "<aqua>");
        COLOR_MAPPINGS.put("&c", "<red>");
        COLOR_MAPPINGS.put("&d", "<light_purple>");
        COLOR_MAPPINGS.put("&e", "<yellow>");
        COLOR_MAPPINGS.put("&f", "<white>");
        
        // Format codes
        COLOR_MAPPINGS.put("&l", "<bold>");
        COLOR_MAPPINGS.put("&o", "<italic>");
        COLOR_MAPPINGS.put("&n", "<underlined>");
        COLOR_MAPPINGS.put("&m", "<strikethrough>");
        COLOR_MAPPINGS.put("&k", "<obfuscated>");
        COLOR_MAPPINGS.put("&r", "<reset>");
    }
    
    /**
     * Convert legacy format to MiniMessage format
     * @param legacy The legacy formatted string
     * @return MiniMessage formatted string
     */
    public static String legacyToMini(String legacy) {
        if (legacy == null || legacy.isEmpty()) {
            return "";
        }
        
        // Already in MiniMessage format, return as-is
        if (isMiniMessage(legacy) && !hasLegacyCodes(legacy)) {
            return legacy;
        }
        
        String result = legacy;
        
        // Convert hex colors first
        result = HEX_PATTERN.matcher(result).replaceAll("<color:#$1>");
        
        // Convert standard color and format codes
        for (Map.Entry<String, String> entry : COLOR_MAPPINGS.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
            result = result.replace(entry.getKey().toUpperCase(), entry.getValue());
        }
        
        return result;
    }
    
    /**
     * Convert MiniMessage format to legacy format
     * @param miniMessage The MiniMessage formatted string
     * @return Legacy formatted string
     */
    public static String miniToLegacy(String miniMessage) {
        if (miniMessage == null || miniMessage.isEmpty()) {
            return "";
        }
        
        // If it's already legacy format, return as-is
        if (!isMiniMessage(miniMessage)) {
            return miniMessage;
        }
        
        try {
            Component component = MiniMessage.miniMessage().deserialize(miniMessage);
            return legacySerializer.serialize(component);
        } catch (Exception e) {
            // If parsing fails, return original string
            return miniMessage;
        }
    }
    
    /**
     * Format text to Component using the appropriate format (MiniMessage or Legacy)
     * @param text The text to format
     * @param useMiniMessage Whether to use MiniMessage format
     * @return Formatted Component
     */
    public static Component format(String text, boolean useMiniMessage) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        
        try {
            if (useMiniMessage) {
                // Convert legacy to MiniMessage if needed
                String miniText = hasLegacyCodes(text) ? legacyToMini(text) : text;
                return miniMessage.deserialize(miniText);
            } else {
                // Use legacy formatting
                return legacySerializer.deserialize(text);
            }
        } catch (Exception e) {
            // If formatting fails, return plain text
            return Component.text(stripAllFormatting(text));
        }
    }
    
    /**
     * Format text to Component using MiniMessage, handling legacy codes
     * @param text The text to format
     * @return Formatted Component
     */
    public static Component format(String text) {
        return format(text, true);
    }
    
    /**
     * Strip all formatting codes for console output
     * @param text The text to strip
     * @return Text without formatting codes
     */
    public static String stripAllFormatting(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        String result = text;
        
        // Remove legacy codes
        result = result.replaceAll("&[0-9a-fk-orA-FK-ORxX]", "");
        // Remove hex codes
        result = result.replaceAll("&#[0-9a-fA-F]{6}", "");
        // Remove MiniMessage tags
        result = result.replaceAll("<[^>]*>", "");
        
        return result;
    }
    
    /**
     * Check if text contains legacy color codes
     * @param text The text to check
     * @return true if text contains legacy codes
     */
    public static boolean hasLegacyCodes(String text) {
        if (text == null) return false;
        return LEGACY_PATTERN.matcher(text).find() || HEX_PATTERN.matcher(text).find();
    }
    
    /**
     * Check if text is in MiniMessage format
     * @param text The text to check
     * @return true if text appears to be MiniMessage format
     */
    public static boolean isMiniMessage(String text) {
        if (text == null) return false;
        return MINI_MESSAGE_PATTERN.matcher(text).find();
    }
    
    /**
     * Apply placeholders to a text string
     * @param text The text with placeholders
     * @param placeholders Map of placeholder -> replacement pairs
     * @return Text with placeholders replaced
     */
    public static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty()) {
            return text;
        }
        
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue() != null ? entry.getValue() : "";
            
            // Support both {placeholder} and %placeholder% formats
            result = result.replace("{" + placeholder + "}", replacement);
            result = result.replace("%" + placeholder + "%", replacement);
            result = result.replace(placeholder, replacement);
        }
        
        return result;
    }
    
    /**
     * Apply simple placeholder replacements
     * @param text The text with placeholders
     * @param replacements Alternating placeholder and replacement values
     * @return Text with placeholders replaced
     */
    public static String applyPlaceholders(String text, String... replacements) {
        if (text == null || replacements.length == 0) {
            return text;
        }
        
        String result = text;
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = replacements[i];
                String replacement = replacements[i + 1] != null ? replacements[i + 1] : "";
                
                // Support both {placeholder} and %placeholder% formats
                result = result.replace("{" + placeholder + "}", replacement);
                result = result.replace("%" + placeholder + "%", replacement);
                result = result.replace(placeholder, replacement);
            }
        }
        
        return result;
    }
    
    /**
     * Validate that a color code is valid
     * @param colorCode The color code to validate (e.g., "&c", "#FF0000")
     * @return true if the color code is valid
     */
    public static boolean isValidColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) {
            return false;
        }
        
        // Check legacy format
        if (colorCode.length() == 2 && colorCode.startsWith("&")) {
            char code = Character.toLowerCase(colorCode.charAt(1));
            return (code >= '0' && code <= '9') || (code >= 'a' && code <= 'f') || 
                   "klmnor".indexOf(code) != -1;
        }
        
        // Check hex format
        if (colorCode.startsWith("#") && colorCode.length() == 7) {
            try {
                Integer.parseInt(colorCode.substring(1), 16);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return false;
    }
    
    // Private constructor to prevent instantiation
    private FormatUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
} 