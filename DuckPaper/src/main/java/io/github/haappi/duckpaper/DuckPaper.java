package io.github.haappi.duckpaper;

import io.github.haappi.duckpaper.chat.Handler;
import io.github.haappi.duckpaper.fun.Fun;
import io.github.haappi.duckpaper.nametag.NameTag;
import org.bukkit.plugin.java.JavaPlugin;

public final class DuckPaper extends JavaPlugin {
    private static DuckPaper instance;

    public static DuckPaper getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        DuckPaper.instance = this;

        new Fun(this);
        new NameTag(this);

        new Handler(this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
