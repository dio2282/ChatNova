package me.dev_dio.chatnova.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Handles chat message filtering including banned words, message length, and URL detection
 */
public class ChatFilter {
    private final Set<String> bannedWords;
    private final int maxMessageLength;
    private final Pattern urlPattern;
    private final Pattern ipPattern;
    
    public ChatFilter(int maxMessageLength) {
        this.bannedWords = new HashSet<>();
        this.maxMessageLength = maxMessageLength;
        
        // Improved URL detection pattern
        this.urlPattern = Pattern.compile(
            "(?i)\\b(?:https?://|www\\.|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\\S*",
            Pattern.CASE_INSENSITIVE
        );
        
        // IP address detection pattern
        this.ipPattern = Pattern.compile(
            "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?::[0-9]{1,5})?\\b"
        );
    }
    
    /**
     * Add a banned word to the filter
     * @param word The word to ban (case-insensitive)
     */
    public void addBannedWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            bannedWords.add(word.toLowerCase().trim());
        }
    }
    
    /**
     * Remove a banned word from the filter
     * @param word The word to remove (case-insensitive)
     */
    public void removeBannedWord(String word) {
        if (word != null) {
            bannedWords.remove(word.toLowerCase().trim());
        }
    }
    
    /**
     * Clear all banned words
     */
    public void clearBannedWords() {
        bannedWords.clear();
    }
    
    /**
     * Get all banned words
     * @return Set of banned words
     */
    public Set<String> getBannedWords() {
        return new HashSet<>(bannedWords);
    }
    
    /**
     * Check if message contains any banned words
     * @param message The message to check
     * @return true if message contains banned words
     */
    public boolean containsBannedWords(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        return bannedWords.stream().anyMatch(lowerMessage::contains);
    }
    
    /**
     * Check if message exceeds maximum length
     * @param message The message to check
     * @return true if message is too long
     */
    public boolean exceedsMaxLength(String message) {
        return message != null && message.length() > maxMessageLength;
    }
    
    /**
     * Check if message contains URLs
     * @param message The message to check
     * @return true if message contains URLs
     */
    public boolean containsUrl(String message) {
        return message != null && urlPattern.matcher(message).find();
    }
    
    /**
     * Check if message contains IP addresses
     * @param message The message to check
     * @return true if message contains IP addresses
     */
    public boolean containsIpAddress(String message) {
        return message != null && ipPattern.matcher(message).find();
    }
    
    /**
     * Get the maximum allowed message length
     * @return Maximum message length
     */
    public int getMaxMessageLength() {
        return maxMessageLength;
    }
    
    /**
     * Censor banned words in a message by replacing them with asterisks
     * @param message The message to censor
     * @return Censored message
     */
    public String censorMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        String censoredMessage = message;
        for (String bannedWord : bannedWords) {
            String stars = "*".repeat(bannedWord.length());
            censoredMessage = censoredMessage.replaceAll(
                "(?i)" + Pattern.quote(bannedWord), 
                stars
            );
        }
        return censoredMessage;
    }
    
    /**
     * Perform a complete filter check on a message
     * @param message The message to filter
     * @return FilterResult containing all filter results
     */
    public FilterResult filterMessage(String message) {
        return new FilterResult(
            containsBannedWords(message),
            exceedsMaxLength(message),
            containsUrl(message),
            containsIpAddress(message),
            censorMessage(message)
        );
    }
    
    /**
     * Result of filtering a message
     */
    public static class FilterResult {
        private final boolean hasBannedWords;
        private final boolean exceedsMaxLength;
        private final boolean hasUrls;
        private final boolean hasIpAddresses;
        private final String censoredMessage;
        
        public FilterResult(boolean hasBannedWords, boolean exceedsMaxLength, 
                          boolean hasUrls, boolean hasIpAddresses, String censoredMessage) {
            this.hasBannedWords = hasBannedWords;
            this.exceedsMaxLength = exceedsMaxLength;
            this.hasUrls = hasUrls;
            this.hasIpAddresses = hasIpAddresses;
            this.censoredMessage = censoredMessage;
        }
        
        public boolean hasBannedWords() { return hasBannedWords; }
        public boolean exceedsMaxLength() { return exceedsMaxLength; }
        public boolean hasUrls() { return hasUrls; }
        public boolean hasIpAddresses() { return hasIpAddresses; }
        public String getCensoredMessage() { return censoredMessage; }
        
        /**
         * Check if the message passed all filters
         * @return true if message is clean
         */
        public boolean isClean() {
            return !hasBannedWords && !exceedsMaxLength && !hasUrls && !hasIpAddresses;
        }
    }
} 