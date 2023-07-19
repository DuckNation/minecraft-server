package io.github.haappi.NPC;

import com.google.common.reflect.TypeToken;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static io.github.haappi.Main.gson;

public class SpawnLosers {
    public static void spawn() {
        File jsonFile = new File("npcs.json");

        if (jsonFile.exists()) {
            try {
                FileReader reader = new FileReader(jsonFile);
                Type npcListType = new TypeToken<ArrayList<NPCPojo>>() {
                }.getType();
                List<NPCPojo> npcList = gson.fromJson(reader, npcListType);

                if (npcList != null) {
                    Team team = new TeamBuilder("noNames", MinecraftServer.getTeamManager())
                            .nameTagVisibility(TeamsPacket.NameTagVisibility.ALWAYS).teamColor(NamedTextColor.GOLD).build();
                    for (NPCPojo npc : npcList) {
                        npc.create(team);
                    }
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("JSON File does not exist.");
        }


    }
}
