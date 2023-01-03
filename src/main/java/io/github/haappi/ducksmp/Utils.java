package io.github.haappi.ducksmp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static void easyPublish(Plugin plugin, Jedis instance, String channel, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            instance.publish(channel, message);
        });
    }

    public static void easyPublish(Jedis instance, String message) {
        easyPublish(DuckSMP.getInstance(), instance, "discord", message);
    }

    public static void easyPublish(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(DuckSMP.getInstance(), () -> {
            try (Jedis jedis = DuckSMP.getInstance().getJedisPool().getResource()) {
                jedis.auth(DuckSMP.getInstance().getConfig().getString("redisPassword"));
                jedis.publish("discord", message);
            }
        });
    }


    public static Boolean registerNewCommand(Command command) {
        return ((CraftServer) Bukkit.getServer()).getCommandMap().register("duck", command);
    }

    @SuppressWarnings("SameParameterValue")
    private static Object getPrivateField(Object object, String field) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public static void unRegisterBukkitCommand(org.bukkit.command.Command command) {
        try {
            Object result = getPrivateField(Bukkit.getServer(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            HashMap<String, Command> thing = (HashMap<String, Command>) commandMap.getKnownCommands();
            thing.remove(command.getName());
            for (String alias : command.getAliases()) {
                if (thing.containsKey(alias) && thing.get(alias).toString().contains(Bukkit.getName())) {
                    thing.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unRegisterBukkitCommand(String command) {
        Command cmd = (((CraftServer) Bukkit.getServer()).getCommandMap()).getCommand(command);
        Map<String, Command> knownCommands = new HashMap<>(((CraftServer) Bukkit.getServer()).getCommandMap().getKnownCommands());
        knownCommands.forEach((s, command1) -> {
            if (s.contains(Bukkit.getName())) {
                Bukkit.getServer().getCommandMap().getKnownCommands().remove(s);
                unRegisterBukkitCommand(command1);
                command1.unregister(((CraftServer) Bukkit.getServer()).getCommandMap());
            }
        });
        if (cmd != null) {
            unRegisterBukkitCommand(cmd);
            cmd.unregister(((CraftServer) Bukkit.getServer()).getCommandMap());
        }
    }
}
