package io.github.haappi;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.thread.Acquirable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NPC {

    private final Player player;
    private final Pos position;
    private final String data;
    private static final ConcurrentHashMap<NPC, String> NPCS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, NPC> IDMapping = new ConcurrentHashMap<>();

    public NPC(Player player, Pos position, String command) {
        this.player = player;
        this.position = position;
        this.data = command;

        NPCS.put(this, command);
        IDMapping.put(this.player.getEntityId(), this);
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getData() {
        return this.data;
    }

    private Pos getPosition() {
        return this.position;
    }

    public static NPC getNPC(Integer entityID) {
        return IDMapping.getOrDefault(entityID, null);
    }

    public static List<NPC> getNPCS() {
        ArrayList<NPC> npcs = new ArrayList<>();
        NPCS.keys().asIterator().forEachRemaining(npcs::add);
        return npcs;
    }

    public static void join() {
        for (NPC npc : NPCS.keySet()) {
            Acquirable<Player> acquirableEntity = npc.getPlayer().getAcquirable();
            acquirableEntity.sync(entity -> {
                entity.swingMainHand();
                entity.teleport(npc.getPosition());
            });
        }
    }
}
