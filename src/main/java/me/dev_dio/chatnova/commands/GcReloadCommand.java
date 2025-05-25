package me.dev_dio.chatnova.commands;

import me.dev_dio.chatnova.ChatNova;
import me.dev_dio.chatnova.Constants;

public class GcReloadCommand extends BaseCommand {
    
    public GcReloadCommand(ChatNova plugin) {
        super(plugin);
    }
    
    @Override
    protected String getRequiredPermission() {
        return Constants.Permissions.GC_RELOAD;
    }
    
    @Override
    protected void executeCommand(Invocation invocation) {
        plugin.reloadConfig();
        sendMessage(invocation.source(), "&a" + Constants.PLUGIN_NAME + " configuration reloaded!");
    }
} 