package lu.r3flexi0n.bridge.arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lu.r3flexi0n.bridge.Bridge;
import lu.r3flexi0n.bridge.utils.TimeComparator;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ArenaManager {

    public static final Map<String, Arena> ARENAS = new HashMap<>();
    public static final Map<Player, Arena> PLAYERS = new HashMap<>();

    public static Arena getFreeArena() {
        for (int x = 0; x < Integer.MAX_VALUE; x = getNext(x)) {
            for (int z = 0; z <= Math.abs(x); z = getNext(z)) {
                String id = x + ";" + z;
                Arena arena = ArenaManager.ARENAS.get(id);
                if (arena == null) {
                    try {
                        arena = new Arena(id);
                        ArenaManager.ARENAS.put(id, arena);
                        return arena;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }

                if (arena.getPlayer() == null) {
                    return arena;
                }
            }
        }
        return null;
    }

    private static int getNext(int i) {
        i *= -1;
        if (i >= 0) {
            i++;
        }
        return i;
    }

    public static void updateScoreboard() {
        updateScoreboard(getTop10());
    }

    public static void updateScoreboard(List<Arena> top10) {

        Scoreboard scoreboard = new Scoreboard();
        ScoreboardObjective objective = scoreboard.registerObjective("bridge", IScoreboardCriteria.b);
        objective.setDisplayName(Bridge.SCOREBOARD_TITLE);

        PacketPlayOutScoreboardObjective removeObjective = new PacketPlayOutScoreboardObjective(objective, 1);

        PacketPlayOutScoreboardObjective createObjective = new PacketPlayOutScoreboardObjective(objective, 0);
        PacketPlayOutScoreboardDisplayObjective displayObjective = new PacketPlayOutScoreboardDisplayObjective(1, objective);

        List<PacketPlayOutScoreboardScore> scores = new ArrayList<>();
        scores.add(getScorePacket(scoreboard, objective, Bridge.SCOREBOARD_INFO, 12));
        scores.add(getScorePacket(scoreboard, objective, "", 10));
        scores.add(getScorePacket(scoreboard, objective, " ", -1));
        scores.add(getScorePacket(scoreboard, objective, Bridge.SCOREBOARD_ONLINE.replace("%ONLINE%", String.valueOf(PLAYERS.size())), -2));

        int i = 9;
        for (Arena arenas : top10) {
            String record = String.format("%.2f", arenas.getRecord() / 1000.0F);
            String score = Bridge.SCOREBOARD_ENTRY.replace("%PLAYER%", arenas.getPlayer().getName()).replace("%TIME%", record);
            scores.add(getScorePacket(scoreboard, objective, score, i));
            i--;
        }

        for (Player players : PLAYERS.keySet()) {
            PlayerConnection connection = ((CraftPlayer) players).getHandle().playerConnection;
            connection.sendPacket(removeObjective);
            connection.sendPacket(createObjective);
            connection.sendPacket(displayObjective);
            for (PacketPlayOutScoreboardScore packets : scores) {
                connection.sendPacket(packets);
            }
        }
    }

    private static PacketPlayOutScoreboardScore getScorePacket(Scoreboard scoreboard, ScoreboardObjective objective, String display, int scoreValue) {
        ScoreboardScore score = new ScoreboardScore(scoreboard, objective, display);
        score.setScore(scoreValue);
        return new PacketPlayOutScoreboardScore(score);
    }

    public static List<Arena> getTop10() {
        List<Arena> records = new ArrayList<>();
        for (Arena arenas : PLAYERS.values()) {
            if (arenas.getRecord() != -1) {
                records.add(arenas);
            }
        }
        Collections.sort(records, new TimeComparator());

        int size = 10;
        if (records.size() < 10) {
            size = records.size();
        }

        return records.subList(0, size);
    }
}
