package io.github.haappi.duckpaper.utils;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;

public class CommandRelated {
    public static Boolean registerNewCommand(Command command) {
        return ((CraftServer) Bukkit.getServer()).getCommandMap().register("duck", command);
    }
}
