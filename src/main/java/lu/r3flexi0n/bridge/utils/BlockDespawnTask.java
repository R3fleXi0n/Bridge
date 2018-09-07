package lu.r3flexi0n.bridge.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockDespawnTask extends BukkitRunnable {

    private final List<DespawnBlock> blocks = new ArrayList<>();

    private final Material transformType;

    private final int transformTicks, despawnTicks;

    public BlockDespawnTask(Material transformType, int transformTicks, int despawnTicks) {
        this.transformType = transformType;
        this.transformTicks = transformTicks;
        this.despawnTicks = despawnTicks;
    }

    @Override
    public void run() {
        Iterator<DespawnBlock> it = blocks.iterator();
        while (it.hasNext()) {

            DespawnBlock despawnBlock = it.next();
            despawnBlock.addTick();
            int ticks = despawnBlock.getTicks();

            Block block = despawnBlock.getBlock();
            if (ticks == transformTicks) {
                block.setType(transformType);
            } else if (ticks == despawnTicks) {
                block.setType(Material.AIR);
                it.remove();
            }
        }
    }

    public void addBlock(DespawnBlock block) {
        blocks.add(block);
    }

    public void removeBlocks(Player player) {
        Iterator<DespawnBlock> it = blocks.iterator();
        while (it.hasNext()) {
            DespawnBlock block = it.next();
            if (block.getPlayer().equals(player)) {
                block.getBlock().setType(Material.AIR);
                it.remove();
            }
        }
    }
}
