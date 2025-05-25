package me.dev_dio.chatnova.commands;

import java.util.Set;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;

public class GcListBlacklistCommand extends BaseCommand {
    
    public GcListBlacklistCommand(ChatNova plugin) {
        super(plugin);
    }
    
    @Override
    protected String getRequiredPermission() {
        return Constants.Permissions.GC_BLACKLIST;
    }
    
    @Override
    protected void executeCommand(Invocation invocation) {
        Set<String> blacklistedPlayers = plugin.getBlacklistedPlayers();
        
        if (blacklistedPlayers.isEmpty()) {
            sendMessage(invocation.source(), "&eNo players are currently blacklisted.");
        } else {
            sendMessage(invocation.source(), "&eBlacklisted players:");
            for (String player : blacklistedPlayers) {
                sendMessage(invocation.source(), "&7- &f" + player);
            }
        }
    }
} 