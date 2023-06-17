package io.github.haappi.duckpaper.nametag;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class NameTag {
    public NameTag(DuckPaper plugin) {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new Pets(), plugin);
    }
}
