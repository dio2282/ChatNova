package me.dev_dio.chatnova.commands;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;

public class GcBlacklistCommand extends BaseCommand {
    
    public GcBlacklistCommand(ChatNova plugin) {
        super(plugin);
    }
    
    @Override
    protected String getRequiredPermission() {
        return Constants.Permissions.GC_BLACKLIST;
    }
    
    @Override
    protected void executeCommand(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length != 1) {
            sendMessage(invocation.source(), "&cUsage: /gcblacklist <player>");
            return;
        }
        
        String targetPlayer = args[0];
        
        if (plugin.isBlacklisted(targetPlayer)) {
            plugin.unblacklistPlayer(targetPlayer);
            sendMessage(invocation.source(), 
                    "&aPlayer " + targetPlayer + " has been removed from the blacklist.");
        } else {
            plugin.blacklistPlayer(targetPlayer);
            sendMessage(invocation.source(), 
                    "&cPlayer " + targetPlayer + " has been blacklisted from global chat.");
        }
    }
} 