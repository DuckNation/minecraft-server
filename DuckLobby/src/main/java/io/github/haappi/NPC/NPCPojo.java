package io.github.haappi.NPC;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.scoreboard.Team;

import java.util.UUID;

public class NPCPojo {
    private String username;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String data;
    private String skinTexture;
    private String skinSignature;

    private ItemPojo mainHand;
    private ItemPojo offHand;
    private ItemPojo helmet;
    private ItemPojo chestplate;
    private ItemPojo leggings;
    private ItemPojo boots;

    public String getUsername() {
        return username;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getData() {
        return data;
    }

    public String getSkinTexture() {
        return skinTexture;
    }

    public String getSkinSignature() {
        return skinSignature;
    }

    public ItemPojo getMainHand() {
        return mainHand;
    }

    public ItemPojo getOffHand() {
        return offHand;
    }

    public ItemPojo getHelmet() {
        return helmet;
    }

    public ItemPojo getChestplate() {
        return chestplate;
    }

    public ItemPojo getLeggings() {
        return leggings;
    }

    public ItemPojo getBoots() {
        return boots;
    }

    public void create(Team team) {
        String uuid = UUID.randomUUID().toString();
        String newUuid = "00000000" + uuid.substring(uuid.indexOf("-"));
        FakePlayer.initPlayer(UUID.fromString(newUuid), this.username,
                new FakePlayerOption().setInTabList(false).setRegistered(false),
                player -> {
                    player.setTeam(team);
                    player.setSkin(new PlayerSkin(this.skinTexture, this.skinSignature));

                    if (this.helmet != null) player.setHelmet(this.getHelmet().build());
                    if (this.chestplate != null) player.setChestplate(this.getChestplate().build());
                    if (this.leggings != null) player.setLeggings(this.getLeggings().build());
                    if (this.boots != null) player.setBoots(this.getBoots().build());
                    if (this.mainHand != null) player.getInventory().setItemInMainHand(this.getMainHand().build());
                    if (this.offHand != null) player.getInventory().setItemInOffHand(this.getOffHand().build());


                    Pos position = new Pos(this.x, this.y, this.z, this.yaw, this.pitch);
                    player.teleport(position);
                    player.swingMainHand();
                    new NPC(player, position, this.data);
                });
    }
}
