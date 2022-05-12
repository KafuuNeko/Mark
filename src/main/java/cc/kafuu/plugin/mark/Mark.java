package cc.kafuu.plugin.mark;


import cc.kafuu.plugin.mark.commands.CommandMark;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Mark extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandMark command = new CommandMark(this);
        Objects.requireNonNull(getCommand("mark")).setExecutor(command);
        Objects.requireNonNull(getCommand("marks")).setExecutor(command);
        Objects.requireNonNull(getCommand("gomark")).setExecutor(command);
        Objects.requireNonNull(getCommand("delmark")).setExecutor(command);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
