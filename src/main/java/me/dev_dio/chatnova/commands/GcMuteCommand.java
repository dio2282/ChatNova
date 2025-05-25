package me.dev_dio.chatnova.commands;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;

public class GcMuteCommand extends BaseCommand {
    
    public GcMuteCommand(ChatNova plugin) {
        super(plugin);
    }
    
    @Override
    protected String getRequiredPermission() {
        return Constants.Permissions.GC_MUTE;
    }
    
    @Override
    protected void executeCommand(Invocation invocation) {
        plugin.toggleGlobalChatMute();
        
        String messageKey = plugin.isGlobalChatMuted() ? 
                "&cGlobal chat has been muted!" : 
                "&aGlobal chat has been unmuted!";
        
        // Broadcast to all players
        plugin.getServer().getAllPlayers().forEach(player ->
                messageManager.sendMessage(player, messageKey)
        );
        
        // Send confirmation to command sender
        sendMessage(invocation.source(), messageKey);
    }
} 