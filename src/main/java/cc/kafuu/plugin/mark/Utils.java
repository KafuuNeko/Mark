package cc.kafuu.plugin.mark;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Utils {
    public static File getDataFolder(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdir()) {
                plugin.getLogger().warning("Unable to create data directory");
                return null;
            }
        }
        return dataFolder;
    }

    public static File getDBPath(JavaPlugin plugin, String uuid) {
        File root = Utils.getDataFolder(plugin);
        if (root == null) {
            return null;
        }
        return new File(root.getPath() + '/' + uuid);
    }
}
