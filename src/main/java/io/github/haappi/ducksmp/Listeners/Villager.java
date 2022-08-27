package io.github.haappi.ducksmp.Listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Villager implements Listener {
    private final DuckSMP plugin;

    public Villager() {
        this.plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    // todo change requirements for villager breeding
}
