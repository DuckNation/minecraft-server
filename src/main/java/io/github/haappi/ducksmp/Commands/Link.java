package io.github.haappi.ducksmp.Commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import io.github.haappi.ducksmp.DuckSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Link extends BukkitCommand {

    private final DuckSMP plugin;

    public Link(String name) {
        super(name);
        this.plugin = DuckSMP.getInstance();
    }

    public static @NotNull Document getStatsFromDatabase(@NotNull UUID uuid) {
        String s = uuid.toString().replaceAll("-", "").toLowerCase();
        Document result = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData").find(new Document("_id", s)).first();
        if (result == null) {
            result = new Document();
            result.put("_id", s);
            DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData").insertOne(result);
        }
        return result;
    }

    public static void saveStuffIntoDB(@NotNull UUID uuid, @NotNull Document doc) {
        String s = uuid.toString().replaceAll("-", "").toLowerCase();
        DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("playerData").updateOne(
                Filters.eq("_id", s),
                new Document("$set", doc),
                new UpdateOptions().upsert(true)
        );
    }

    public static void setPDCLink(Player player, byte b) {
        Bukkit.getScheduler().runTask(DuckSMP.getInstance(), () -> player.getPersistentDataContainer().set(new NamespacedKey(DuckSMP.getInstance(), "linked"), PersistentDataType.BYTE, b));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int random = (int) (Math.random() * 9000) + 1000; // generate a 4 digit random number
                Document doc = getStatsFromDatabase(player.getUniqueId());
                if (doc.get("discordID") == null) {
                    setPDCLink(player, (byte) 0);
                    player.sendMessage(Component.text("Your link code is: ", NamedTextColor.AQUA).append(Component.text(String.valueOf(random), NamedTextColor.YELLOW)));
                    player.sendMessage(Component.text("DM this code to", NamedTextColor.AQUA).append(Component.text(" Duck Boot#0576", NamedTextColor.YELLOW)));
                    saveStuffIntoDB(player.getUniqueId(), new Document("pinCode", String.valueOf(random)));
                } else {
                    player.sendMessage(Component.text("You are already linked to a Discord account! Run ", NamedTextColor.RED).append(Component.text("/unlink", NamedTextColor.YELLOW).append(Component.text(" to unlink.", NamedTextColor.RED))));
                    setPDCLink(player, (byte) 1);
                }
            });

        }
        return true;
    }
}
