package io.github.haappi.duckpaper;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import io.github.haappi.duckpaper.NMS.NMSEvents;
import io.github.haappi.duckpaper.chat.ChatHandler;
import io.github.haappi.duckpaper.commands.CommandHandler;
import io.github.haappi.duckpaper.fun.Fun;
import io.github.haappi.duckpaper.nametag.NameTag;
import io.github.haappi.duckpaper.utils.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;

import java.util.function.Function;

public final class DuckPaper extends JavaPlugin implements Listener {
    private static DuckPaper instance;
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static final String PLUGIN_CHANNEL = "duck:messenger";
    public static final CloseableHttpClient httpClient = HttpClients.createDefault();


    public static DuckPaper getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {


        DuckPaper.instance = this;

        new Fun(this);
        new CommandHandler(this);
        new NameTag(this);

        new ChatHandler(this);
        new NMSEvents(this);
//        new ChatEvents(this);

        getServer().getPluginManager().registerEvents(this, this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);

        Config.loadConfig(this);

        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

}
