package io.github.haappi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.notifications.Notification;
import net.minestom.server.advancements.notifications.NotificationCenter;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.tag.Tag;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.thread.AcquirableCollection;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompoundLike;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.github.haappi.Utils.raytrace;
import static io.github.haappi.Utils.stringToByteArray;

public class Main {
    private static DimensionType LIMBO;
    private static final ConcurrentHashMap<UUID, Long> simpleCooldown = new ConcurrentHashMap<>();
    private static final Notification notification = new Notification(
            Component.text("manage to die!", NamedTextColor.LIGHT_PURPLE),
            FrameType.GOAL,
            ItemStack.of(Material.BARRIER)
    );

    private static DimensionType generateDimension() {
        DimensionTypeManager dimensionManager = MinecraftServer.getDimensionTypeManager();
        DimensionType LIMBO = DimensionType.builder(NamespaceID.from("duck:lobby"))
                .ultrawarm(false)
                .natural(false)
                .piglinSafe(false)
                .respawnAnchorSafe(false)
                .bedSafe(false)
                .raidCapable(false)
                .skylightEnabled(true)
                .ceilingEnabled(false)
                .effects("minecraft:overworld")
                .ambientLight(1.0f)
                .height(400)
                .minY(-80)
                .logicalHeight(384)
                .build();
        dimensionManager.addDimension(LIMBO);
        return LIMBO;
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
        Main.LIMBO = generateDimension();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(LIMBO);

        instanceContainer.setChunkLoader(new AnvilLoader("/home/happy/hub_world"));
        instanceContainer.setTimeRate(0);
        instanceContainer.setTime(12700);

        final Pos SPAWN = new Pos(114.5, 125, 57.5, -90f, -5f);


        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();

            player.setGameMode(GameMode.ADVENTURE);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(SPAWN);

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                NPC.join();
            }).start();
        });
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getPosition().y() < 60) {
                player.teleport(SPAWN);
                NotificationCenter.send(notification, player);
            };
        });

        globalEventHandler.addListener(EntityAttackEvent.class, event -> {
            Main.handleStuff((Player) event.getEntity(), event.getTarget());
        });

        globalEventHandler.addListener(PlayerEntityInteractEvent.class, event -> {
            Main.handleStuff(event.getPlayer(), event.getTarget());
        });

        globalEventHandler.addListener(PlayerBlockInteractEvent.class, event -> {
           event.setCancelled(false);

        });

        SpawnLosers.spawn();


        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Pos pos = event.getPlayer().getPosition();
            if ((int) pos.y() != 125) {
                return;
            }
            if (instanceContainer.getBlock(pos).compare(Block.LIGHT_GRAY_CARPET)) {
                System.out.println(pos);
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



        instanceContainer.scheduler().submitTask(() -> {
            if (instanceContainer.getPlayers().size() <= 3) {
                return TaskSchedule.seconds(3);
            }
            Player player = instanceContainer.getPlayers().stream().filter(p -> (p instanceof FakePlayer)).limit(1).collect(Collectors.toList()).get(0);
            player.sendPacketsToViewers(
                    particlePackets
            );
            return TaskSchedule.seconds(1);

        });




        VelocityProxy.enable("WeS7QypY80sT");

        minecraftServer.start("0.0.0.0", 25566);
    }
}