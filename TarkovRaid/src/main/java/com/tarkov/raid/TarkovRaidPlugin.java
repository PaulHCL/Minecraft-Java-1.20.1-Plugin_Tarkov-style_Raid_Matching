package com.tarkov.raid;

import org.bukkit.plugin.java.JavaPlugin;

public class TarkovRaidPlugin extends JavaPlugin {
    private RaidManager raidManager;
    private CommandHandler commandHandler;
    private JoinListener joinListener;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messageManager = new MessageManager(this);
        this.raidManager = new RaidManager(this);
        this.raidManager.loadData();

        this.commandHandler = new CommandHandler(this);
        getCommand("traid").setExecutor(this.commandHandler);
        getCommand("traid").setTabCompleter(this.commandHandler);

        this.joinListener = new JoinListener(this);
        getServer().getPluginManager().registerEvents(this.joinListener, this);

        getLogger().info("Tarkov Raid System Enabled.");
    }

    @Override
    public void onDisable() {
        if (raidManager != null) raidManager.saveData();
        getLogger().info("Tarkov Raid System Disabled.");
    }

    public RaidManager getRaidManager() {
        return raidManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}