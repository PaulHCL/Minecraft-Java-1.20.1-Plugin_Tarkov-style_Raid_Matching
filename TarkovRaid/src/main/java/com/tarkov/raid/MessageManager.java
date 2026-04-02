package com.tarkov.raid;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageManager {
    private final TarkovRaidPlugin plugin;

    public MessageManager(TarkovRaidPlugin plugin) {
        this.plugin = plugin;
    }

    public String getPrefix() {
        return colorize(plugin.getConfig().getString("messages.prefix", "&6[TarkovRaid] "));
    }

    public void send(CommandSender sender, String path) {
        String msg = plugin.getConfig().getString("messages." + path);
        if (msg != null) {
            sender.sendMessage(getPrefix() + colorize(msg));
        }
    }

    public void send(CommandSender sender, String path, String... args) {
        String msg = plugin.getConfig().getString("messages." + path);
        if (msg != null) {
            for (int i = 0; i < args.length; i++) {
                msg = msg.replace("{" + i + "}", args[i]);
            }
            sender.sendMessage(getPrefix() + colorize(msg));
        }
    }

    public void sendRaw(CommandSender sender, String path) {
        String msg = plugin.getConfig().getString("messages." + path);
        if (msg != null) {
            sender.sendMessage(colorize(msg));
        }
    }

    public void sendRaw(CommandSender sender, String path, String... args) {
        String msg = plugin.getConfig().getString("messages." + path);
        if (msg != null) {
            for (int i = 0; i < args.length; i++) {
                msg = msg.replace("{" + i + "}", args[i]);
            }
            sender.sendMessage(colorize(msg));
        }
    }

    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}