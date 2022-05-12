package cc.kafuu.plugin.mark.commands;

import cc.kafuu.plugin.mark.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CommandMark implements CommandExecutor {
    private final JavaPlugin mJavaPlugin;

    public CommandMark(JavaPlugin plugin) {
        mJavaPlugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§4非玩家不可使用");
            return true;
        }

        File dbPath = Utils.getDBPath(mJavaPlugin, ((Player)sender).getUniqueId().toString());
        if (dbPath == null) {
            sender.sendMessage("§4无法取得数据库路径");
            return true;
        }

        try (DB db = Iq80DBFactory.factory.open(dbPath, new Options().createIfMissing(true))) {

            if (command.getName().equalsIgnoreCase("marks")) {
                if (args.length != 0) {
                    return false;
                }
                listMarks((Player) sender, db);

            } else if (command.getName().equalsIgnoreCase("mark")) {
                if (args.length == 0) {
                    listMarks((Player) sender, db);
                } else {
                    markLocation((Player) sender, args[0].toLowerCase(), db);
                }

            } else if (command.getName().equalsIgnoreCase("gomark")) {
                if (args.length != 1) {
                    return false;
                }
                goMark((Player) sender, args[0].toLowerCase(), db);

            } else if (command.getName().equalsIgnoreCase("delmark")) {
                if (args.length == 0) {
                    return false;
                }
                deleteMark((Player) sender, args, db);

            }

        } catch (Exception e) {
            mJavaPlugin.getLogger().warning(e.getMessage());
        }

        return true;
    }

    private void listMarks(final Player player, final DB db) {
        int count = 0;

        StringBuilder list = new StringBuilder();

        for (Map.Entry<byte[], byte[]> element : db) {
            if (element.getValue() == null || element.getValue().length == 0) {
                continue;
            }
            count++;
            list.append(new String(element.getKey())).append(' ');
        }

        player.sendMessage((count == 0) ? "未查询到标记点" : list.toString());
    }

    private void markLocation(final Player player, final String markName, final DB db) {
        Location location = player.getLocation();

        JsonObject worldUUIDJson = new JsonObject();
        worldUUIDJson.addProperty("mostSigBits", Objects.requireNonNull(location.getWorld()).getUID().getMostSignificantBits());
        worldUUIDJson.addProperty("leastSigBits", Objects.requireNonNull(location.getWorld()).getUID().getLeastSignificantBits());

        JsonObject locationJson = new JsonObject();
        locationJson.add("world", worldUUIDJson);
        locationJson.addProperty("x", location.getX());
        locationJson.addProperty("y", location.getY());
        locationJson.addProperty("z", location.getZ());
        locationJson.addProperty("yaw", location.getYaw());
        locationJson.addProperty("pitch", location.getPitch());

        if (db.get(markName.getBytes()) != null) {
            player.sendMessage("§4此标记名称已存在");
        } else {
            db.put(markName.getBytes(), locationJson.toString().getBytes());
            player.sendMessage("已标记位置");
        }
    }

    private void goMark(final Player player, final String markName, final DB db) {
        byte[] dataBytes = db.get(markName.getBytes());
        if (dataBytes == null) {
            player.sendMessage("§4标记点名称不存在");
            return;
        }

        JsonObject data = new Gson().fromJson(new String(dataBytes), JsonObject.class);

        World world = mJavaPlugin.getServer().getWorld(new UUID(data.getAsJsonObject("world").get("mostSigBits").getAsLong(), data.getAsJsonObject("world").get("leastSigBits").getAsLong()));

        if (world == null) {
            player.sendMessage("§4您所标记的地点所在世界不存在");
            return;
        }

        Location location = new Location(world, data.get("x").getAsDouble(), data.get("y").getAsDouble(), data.get("z").getAsDouble(), data.get("yaw").getAsFloat(), data.get("pitch").getAsFloat());

        player.sendMessage("正在传送...");
        player.teleport(location);

    }

    private void deleteMark(final Player player, final String[] args, final DB db) {
        for (String name : args) {
            byte[] nameBytes = name.toLowerCase().getBytes();
            if (db.get(nameBytes) == null) {
                player.sendMessage("§4标记点'" + name + "'不存在");
            } else {
                db.delete(nameBytes);
                player.sendMessage("已删除标记点'" + name + "'");
            }
        }
    }
}
