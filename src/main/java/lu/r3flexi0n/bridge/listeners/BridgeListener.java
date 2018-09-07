package lu.r3flexi0n.bridge.listeners;

import lu.r3flexi0n.bridge.Bridge;
import lu.r3flexi0n.bridge.arena.Arena;
import lu.r3flexi0n.bridge.arena.ArenaManager;
import lu.r3flexi0n.bridge.utils.DespawnBlock;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class BridgeListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (ArenaManager.PLAYERS.get(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        Player player = e.getPlayer();
        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena == null) {
            return;
        }

        if (e.getBlockReplacedState().getType() != Material.AIR) {
            e.setCancelled(true);
            return;
        }

        if (e.getBlockAgainst().getType() == Material.BARRIER) {
            e.setCancelled(true);
            arena.respawn();
            return;
        }

        ItemStack item = player.getItemInHand();
        if (item.getAmount() <= 3) {
            item.setAmount(64);
        }

        if (arena.getCurrentTime() == -1) {
            arena.start();
        }

        Block block = e.getBlock();
        Bridge.despawnTask.addBlock(new DespawnBlock(block.getWorld(), block.getX(), block.getY(), block.getZ(), player));
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {

        int y = e.getTo().getBlockY();
        if (e.getFrom().getBlockY() < y) {
            return;
        }

        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }

        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena == null) {
            return;
        }

        if (y < arena.getDeathHeight()) {
            arena.respawn();
        }

    }

    @EventHandler
    public void onStepGoldplate(PlayerInteractEvent e) {

        if (e.getAction() != Action.PHYSICAL) {
            return;
        }

        if (e.getClickedBlock().getType() != Material.GOLD_PLATE) {
            return;
        }

        Player player = e.getPlayer();
        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena == null) {
            return;
        }

        if (arena.getCurrentTime() == -1) {
            return;
        }

        arena.win();
    }
}
