package io.github.haappi.ducksmp.Commands;

import com.mongodb.client.MongoCollection;
import io.github.haappi.ducksmp.DuckSMP;
import io.github.haappi.ducksmp.Listeners.TotemHandler;
import io.github.haappi.ducksmp.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static io.github.haappi.ducksmp.Internals.Messages.insertEmptyDocumentIfNeeded;
import static io.github.haappi.ducksmp.Listeners.TotemHandler.mobs;

public class ChangeMob extends BukkitCommand {

    public ChangeMob(String name) {
        super(name);
        setAliases(List.of("cm"));
        setDescription("Change the mob type of a totem");
        setUsage("/cm [mob]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need to be an OP to use this command.");
            return true;
        }
        if (args.length != 1) {
            TotemHandler.randomMob = mobs.get(Utils.random.nextInt(mobs.size()));
        } else {
            try {
                TotemHandler.randomMob = EntityType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text("Invalid mob type.", NamedTextColor.RED));
                return true;
            }
        }
        sender.sendMessage(Component.text("Random mob set to ", NamedTextColor.GOLD)
                .append(Component.text(TotemHandler.randomMob.name(), NamedTextColor.AQUA)));

        updateInDiscord();

        return true;
    }

    public static void updateInDiscord() {
        insertEmptyDocumentIfNeeded();

        MongoCollection<Document> collection = DuckSMP.getMongoClient().getDatabase("duckMinecraft").getCollection("messages");
        Document doc = new Document();
        doc.put("type", "change_mob");
        doc.put("message", "The random totem mob is now: " + TotemHandler.randomMob.name());
        doc.put("mobName", TotemHandler.randomMob.name());
        doc.put("bound", "clientbound");
        doc.put("ack", 0);

        collection.insertOne(doc);
    }
}
