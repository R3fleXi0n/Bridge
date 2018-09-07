package lu.r3flexi0n.bridge;

import java.io.File;
import lu.r3flexi0n.bridge.arena.Arena;
import lu.r3flexi0n.bridge.arena.ArenaManager;
import lu.r3flexi0n.bridge.listeners.BridgeListener;
import lu.r3flexi0n.bridge.listeners.JoinQuitListener;
import lu.r3flexi0n.bridge.listeners.OtherListener;
import lu.r3flexi0n.bridge.listeners.WorldListener;
import lu.r3flexi0n.bridge.utils.BlockDespawnTask;
import lu.r3flexi0n.bridge.utils.TimeTask;
import lu.r3flexi0n.bridge.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Bridge extends JavaPlugin {

    public static Bridge instance;

    public static String WORLD_NAME;
    public static int ARENA_DISTANCE;
    public static boolean BUNGEE;

    public static World world;

    public static BlockDespawnTask despawnTask;

    public static ItemStack blockItem;
    public static ItemStack leaveItem;

    @Override
    public void onEnable() {
        instance = this;

        createConfig();
        loadConfig();

        loadListeners();

        world = getServer().getWorld(WORLD_NAME);
        if (world == null) {

            File oldWorld = new File(getServer().getWorldContainer(), WORLD_NAME);
            if (oldWorld.exists()) {
                Utils.deleteFolder(oldWorld);
            }

            world = WorldCreator.name(WORLD_NAME)
                    .environment(Environment.NORMAL)
                    .type(WorldType.FLAT)
                    .generatorSettings("3;minecraft:air;127;decoration")
                    .generateStructures(false).createWorld();
            world.setTime(6000);
            world.setGameRuleValue("doDaylightCycle", "false");
        }

        despawnTask = new BlockDespawnTask(Material.REDSTONE_BLOCK, 20, 40);
        despawnTask.runTaskTimer(this, 0L, 1L);

        new TimeTask().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        for (Arena arenas : ArenaManager.PLAYERS.values()) {
            arenas.leave();
        }
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new BridgeListener(), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
        getServer().getPluginManager().registerEvents(new OtherListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    private void createConfig() {

        getConfig().addDefault("Settings.WorldName", "Bridge");
        getConfig().addDefault("Settings.ArenaDistance", 50);
        getConfig().addDefault("Settings.BlockItemID", 24);
        getConfig().addDefault("Settings.LeaveItemID", 341);
        getConfig().addDefault("Settings.Bungee", false);

        getConfig().addDefault("Language.Prefix", "&8[&4Bridge&8] &7");
        getConfig().addDefault("Language.LevelSuccess", "&7Du hast das Level in &4%TIME%&7s geschafft.");
        getConfig().addDefault("Language.NewBest", "&7Du hast eine neue Bestzeit&8: &4%TIME%&7s");
        getConfig().addDefault("Language.TimeDisplay", "&7&lZeit&8&l: &4&l%TIME%");
        getConfig().addDefault("Language.NewTop", "&7Neue Top10 Zeit&8: &4%PLAYER%&8: &4%TIME%&7s");
        getConfig().addDefault("Language.ScoreboardTitle", " &8➹ &7Bridge &8➷");
        getConfig().addDefault("Language.ScoreboardInfo", "&4YourServer.com");
        getConfig().addDefault("Language.ScoreboardOnline", "&7Spieler online&8: &4%ONLINE%");
        getConfig().addDefault("Language.ScoreboardEntry", "&7%PLAYER%&8: &4%TIME%");
        getConfig().addDefault("Language.ArenaGenerating", "&7Die Arena wird generiert...");
        getConfig().addDefault("Language.SignLine1", "&l[&rBridge&l]");
        getConfig().addDefault("Language.SignLine2", "&2&lJoin");
        getConfig().addDefault("Language.SignLine3", "");
        getConfig().addDefault("Language.SignLine4", "&2&l%PLAYERS% &rSpieler");
        getConfig().addDefault("Language.SignCreated", "&7Du hast ein Schild erstellt.");
        getConfig().addDefault("Language.LeaveItem", "&aVerlassen");

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadConfig() {

        WORLD_NAME = getConfig().getString("Settings.WorldName");
        ARENA_DISTANCE = getConfig().getInt("Settings.ArenaDistance");
        BUNGEE = getConfig().getBoolean("Settings.Bungee");

        PREFIX = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.Prefix"));
        TIME_DISPLAY = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.TimeDisplay"));
        LEVEL_SUCCESS = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.LevelSuccess"));
        NEW_BEST = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.NewBest"));
        NEW_TOP = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.NewTop"));
        SCOREBOARD_TITLE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.ScoreboardTitle"));
        SCOREBOARD_INFO = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.ScoreboardInfo"));
        SCOREBOARD_ONLINE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.ScoreboardOnline"));
        SCOREBOARD_ENTRY = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.ScoreboardEntry"));
        ARENA_GENERATING = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.ArenaGenerating"));
        SIGN_LINE1 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.SignLine1"));
        SIGN_LINE2 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.SignLine2"));
        SIGN_LINE3 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.SignLine3"));
        SIGN_LINE4 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.SignLine4"));
        SIGN_CREATED = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.SignCreated"));

        int blockID = getConfig().getInt("Settings.BlockItemID");
        blockItem = new ItemStack(blockID, 64);

        int leaveID = getConfig().getInt("Settings.LeaveItemID");
        String name = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Language.LeaveItem"));
        leaveItem = new ItemStack(leaveID);
        ItemMeta meta = leaveItem.getItemMeta();
        meta.setDisplayName(name);
        leaveItem.setItemMeta(meta);
    }

    public static String PREFIX;
    public static String LEVEL_SUCCESS;
    public static String NEW_BEST;
    public static String TIME_DISPLAY;
    public static String NEW_TOP;
    public static String SCOREBOARD_TITLE;
    public static String SCOREBOARD_INFO;
    public static String SCOREBOARD_ONLINE;
    public static String SCOREBOARD_ENTRY;
    public static String ARENA_GENERATING;
    public static String SIGN_LINE1;
    public static String SIGN_LINE2;
    public static String SIGN_LINE3;
    public static String SIGN_LINE4;
    public static String SIGN_CREATED;
}
