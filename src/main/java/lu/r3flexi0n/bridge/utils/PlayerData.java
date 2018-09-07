package lu.r3flexi0n.bridge.utils;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {

    private final Location location;

    private final ItemStack[] inventory, armor;

    private final double health;

    private final int food, level;

    private final float exp;

    private final Collection<PotionEffect> effects;

    private final Scoreboard scoreboard;

    public PlayerData(Location location, ItemStack[] inventory, ItemStack[] armor, double health, int food, int level, float exp, Collection<PotionEffect> effects, Scoreboard scoreboard) {
        this.location = location;
        this.inventory = inventory;
        this.armor = armor;
        this.health = health;
        this.food = food;
        this.level = level;
        this.exp = exp;
        this.effects = effects;
        this.scoreboard = scoreboard;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public double getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public int getLevel() {
        return level;
    }

    public float getExp() {
        return exp;
    }

    public Collection<PotionEffect> getEffects() {
        return effects;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
