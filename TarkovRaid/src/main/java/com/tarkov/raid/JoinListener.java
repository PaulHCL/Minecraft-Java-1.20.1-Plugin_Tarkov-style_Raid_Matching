package com.tarkov.raid;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class JoinListener implements Listener {
    private final TarkovRaidPlugin plugin;
    private final Random random = new Random();
    private final Map<UUID, Long> teleportCooldown = new HashMap<>();
    private final Map<UUID, String> preparingPlayers = new HashMap<>();
    private final Map<UUID, CountdownTask> countdownTasks = new HashMap<>();

    // 圆形判定半径（2 格半径 = 5 格直径）
    private static final double JOIN_RADIUS = 2.5;

    public JoinListener(TarkovRaidPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        UUID playerId = player.getUniqueId();

        // 检查是否在加入点范围内
        String raidName = getRaidNameInRadius(to, JOIN_RADIUS);

        if (raidName != null) {
            if (!preparingPlayers.containsKey(playerId) && !countdownTasks.containsKey(playerId)) {
                preparingPlayers.put(playerId, raidName);
                player.sendTitle(
                        "§e请确认是否加入战局",
                        "§7 战局：§b" + raidName + "\n§f 右键确认 | 左键取消",
                        10, 100, 10
                );
            }
        } else {
            // 离开范围，取消准备和倒计时
            if (preparingPlayers.containsKey(playerId)) {
                preparingPlayers.remove(playerId);
                player.sendTitle("", "", 0, 1, 0);
            }
            if (countdownTasks.containsKey(playerId)) {
                cancelCountdown(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!preparingPlayers.containsKey(playerId)) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        String raidName = preparingPlayers.get(playerId);

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);
            startCountdown(player, raidName);
        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            event.setCancelled(true);
            cancelJoin(player);
        }
    }

    private String getRaidNameInRadius(Location loc, double radius) {
        RaidManager manager = plugin.getRaidManager();
        int centerX = loc.getBlockX();
        int centerY = loc.getBlockY();
        int centerZ = loc.getBlockZ();

        // 检查圆形范围内的所有方块
        for (int x = centerX - (int)radius; x <= centerX + (int)radius; x++) {
            for (int z = centerZ - (int)radius; z <= centerZ + (int)radius; z++) {
                // 圆形判定：距离中心的距离 <= 半径
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance <= radius) {
                    Block block = loc.getWorld().getBlockAt(x, centerY - 1, z);
                    String raidName = manager.getRaidNameAt(block.getLocation());
                    if (raidName != null) {
                        return raidName;
                    }
                }
            }
        }
        return null;
    }

    private void startCountdown(Player player, String raidName) {
        UUID playerId = player.getUniqueId();

        // 先预检测安全传送位置
        Location playerLoc = player.getLocation();
        RaidManager manager = plugin.getRaidManager();
        RaidManager.RaidData raid = manager.getRaid(raidName);

        if (raid == null) {
            player.sendMessage("§c战局不存在！");
            cancelJoin(player);
            return;
        }

        // 预先找到安全位置
        Location safeLoc = findSafeLocation(raid, player.getWorld());
        if (safeLoc == null) {
            player.sendMessage("§c无法找到安全的传送位置，请稍后再试或联系管理员。");
            cancelJoin(player);
            return;
        }

        // 开始倒计时
        player.sendTitle(
                "§e§l准备传送",
                "§7战局：§b" + raidName + "\n§f§l10",
                10, 20, 10
        );

        CountdownTask task = new CountdownTask(player, raidName, safeLoc);
        BukkitTask bukkitTask = task.runTaskTimer(plugin, 20L, 20L); // 1 秒执行一次
        task.setBukkitTask(bukkitTask);
        countdownTasks.put(playerId, task);

        player.sendMessage("§e你已确认加入战局，§b10 秒后 §e传送...");
        player.sendMessage("§c离开加入点范围将取消传送！");
    }

    private void cancelCountdown(Player player) {
        UUID playerId = player.getUniqueId();
        CountdownTask task = countdownTasks.remove(playerId);
        if (task != null) {
            task.getBukkitTask().cancel();
        }
        player.sendTitle(
                "§c§l传送取消",
                "§7你已离开加入点范围",
                10, 50, 10
        );
        player.sendMessage("§c你已离开加入点范围，传送已取消。");
    }

    private void cancelJoin(Player player) {
        UUID playerId = player.getUniqueId();
        preparingPlayers.remove(playerId);

        CountdownTask task = countdownTasks.remove(playerId);
        if (task != null) {
            task.getBukkitTask().cancel();
        }

        player.sendTitle(
                "§c已取消",
                "§7 你已取消加入战局",
                10, 50, 10
        );
        player.sendMessage("§e你已取消加入战局。");
    }

    private Location findSafeLocation(RaidManager.RaidData raid, org.bukkit.World world) {
        int maxAttempts = plugin.getConfig().getInt("teleport.max_attempts", 50);
        int minY = plugin.getConfig().getInt("teleport.min_y", -64);
        int maxY = plugin.getConfig().getInt("teleport.max_y", 319);

        int attempts = 0;
        while (attempts < maxAttempts) {
            double x = raid.getMinX() + (random.nextDouble() * (raid.getMaxX() - raid.getMinX()));
            double z = raid.getMinZ() + (random.nextDouble() * (raid.getMaxZ() - raid.getMinZ()));
            int y = minY + random.nextInt(maxY - minY);

            Location loc = new Location(world, x, y, z);

            if (isSafe(loc)) {
                return loc;
            }
            attempts++;
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        Block target = loc.getBlock();
        Block above = loc.clone().add(0, 1, 0).getBlock();
        Block below = loc.clone().add(0, -1, 0).getBlock();

        if (!target.getType().isAir() && !target.isPassable()) return false;
        if (!above.getType().isAir() && !above.isPassable()) return false;
        if (below.getType().isAir() || !below.getType().isSolid()) return false;

        return true;
    }

    // 内部类：倒计时任务
    private class CountdownTask extends BukkitRunnable {
        private final Player player;
        private final String raidName;
        private final Location safeLocation;
        private BukkitTask bukkitTask;
        private int secondsLeft = 10;

        public CountdownTask(Player player, String raidName, Location safeLocation) {
            this.player = player;
            this.raidName = raidName;
            this.safeLocation = safeLocation;
        }

        public void setBukkitTask(BukkitTask task) {
            this.bukkitTask = task;
        }

        public BukkitTask getBukkitTask() {
            return bukkitTask;
        }

        @Override
        public void run() {
            UUID playerId = player.getUniqueId();

            // 检查玩家是否还在准备列表中
            if (!preparingPlayers.containsKey(playerId)) {
                cancel();
                countdownTasks.remove(playerId);
                return;
            }

            // 检查玩家是否还在加入点范围内
            String currentRaidName = getRaidNameInRadius(player.getLocation(), JOIN_RADIUS);
            if (currentRaidName == null || !currentRaidName.equals(raidName)) {
                cancelCountdown(player);
                cancel();
                countdownTasks.remove(playerId);
                return;
            }

            secondsLeft--;

            if (secondsLeft > 0) {
                // 更新倒计时显示
                player.sendTitle(
                        "§e§l准备传送",
                        "§7 战局：§b" + raidName + "\n§f§l" + secondsLeft,
                        0, 20, 0
                );
                player.sendMessage("§e传送倒计时：§b" + secondsLeft + " §e秒...");
            } else {
                // 倒计时结束，执行传送
                player.teleport(safeLocation);
                plugin.getMessageManager().send(player, "teleport_message", raidName);

                // 清理状态
                preparingPlayers.remove(playerId);
                countdownTasks.remove(playerId);
                player.sendTitle(
                        "§a§l传送成功",
                        "§7 你已进入战局区域",
                        10, 50, 10
                );

                // 添加冷却时间
                teleportCooldown.put(playerId, System.currentTimeMillis());

                cancel();
            }
        }
    }
}