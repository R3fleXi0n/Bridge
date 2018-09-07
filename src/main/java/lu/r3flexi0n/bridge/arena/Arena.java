package lu.r3flexi0n.bridge.arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lu.r3flexi0n.bridge.Bridge;
import lu.r3flexi0n.bridge.utils.PlayerData;
import lu.r3flexi0n.bridge.utils.Utils;
import lu.r3flexi0n.schematicapi.SchematicAPI;
import lu.r3flexi0n.schematicapi.objects.Schematic;
import lu.r3flexi0n.schematicapi.utils.Region;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Arena {

    private final Location center;
    private final Location spawn;
    private final int deathHeight;

    private Player player;
    private PlayerData playerData;

    private long record = -1;

    private long currentTime = -1;

    private boolean pasted;

    public Arena(String id) throws IOException {

        String[] data = id.split(";");
        int x = Integer.parseInt(data[0]);
        int z = Integer.parseInt(data[1]);

        Schematic schematic = new Schematic(getRandomSchematic());
        Region region = schematic.getRegion();

        this.center = new Location(Bridge.world, x * (Bridge.ARENA_DISTANCE + region.getWidth()), 50, z * (Bridge.ARENA_DISTANCE + region.getLength()));

        this.spawn = schematic.getConvertedLocation("spawn", center).get(0);
        this.deathHeight = schematic.getConvertedLocation("deathheight", center).get(0).getBlockY();

        closeSpawn();
        schematic.paste(center, SchematicAPI.BLOCKS_PER_TICK, (Long t) -> {
            openSpawn();
            pasted = true;
            if (player != null) {
                Utils.sendTitle(player, "", "", 20, 20, 20);
            }
        });
    }

    public Location getCenter() {
        return center;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getDeathHeight() {
        return deathHeight;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public long getRecord() {
        return record;
    }

    public void setRecord(long record) {
        this.record = record;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    private void closeSpawn() {
        for (Block blocks : getSpawnBlocks()) {
            blocks.setType(Material.BARRIER);
        }
    }

    private void openSpawn() {
        for (Block blocks : getSpawnBlocks()) {
            if (blocks.getType() == Material.BARRIER) {
                blocks.setType(Material.AIR);
            }
        }
    }

    private List<Block> getSpawnBlocks() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(spawn.clone().add(0, -1, 0).getBlock());
        blocks.add(spawn.clone().add(0, 2, 0).getBlock());
        blocks.add(spawn.clone().add(1, 1, 0).getBlock());
        blocks.add(spawn.clone().add(0, 1, 1).getBlock());
        blocks.add(spawn.clone().add(-1, 1, 0).getBlock());
        blocks.add(spawn.clone().add(0, 1, -1).getBlock());
        return blocks;
    }

    public void join(Player player) {

        if (ArenaManager.PLAYERS.get(player) != null) {
            return;
        }

        this.player = player;

        if (!Bridge.BUNGEE) {
            this.playerData = new PlayerData(
                    player.getLocation(),
                    player.getInventory().getContents(),
                    player.getInventory().getArmorContents(),
                    player.getHealth(),
                    player.getFoodLevel(),
                    player.getLevel(),
                    player.getExp(),
                    player.getActivePotionEffects(),
                    player.getScoreboard());
        }

        if (!pasted) {
            Utils.sendTitle(player, "", Bridge.ARENA_GENERATING, 20, 1000, 20);
        }

        ArenaManager.PLAYERS.put(player, this);

        Utils.resetPlayer(player);

        player.teleport(spawn);
        setInventory();

        ArenaManager.updateScoreboard();
    }

    public void leave() {
        ArenaManager.PLAYERS.remove(player);

        Utils.resetPlayer(player);

        if (playerData != null) {
            player.teleport(playerData.getLocation());
            ItemStack[] inventory = playerData.getInventory();
            for (int i = 0; i < inventory.length; i++) {
                player.getInventory().setItem(i, inventory[i]);
            }
            player.getInventory().setArmorContents(playerData.getArmor());
            player.setHealth(playerData.getHealth());
            player.setFoodLevel(playerData.getFood());
            player.setLevel(playerData.getLevel());
            player.setExp(playerData.getExp());
            player.addPotionEffects(playerData.getEffects());
            player.setScoreboard(playerData.getScoreboard());
        }

        player = null;
        record = -1;
        currentTime = -1;

        ArenaManager.updateScoreboard();
    }

    public void start() {
        currentTime = System.currentTimeMillis();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    public void respawn() {
        currentTime = -1;
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        Bukkit.getScheduler().runTaskLater(Bridge.instance, () -> {
            player.teleport(spawn);
            player.setGameMode(GameMode.SURVIVAL);
            setInventory();
        }, 5L);
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
    }

    public void win() {

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        long difference = (System.currentTimeMillis() - currentTime);
        float seconds = difference / 1000.0F;
        String time = String.format("%.2f", seconds);

        Utils.sendBar(player, Bridge.TIME_DISPLAY.replace("%TIME%", time));

        respawn();

        boolean newBest = false;

        player.sendMessage(Bridge.PREFIX + Bridge.LEVEL_SUCCESS.replace("%TIME%", time));
        if (record == -1 || difference < record) {
            record = difference;
            player.sendMessage(Bridge.PREFIX + Bridge.NEW_BEST.replace("%TIME%", time));
            newBest = true;
        }

        List<Arena> top10 = ArenaManager.getTop10();
        if (top10.contains(this) && newBest) {
            ArenaManager.updateScoreboard(top10);
            String message = Bridge.PREFIX + Bridge.NEW_TOP.replace("%PLAYER%", player.getName()).replace("%TIME%", time);
            for (Player players : ArenaManager.PLAYERS.keySet()) {
                players.sendMessage(message);
            }
        }
    }

    private void setInventory() {
        player.getInventory().clear();
        for (int i = 0; i < 8; i++) {
            player.getInventory().setItem(i, Bridge.blockItem);
        }
        player.getInventory().setItem(8, Bridge.BUNGEE ? Bridge.blockItem : Bridge.blockItem);
    }

    private File getRandomSchematic() {
        List<File> schematics = new ArrayList<>();
        for (File files : Bridge.instance.getDataFolder().listFiles()) {
            if (files.getName().endsWith(".schematic")) {
                schematics.add(files);
            }
        }
        return schematics.get(new Random().nextInt(schematics.size()));
    }
}
