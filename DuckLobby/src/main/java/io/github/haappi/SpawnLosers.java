package io.github.haappi;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.TeamBuilder;

import java.util.UUID;

public class SpawnLosers {
    public static void spawn() {
        var team = new TeamBuilder("noNames", MinecraftServer.getTeamManager())
                .nameTagVisibility(TeamsPacket.NameTagVisibility.ALWAYS).teamColor(NamedTextColor.GOLD).build();

        FakePlayer.initPlayer(UUID.fromString("00000000-0000-0000-1234-000000000000"), "SMP",
                new FakePlayerOption().setInTabList(false).setRegistered(false),
                player -> {
                    player.setTeam(team);

                    ItemStack pickaxe = ItemStack.builder(Material.IRON_PICKAXE)
                            .meta(metaBuilder ->
                                    metaBuilder.enchantment(Enchantment.EFFICIENCY, (short) 1)
                            )
                            .build();


                    ItemStack helmet = ItemStack.builder(Material.LEATHER_HELMET)
                            .meta(metaBuilder ->
                                    metaBuilder.enchantment(Enchantment.EFFICIENCY, (short) 1)
                            )
                            .build();

                    ItemStack chestplate = ItemStack.builder(Material.CHAINMAIL_CHESTPLATE)
                            .build();

                    player.getInventory().setItemInMainHand(pickaxe);
                    player.getInventory().setHelmet(helmet);
                    player.getInventory().setChestplate(chestplate);

                    player.setSkin(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTYyOTQ4NDM3OTQ3OCwKICAicHJvZmlsZUlkIiA6ICJkMGI4MjE1OThmMTE0NzI1ODBmNmNiZTliOGUxYmU3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJqYmFydHl5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M1NzlhNjExNWVhZjMzZGE1N2FlNWQ5ZjRhNGY4Nzk5MjEyOThlMmI0M2IyYTA0ZTQzY2YyNDk2MDFiYjk0ZDAiCiAgICB9CiAgfQp9", "YwCh0k1/I40ECWEhvUPqHKk99SFR/2F5aHSj7yykCCfX8hwqW8eORKQmff3NROoIfBXBCY+MGZ0y392fWvo7NBRG6LbtO7DxqaQDRKbuTSLj8alsPi4X8rPtDt4dEK5Di/tYbU0MmgGyx+5AJyy4rirETORVx6lhSe3iP1H5jErIONSIarQS9coJnT0ZWXNEIv0C7CUOioSz7HkX7X2ny0ligDPP0rwp4mnkC6mC7itqZdszPi1dXqQqxZdzZM+VTaAjZp8A55gysPkAW5IbAce67qefsrIy1xm+KwSlgal18grxjoqF+sEr/3S+4cXGPvZwMx5t9PnraHGwwoi1WiBQdAdK2cPTK6gLZ8EsRTj7KZ2Yq8effwpInMd1E4/PwiJJuHymmyo/zInvh1XoLTYZvac5OySOdKR2VVTEa1Wtux+WnDLKigrluC0QjxyYE+LGCFXcqjHoqxwsnBRl+ccdeKWNbhR/+VvR9FIpSKfQckKzzZ0TYmm0ecGFt5bzxgIxXUS2tWZh+X5dEwvku3vWuZG55NePf9pnD0DwVODiV2DrLH5SPdur9PvblOKgqLchKByY5SrCX8/t6y1aykpfQUN9ZLjKH5HJVrw6WPxCiQ6PT5Cq7SNJ9okBdnbGDk23kMv8aY8p0e+z84fZJF2YA8RoxKpUTgqRovXFjS4="));


                    Pos position = new Pos(142.5, 122, 57.5, 90f, 0f);

                    player.teleport(position);
                    player.swingMainHand();

                    new NPC(player, position, "Server;smp");
                });


        FakePlayer.initPlayer(UUID.fromString("00000000-0000-0000-5678-000000000000"), "Events",
                new FakePlayerOption().setInTabList(false).setRegistered(false),
                player -> {
                    player.setTeam(team);

                    ItemStack pickaxe = ItemStack.builder(Material.STONE_SWORD)
                            .meta(metaBuilder ->
                                    metaBuilder.enchantment(Enchantment.SHARPNESS, (short) 1)
                            )
                            .build();

                    ItemStack block = ItemStack.of(Material.ORANGE_CONCRETE);

                    ItemStack helmet = ItemStack.builder(Material.IRON_LEGGINGS)
                            .meta(metaBuilder ->
                                    metaBuilder.enchantment(Enchantment.EFFICIENCY, (short) 1)
                            )
                            .build();


                    player.getInventory().setItemInMainHand(pickaxe);
                    player.getInventory().setItemInOffHand(block);
                    player.getInventory().setLeggings(helmet);

                    player.setSkin(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTY2OTI5NTE0MjYwNSwKICAicHJvZmlsZUlkIiA6ICIwOWQ3YmJjNGYxZmU0ZjlkYjRiZmE1MTY2NDA3OTcyYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJlemtkIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU2ZjY0NWQ2YWU2YTQzMWY2ZjY2YWNkMTUwODA5ZGM2Yzk2NjhiMzU0NDVlOGEyZGY3ZDVlYTQ4YTRiMDExNzgiCiAgICB9CiAgfQp9", "dcBBt6PKxSmw2/cIKvdT4g4UNRyEtEwPpmpT9c3QLVQaewKO+K6WD2il4ZbYmvYkkp57zl6CXRaA6HmXxFAmuqhlnS2yOUlHglBWtbRP6Zk+TAXJZuCwWYhzlVzwSLarBkJNvsIT6GNwXzNp5gYpL2SMkYNebmtHlU2pQJrPXo3S/UunQi3En0kPquMJS9ExGeqHNwKfuGq8JPRuxjuOTXXAlErSZfLKCh/RQzZapOzPtvx++Fd4duoTDua1cI5vuMRdo5Yqv4wq0XJq90hUqWzLiEiqvbJFkAcz9Yvtbn/z33YG7rCTh3fT6YV4rK134GgmzTwNrfOWfO+Q4072dzh2ULQC4KWmbQNBDrKCVxbRtFzrT6Voe5I72Eh/FhnXtUnzn3jKfTvX18u5g3wTd46W+xAvQJdxqLfsBpphuORixN/T28bomeI/aman8iWqQH7JypBy2iFBTtREmu2HUH0i8kZd+3KiDNxKJRpcDMCoFA7mIqbooO9a6HDK1xsNw1UALG7td+lPZRGMbz24Evu4hze3txatUfOXZroYSBcVk+7msgTyxCXLBtNPRO14VLr6HO0ug/JVBG/YD1oI0mISZhgkiLI7uefV8fIQdZrTb2MGY64Uqgt0an6IXj4c+QFJfJkYfV9Xa98pUMEUsoJdKP2CKbWCiIdxlJvimaQ="));

                    Pos position = new Pos(138.5, 122, 52.5, 60f, 0f);

                    player.teleport(position);
                    player.swingMainHand();

                    new NPC(player, position, "Server;terra");
                });

        FakePlayer.initPlayer(UUID.fromString("00000000-0000-0000-9123-000000000000"), "???",
                new FakePlayerOption().setInTabList(false).setRegistered(false),
                player -> {
                    player.setTeam(team);

                    ItemStack tnt = ItemStack.builder(Material.TNT)
                            .meta(metaBuilder ->
                                    metaBuilder.enchantment(Enchantment.EFFICIENCY, (short) 1)
                            )
                            .build();

                    player.getInventory().setItemInMainHand(tnt);


                    player.setSkin(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTY4OTMwMTg4NTU3NCwKICAicHJvZmlsZUlkIiA6ICJjYjYxY2U5ODc4ZWI0NDljODA5MzliNWYxNTkwMzE1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJWb2lkZWRUcmFzaDUxODUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjUzZGMwYzY5NmU3MmNiNTZkZTkwNWYwOTY3NWE5N2I2YWQwYmYyYjg1ZjNhZDE2NWYxNzE0NTg5YWU5ODZhZCIKICAgIH0KICB9Cn0=", "cXq2QA00px0dIZp9rF7fQDr69ucqg8Ngw57HMSWn0kKtgDwpPFPq55YelGVnOXJNK+qe+1qqzVL5Hy639NTkdN/DOw6jcQ8b6dmM2NgF1dt1Ew6zSGsuKIJxNAGbwFFKt3Jy4JmdIBrJ6qSRjwicGQj3qzyBZv1sw8RtAeB6Z8eDiNMqQWeoUwO8XWgtvei5CkKkgGbkiBVAFvDbADEMz0IrmfKS1Ni1nAsE0YOpEyGv6SQBXfdc0fW8EWr0zoOJLyekyU1GhDfbfZQj1uKKA6HjxXxoPPo5m8Nub9uDsD2F7Ky/yjwa3ppalYsSIq8H+J7y+sVubiJjd+6nT/azA4ydRz9xvEq6SKJ05UZoXJzPpt8ng430csTOrz/Ugpgb9A5un1ssRZx0HsOCR9x4mW3QNPb8q3UfD/MXIiKGKMbhgG2xzizTfue1/0xbRKP49xJ54QHCddVFS9AjQ72dew9ht0ZXbrJ+XeZOdgMJFu+sbb4dP0XfFf1fUV6W5P1c5U9FKZbzbs67kDgnFIfF2kZxk2yVJVxqOOxraXbEahFpF9D1Zc8vVoGLG135diSt6lrAMsLSdsptrhz59kObwvNtUz75Av0mZVRGv9HXhyUw75d/l/eJXEth49ZVRt3APeYrZR5cEXTfHMcgf94CcuUh6dD+BavKwmVTD8P2mnU="));

                    Pos position = new Pos(138.5, 122, 62.5, 120f, 0f);

                    player.teleport(position);
                    player.swingMainHand();

                    new NPC(player, position, "Message;lol im useless");
                });
    }
}
