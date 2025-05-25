package me.dev_dio.chatnova.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;
import me.dev_dio.chatnova.manager.MessageManager;

public abstract class BaseCommand implements SimpleCommand {
    protected final ChatNova plugin;
    protected final MessageManager messageManager;
    
    public BaseCommand(ChatNova plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    /**
     * Check if the command source has the required permission
     */
    protected boolean hasPermission(CommandSource source, String permission) {
        return source.hasPermission(permission);
    }
    
    /**
     * Send a "no permission" message to the command source
     */
    protected void sendNoPermissionMessage(CommandSource source) {
        messageManager.sendConfigMessage(source, 
                Constants.Messages.NO_PERMISSION, 
                Constants.Defaults.NO_PERMISSION_MSG);
    }
    
    /**
     * Send a formatted message to the command source
     */
    protected void sendMessage(CommandSource source, String message) {
        messageManager.sendMessage(source, message);
    }
    
    /**
     * Send a message from config to the command source
     */
    protected void sendConfigMessage(CommandSource source, String configPath, String defaultMessage) {
        messageManager.sendConfigMessage(source, configPath, defaultMessage);
    }
    
    /**
     * Execute the command with permission check
     */
    protected abstract void executeCommand(Invocation invocation);
    
    /**
     * Get the required permission for this command
     */
    protected abstract String getRequiredPermission();
    
    @Override
    public void execute(Invocation invocation) {
        String permission = getRequiredPermission();
        
        if (permission != null && !hasPermission(invocation.source(), permission)) {
            sendNoPermissionMessage(invocation.source());
            return;
        }
        
        executeCommand(invocation);
    }
} 