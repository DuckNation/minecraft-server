package io.github.haappi.ducksmp.Listeners;

import com.google.common.io.ByteStreams;
import io.github.haappi.ducksmp.DuckSMP;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class ChatFilter implements Listener {

    private final DuckSMP plugin;
    private static final HashSet<String> blockedWords = new HashSet<>();

    public ChatFilter() {
        // https://github.com/Flo0/WorkloadDistribution/tree/master/src/main/java/com/gestankbratwurst/scheduling/workloaddistribution
        // take a look at that later to evenly distribute the workload
        plugin = DuckSMP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        @Nullable InputStream file = plugin.getResource("banned_words.txt");
        StringBuilder content = new StringBuilder();
        if (file != null) {
            content = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    content.append(line.toLowerCase()).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        blockedWords.addAll(Arrays.asList(content.toString().split("\n")));
        plugin.getLogger().info("Loaded " + blockedWords.size() + " blocked words.");
        blockedWords.add("fuck");
    }

    public static String filterMessage(String message) {
        StringBuilder sb = new StringBuilder();
        for (String word : message.split(" ")) {
            if (blockedWords.contains(word.toLowerCase())) {
                sb.append("*".repeat(word.length())).append(" ");
            } else {
                sb.append(word).append(" ");
            }
        }
        return sb.toString();
    }

    public static String filterMessage(Component component) {
        String message = PaperAdventure.asPlain(component, Locale.UK);
        return filterMessage(message);
    }

    public static String filterMessage(Component component, Locale locale) {
        String message = PaperAdventure.asPlain(component, locale);
        return filterMessage(message);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessage(AsyncChatEvent event) {
        event.message(Component.text(filterMessage(event.message(), event.getPlayer().locale())));
    }
}
