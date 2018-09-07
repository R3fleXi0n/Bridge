package lu.r3flexi0n.bridge.listeners;

import lu.r3flexi0n.bridge.Bridge;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onCropTrampling(PlayerInteractEvent e) {

        if (e.getAction() != Action.PHYSICAL) {
            return;
        }

        if (e.getClickedBlock().getType() != Material.SOIL) {
            return;
        }

        if (e.getClickedBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {

        if (e.getSpawnReason() == SpawnReason.CUSTOM) {
            return;
        }

        if (e.getLocation().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWheaterChange(WeatherChangeEvent e) {

        if (!e.toWeatherState()) {
            return;
        }
        if (e.getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (e.getBlock().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
        if (e.getEntity().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestoryArmorStand(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame) && !(e.getEntity() instanceof ArmorStand)) {
            return;
        }

        if (e.getEntity().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChangeArmorStand(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof ItemFrame) && !(e.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        if (e.getRightClicked().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandChange(PlayerArmorStandManipulateEvent e) {
        if (e.getRightClicked().getWorld().equals(Bridge.world)) {
            e.setCancelled(true);
        }
    }
}
