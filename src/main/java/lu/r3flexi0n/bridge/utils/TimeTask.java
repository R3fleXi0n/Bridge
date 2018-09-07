package lu.r3flexi0n.bridge.utils;

import lu.r3flexi0n.bridge.Bridge;
import lu.r3flexi0n.bridge.arena.Arena;
import lu.r3flexi0n.bridge.arena.ArenaManager;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeTask extends BukkitRunnable {

    @Override
    public void run() {

        long now = System.currentTimeMillis();

        for (Arena arenas : ArenaManager.ARENAS.values()) {
            if (arenas.getCurrentTime() != -1) {
                float time = (now - arenas.getCurrentTime()) / 1000.0F;
                String timeString = String.format("%.2f", time);
                Utils.sendBar(arenas.getPlayer(), Bridge.TIME_DISPLAY.replace("%TIME%", timeString));
            }
        }
    }
}
