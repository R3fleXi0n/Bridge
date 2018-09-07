package lu.r3flexi0n.bridge.listeners;

import lu.r3flexi0n.bridge.Bridge;
import lu.r3flexi0n.bridge.arena.Arena;
import lu.r3flexi0n.bridge.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!Bridge.BUNGEE) {
            return;
        }

        Player player = e.getPlayer();

        Arena arena = ArenaManager.getFreeArena();
        arena.join(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena == null) {
            return;
        }

        arena.leave();
        Bridge.despawnTask.removeBlocks(player);
    }

    @EventHandler
    public void onJoinSign(PlayerInteractEvent e) {

        if (Bridge.BUNGEE) {
            return;
        }

        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        BlockState block = e.getClickedBlock().getState();
        if (!(block instanceof Sign)) {
            return;
        }
        Sign sign = (Sign) block;

        if (!ChatColor.stripColor(sign.getLine(0)).equals(ChatColor.stripColor(Bridge.SIGN_LINE1))
                || !ChatColor.stripColor(sign.getLine(1)).equals(ChatColor.stripColor(Bridge.SIGN_LINE2))
                || !ChatColor.stripColor(sign.getLine(2)).equals(ChatColor.stripColor(Bridge.SIGN_LINE3))) {
            return;
        }

        Player player = e.getPlayer();
        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena != null) {
            return;
        }

        arena = ArenaManager.getFreeArena();
        arena.join(player);

        sign.setLine(3, Bridge.SIGN_LINE4.replace("%PLAYERS%", String.valueOf(ArenaManager.PLAYERS.size())));
        sign.update();
    }

    @EventHandler
    public void onLeaveItem(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!e.getItem().isSimilar(Bridge.leaveItem)) {
            return;
        }

        Player player = e.getPlayer();
        Arena arena = ArenaManager.PLAYERS.get(player);
        if (arena == null) {
            return;
        }

        arena.leave();
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {

        if (!e.getLine(0).equals("[Bridge]")) {
            return;
        }

        Player player = e.getPlayer();
        if (!player.hasPermission("bridge.createsign")) {
            return;
        }

        e.setLine(0, Bridge.SIGN_LINE1);
        e.setLine(1, Bridge.SIGN_LINE2);
        e.setLine(2, Bridge.SIGN_LINE3);
        e.setLine(3, Bridge.SIGN_LINE4.replace("%PLAYERS%", String.valueOf(ArenaManager.PLAYERS.size())));

        player.sendMessage(Bridge.PREFIX + Bridge.SIGN_CREATED);
    }
}
