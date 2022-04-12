package cc.kafuu.plugin.mark;


import cc.kafuu.plugin.mark.commands.CommandMark;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Mark extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Objects.requireNonNull(getCommand("mark")).setExecutor(new CommandMark(this));
        Objects.requireNonNull(getCommand("marks")).setExecutor(new CommandMark(this));
        Objects.requireNonNull(getCommand("gomark")).setExecutor(new CommandMark(this));
        Objects.requireNonNull(getCommand("delmark")).setExecutor(new CommandMark(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
