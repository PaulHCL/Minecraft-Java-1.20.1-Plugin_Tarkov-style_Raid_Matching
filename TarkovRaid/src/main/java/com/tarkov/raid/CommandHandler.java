package com.tarkov.raid;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final TarkovRaidPlugin plugin;

    public CommandHandler(TarkovRaidPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().send(sender, "console_only");
            return true;
        }
        Player p = (Player) sender;
        RaidManager manager = plugin.getRaidManager();

        if (args.length == 0) {
            p.sendMessage(plugin.getMessageManager().getPrefix() +
                    plugin.getMessageManager().colorize("&6=== 塔科夫战局系统 ==="));
            plugin.getMessageManager().sendRaw(p, "help_create");
            plugin.getMessageManager().sendRaw(p, "help_create_continue");
            plugin.getMessageManager().sendRaw(p, "help_confirm");
            plugin.getMessageManager().sendRaw(p, "help_setjoin");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("create")) {
            RaidManager.SelectionSession session = manager.getSession(p.getUniqueId());
            Location loc = p.getLocation();

            if (session == null) {
                if (args.length < 2) {
                    plugin.getMessageManager().send(p, "first_create_name");
                    return true;
                }
                String name = args[1];
                if (manager.getRaid(name) != null) {
                    plugin.getMessageManager().send(p, "raid_exists");
                    return true;
                }
                manager.createRaidSession(p.getUniqueId(), name);
                manager.addPoint(p.getUniqueId(), loc);
                p.sendMessage(plugin.getMessageManager().getPrefix() +
                        plugin.getMessageManager().colorize("&a[1/5] 已记录第 1 点。请前往第 2 个角落输入 /traid create"));
            } else {
                int step = session.getStep();
                if (step >= 5) {
                    plugin.getMessageManager().send(p, "points_enough");
                    return true;
                }

                if (step == 4) {
                    Location firstPoint = session.getPoints().get(0);
                    if (!loc.getBlock().equals(firstPoint.getBlock())) {
                        plugin.getMessageManager().send(p, "verify_failed");
                        return true;
                    }
                }

                manager.addPoint(p.getUniqueId(), loc);
                int nextStep = step + 1;

                if (nextStep < 5) {
                    plugin.getMessageManager().send(p, "point_recorded", String.valueOf(nextStep));
                } else {
                    plugin.getMessageManager().send(p, "point_final");
                }
            }
        } else if (sub.equals("confirm")) {
            RaidManager.SelectionSession session = manager.getSession(p.getUniqueId());
            if (session == null) {
                plugin.getMessageManager().send(p, "no_session");
                return true;
            }
            if (session.getStep() < 5) {
                plugin.getMessageManager().send(p, "not_enough_points");
                return true;
            }
            if (manager.finalizeRaid(p.getUniqueId())) {
                plugin.getMessageManager().send(p, "raid_created", session.getName());
            } else {
                plugin.getMessageManager().send(p, "raid_create_failed");
            }
        } else if (sub.equals("setjoin")) {
            if (args.length < 2) {
                p.sendMessage(plugin.getMessageManager().getPrefix() +
                        plugin.getMessageManager().colorize("&c用法：/traid setjoin <战局名>"));
                return true;
            }
            String raidName = args[1];
            if (manager.getRaid(raidName) == null) {
                plugin.getMessageManager().send(p, "raid_not_found");
                return true;
            }
            manager.addJoinPoint(p.getLocation(), raidName);
            plugin.getMessageManager().send(p, "join_point_set", raidName);
        } else if (sub.equals("reload")) {
            if (!p.isOp()) {
                plugin.getMessageManager().send(p, "no_permission");
                return true;
            }
            plugin.reloadConfig();
            manager.loadData();
            plugin.getMessageManager().send(p, "config_reloaded");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("create");
            completions.add("confirm");
            completions.add("setjoin");
            completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setjoin")) {
            Set<String> raidNames = plugin.getRaidManager().getRaidNames();
            for (String name : raidNames) {
                completions.add(name);
            }
        }
        return completions;
    }
}