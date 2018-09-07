package lu.r3flexi0n.bridge.utils;

import java.util.Comparator;
import lu.r3flexi0n.bridge.arena.Arena;

public class TimeComparator implements Comparator<Arena> {

    @Override
    public int compare(Arena arena1, Arena arena2) {
        return arena1.getRecord() < arena2.getRecord() ? -1 : arena1.getRecord() == arena2.getRecord() ? 0 : 1;
    }

}
