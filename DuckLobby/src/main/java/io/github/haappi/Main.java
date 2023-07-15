package io.github.haappi;

import com.google.gson.Gson;
import io.github.haappi.NPC.NPC;
import io.github.haappi.NPC.SpawnLosers;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.github.haappi.Utils.stringToByteArray;

public class Main {
    private static DimensionType LOBBY;
    private static final ConcurrentHashMap<UUID, Long> simpleCooldown = new ConcurrentHashMap<>();
    public static final Gson gson = new Gson();

    private static DimensionType generateDimension() {
        return DimensionType.OVERWORLD;
    }

    private static void handleStuff(Player player, Entity entity) {
        if (simpleCooldown.containsKey(player.getUuid())) {
            if (simpleCooldown.get(player.getUuid()) > System.currentTimeMillis()) {
                return;
            } else {
                simpleCooldown.remove(player.getUuid());
            }
        }
        simpleCooldown.put(player.getUuid(), System.currentTimeMillis() + 800);
        NPC npc = NPC.getNPC(entity.getEntityId());
        if (npc == null) {
            return;
        }
        player.sendPluginMessage("duck:messenger", stringToByteArray(npc.getData()));
    }


    public static void main(String[] args)  {
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        Main.LOBBY = generateDimension();
        InstanceContainer world = instanceManager.createInstanceContainer(LOBBY);

        LobbyConfig config = new LobbyConfig("lobby.properties");

        world.setChunkLoader(new AnvilLoader(config.getWorldPath()));

        System.setProperty("minestom.chunk-view-distance", String.valueOf(config.getViewDistance()));
        MinecraftServer.setBrandName(config.getServerBrand());
        MinecraftServer.setCompressionThreshold(0);

        world.setTimeRate(0);
        world.setTime(12700);

        final Pos SPAWN = new Pos(config.getSpawnX(), config.getSpawnY(), config.getSpawnZ(), config.getSpawnYaw(), config.getSpawnPitch());


        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();

            player.setGameMode(GameMode.ADVENTURE);
            event.setSpawningInstance(world);
            player.setRespawnPoint(SPAWN);

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                NPC.join(player);
            }).start();
        });
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getPosition().y() < 60) {
                player.teleport(SPAWN);
            };
        });

        globalEventHandler.addListener(EntityAttackEvent.class, event -> {
            Main.handleStuff((Player) event.getEntity(), event.getTarget());
        });

        globalEventHandler.addListener(PlayerEntityInteractEvent.class, event -> {
            Main.handleStuff(event.getPlayer(), event.getTarget());
        });

        SpawnLosers.spawn();


        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Pos pos = event.getPlayer().getPosition();
            if ((int) pos.y() != 125) {
                return;
            }
            if (world.getBlock(pos).compare(Block.LIGHT_GRAY_CARPET)) {
                event.getPlayer().scheduleNextTick(player -> player.setVelocity(player.getVelocity().lerp(Vec.fromPoint(new Pos(pos.x() + 5, pos.y() + 5, pos.z() - 2)), 0.069d)));
            }
        });

        final ParticlePacket[] particlePackets = new ParticlePacket[3];

        for (int i = 0; i < 3; i++) {
            particlePackets[i] = ParticleCreator.createParticlePacket(
                    Particle.HAPPY_VILLAGER,
                    116.5,
                    125,
                    56 + i,
                    0.35f,
                    0.25f,
                    0.35f,
                    8
            );
        }



        world.scheduler().submitTask(() -> {
            if (world.getPlayers().size() <= 3) {
                return TaskSchedule.seconds(3);
            }
            Player player = world.getPlayers().stream().filter(p -> (p instanceof FakePlayer)).limit(1).collect(Collectors.toList()).get(0);
            player.sendPacketsToViewers(
                    particlePackets
            );
            return TaskSchedule.seconds(1);

        });




        VelocityProxy.enable(config.getVelocitySecret());

        minecraftServer.start(config.getServerAddress(), config.getServerPort());
    }
}