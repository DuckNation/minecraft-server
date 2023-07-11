package io.github.haappi.duckpaper.fun;

import io.github.haappi.duckpaper.DuckPaper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/*
Ported from https://github.com/oddlama/vane/blob/ace0e15bb30060ffc43406840951cefe9ac250ca/vane-trifles/src/main/java/org/oddlama/vane/trifles/
 */
public class Fun {

    public Fun(DuckPaper plugin) {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new FastWalker(), plugin);
        manager.registerEvents(new Doors(), plugin);
        manager.registerEvents(new AntiBookBan(), plugin);
        manager.registerEvents(new AntiRaidFarm(), plugin);
        manager.registerEvents(new NightSkip(), plugin);
        manager.registerEvents(new ItsNotGay(), plugin);
        manager.registerEvents(new DeathHead(), plugin);
        manager.registerEvents(new Perms(), plugin);


    }
}
