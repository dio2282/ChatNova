package me.dev_dio.chatnova.commands;

import com.velocitypowered.api.proxy.Player;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;

public class GcToggleCommand extends BaseCommand {
    
    public GcToggleCommand(ChatNova plugin) {
        super(plugin);
    }
    
    @Override
    protected String getRequiredPermission() {
        return Constants.Permissions.GC_TOGGLE;
    }
    
    @Override
    protected void executeCommand(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            sendMessage(invocation.source(), "&cHanya player yang bisa menggunakan command ini.");
            return;
        }
        
        Player player = (Player) invocation.source();
        boolean toggled = plugin.toggleGlobalChatVisibility(player.getUniqueId());
        
        if (toggled) {
            sendConfigMessage(player, Constants.Messages.GC_HIDDEN, Constants.Defaults.GC_HIDDEN_MSG);
        } else {
            sendConfigMessage(player, Constants.Messages.GC_SHOWN, Constants.Defaults.GC_SHOWN_MSG);
        }
    }
} 