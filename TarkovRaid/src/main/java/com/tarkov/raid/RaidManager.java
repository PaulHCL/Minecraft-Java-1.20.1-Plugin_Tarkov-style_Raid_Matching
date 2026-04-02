package com.tarkov.raid;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RaidManager {
    private final TarkovRaidPlugin plugin;
    private final Map<String, RaidData> raids = new HashMap<>();
    private final List<JoinPoint> joinPoints = new ArrayList<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    private final Map<UUID, SelectionSession> sessions = new HashMap<>();

    public RaidManager(TarkovRaidPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "raids.yml");
    }

    public void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadFromConfig();
    }

    private void loadFromConfig() {
        raids.clear();
        joinPoints.clear();

        if (dataConfig.contains("raids")) {
            for (String name : dataConfig.getConfigurationSection("raids").getKeys(false)) {
                String path = "raids." + name;
                String worldName = dataConfig.getString(path + ".world");
                double minX = dataConfig.getDouble(path + ".minX");
                double maxX = dataConfig.getDouble(path + ".maxX");
                double minZ = dataConfig.getDouble(path + ".minZ");
                double maxZ = dataConfig.getDouble(path + ".maxZ");
                int minY = dataConfig.getInt(path + ".minY", -64);
                int maxY = dataConfig.getInt(path + ".maxY", 319);

                RaidData raid = new RaidData(name, worldName, minX, maxX, minZ, maxZ, minY, maxY);
                raids.put(name, raid);
            }
        }

        if (dataConfig.contains("joinpoints")) {
            for (String key : dataConfig.getConfigurationSection("joinpoints").getKeys(false)) {
                String path = "joinpoints." + key;
                String worldName = dataConfig.getString(path + ".world");
                double x = dataConfig.getDouble(path + ".x");
                double y = dataConfig.getDouble(path + ".y");
                double z = dataConfig.getDouble(path + ".z");
                String raidName = dataConfig.getString(path + ".raid");

                Location loc = new Location(org.bukkit.Bukkit.getWorld(worldName), x, y, z);
                joinPoints.add(new JoinPoint(loc, raidName));
            }
        }
    }

    public void saveData() {
        dataConfig = new YamlConfiguration();

        for (RaidData raid : raids.values()) {
            String path = "raids." + raid.getName();
            dataConfig.set(path + ".world", raid.getWorldName());
            dataConfig.set(path + ".minX", raid.getMinX());
            dataConfig.set(path + ".maxX", raid.getMaxX());
            dataConfig.set(path + ".minZ", raid.getMinZ());
            dataConfig.set(path + ".maxZ", raid.getMaxZ());
            dataConfig.set(path + ".minY", raid.getMinY());
            dataConfig.set(path + ".maxY", raid.getMaxY());
        }

        int index = 0;
        for (JoinPoint jp : joinPoints) {
            String path = "joinpoints." + index;
            dataConfig.set(path + ".world", jp.getLocation().getWorld().getName());
            dataConfig.set(path + ".x", jp.getLocation().getX());
            dataConfig.set(path + ".y", jp.getLocation().getY());
            dataConfig.set(path + ".z", jp.getLocation().getZ());
            dataConfig.set(path + ".raid", jp.getRaidName());
            index++;
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createRaidSession(UUID playerId, String name) {
        if (raids.containsKey(name)) return false;
        sessions.put(playerId, new SelectionSession(name));
        return true;
    }

    public SelectionSession getSession(UUID playerId) {
        return sessions.get(playerId);
    }

    public void removeSession(UUID playerId) {
        sessions.remove(playerId);
    }

    public void addPoint(UUID playerId, Location loc) {
        SelectionSession session = sessions.get(playerId);
        if (session != null) {
            session.addPoint(loc);
        }
    }

    public boolean finalizeRaid(UUID playerId) {
        SelectionSession session = sessions.get(playerId);
        if (session == null || session.getPoints().size() < 5) return false;

        List<Location> points = session.getPoints();
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;

        for (int i = 0; i < 4; i++) {
            Location p = points.get(i);
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minZ = Math.min(minZ, p.getZ());
            maxZ = Math.max(maxZ, p.getZ());
        }

        String worldName = points.get(0).getWorld().getName();
        RaidData raid = new RaidData(session.getName(), worldName, minX, maxX, minZ, maxZ, -64, 319);
        raids.put(session.getName(), raid);
        removeSession(playerId);
        saveData();
        return true;
    }

    public void addJoinPoint(Location loc, String raidName) {
        if (!raids.containsKey(raidName)) return;
        joinPoints.add(new JoinPoint(loc, raidName));
        saveData();
    }

    public String getRaidNameAt(Location loc) {
        for (JoinPoint jp : joinPoints) {
            if (jp.getLocation().getBlock().equals(loc.getBlock())) {
                return jp.getRaidName();
            }
        }
        return null;
    }

    public RaidData getRaid(String name) {
        return raids.get(name);
    }

    public Set<String> getRaidNames() {
        return raids.keySet();
    }

    public static class RaidData {
        private String name;
        private String worldName;
        private double minX, maxX, minZ, maxZ;
        private int minY;
        private int maxY;

        public RaidData(String name, String worldName, double minX, double maxX, double minZ, double maxZ, int minY, int maxY) {
            this.name = name;
            this.worldName = worldName;
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
            this.minY = minY;
            this.maxY = maxY;
        }

        public String getName() { return name; }
        public String getWorldName() { return worldName; }
        public double getMinX() { return minX; }
        public double getMaxX() { return maxX; }
        public double getMinZ() { return minZ; }
        public double getMaxZ() { return maxZ; }
        public int getMinY() { return minY; }
        public int getMaxY() { return maxY; }
    }

    public static class JoinPoint {
        private Location loc;
        private String raidName;

        public JoinPoint(Location loc, String raidName) {
            this.loc = loc;
            this.raidName = raidName;
        }

        public Location getLocation() { return loc; }
        public String getRaidName() { return raidName; }
    }

    public static class SelectionSession {
        private String name;
        private List<Location> points = new ArrayList<>();

        public SelectionSession(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public List<Location> getPoints() { return points; }
        public void addPoint(Location loc) { points.add(loc); }
        public int getStep() { return points.size(); }
    }
}