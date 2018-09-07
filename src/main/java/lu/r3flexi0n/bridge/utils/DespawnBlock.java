package lu.r3flexi0n.bridge.utils;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DespawnBlock {

    private final World world;
    private final int x;
    private final int y;
    private final int z;

    private final Player player;

    private int ticks = 0;

    public DespawnBlock(World world, int x, int y, int z, Player player) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTicks() {
        return ticks;
    }

    public void addTick() {
        ticks++;
    }

    public Block getBlock() {
        return world.getBlockAt(x, y, z);
    }
}
