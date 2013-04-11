package darvin939.DarkDays;

import haveric.stackableItems.StackableItems;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.BlockListener;
import darvin939.DarkDays.Listeners.EntityListener;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Listeners.ZombieListener;
import darvin939.DarkDays.Players.Effect;
import darvin939.DarkDays.Players.EffectManager;
import darvin939.DarkDays.Players.Item;
import darvin939.DarkDays.Players.ItemManager;
import darvin939.DarkDays.Utils.MetricsLite;

public class DarkDays extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile des;
	public static File datafolder;

	private PlayerListener plis = new PlayerListener(this);
	private EntityListener elis = new EntityListener(this);
	private BlockListener blis = new BlockListener(this);
	// private Wand wlis = new Wand(this);
	private ZombieListener zlis = new ZombieListener(this);

	private EffectManager effects;
	private ItemManager items;

	public static String prefix = "[DarkDays Beta] ";
	public static final String premPrefix = "darkdays.";
	public static final String cmdPrefix = "/dd ";

	public void onDisable() {
		log.info(prefix + "Plugin v." + des.getVersion() + " disabled");
		
		Config.getPC().saveAll();
		
		Config.save(new File(datafolder, "config.yml"));
	}

	public static String getPrefix() {
		return prefix;
	}

	public static String getDataPath() {
		return datafolder.getAbsolutePath();
	}

	public static void setPrefix(String pfx) {
		prefix = pfx;
	}

	public void onEnable() {
		des = getDescription();
		if (getServer().getPluginManager().isPluginEnabled("TagAPI")) {
			log.info(prefix + "Successfully hooked with TagAPI!");
			log.info(prefix + "Plugin " + des.getName() + " v" + des.getVersion() + " enabled");
			datafolder = getDataFolder();
			if (!datafolder.exists())
				datafolder.mkdir();
			PluginManager pm = getServer().getPluginManager();
			Config.init();
			Config cfg = new Config(this, Nodes.verCheck.getBoolean(), Nodes.language.getString(), "darkdays", getPrefix());
			cfg.initOtherConfigs();

			new Tasks(this);
			
			effects = new EffectManager(this);
			items = new ItemManager(this);
			printEffects(effects);
			printItems(items);
			setupStackableItems();
			registerEvents(pm);

			try {
				MetricsLite metrics = new MetricsLite(this);
				metrics.start();
			} catch (IOException e) {
				log.info(prefix + "Failed to submit stats to the Metrics (mcstats.org)");
			}
		} else {
			log.severe(prefix + "TagAPI not found on the server! Shutting down DarkDays...");
		}
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(plis, this);
		pm.registerEvents(elis, this);
		pm.registerEvents(blis, this);
		// pm.registerEvents(wlis, this);
		pm.registerEvents(zlis, this);
	}

	private void printItems(ItemManager items) {
		if (!items.getItems().isEmpty()) {
			String eff = "";
			for (Entry<String, Item> set : items.getItems().entrySet())
				eff = eff + set.getKey() + ", ";
			eff = eff.substring(0, eff.length() - 2);
			log.info(prefix + "Loaded items: " + eff);
		} else
			log.info(prefix + "No extra items was't found");
	}

	public void printEffects(EffectManager effect) {
		if (!effect.getEffects().isEmpty()) {
			String eff = "";
			for (Entry<String, Effect> set : effect.getEffects().entrySet())
				eff = eff + set.getKey() + ", ";
			eff = eff.substring(0, eff.length() - 2);
			log.info(prefix + "Loaded effects: " + eff);
		} else
			log.info(prefix + "No effects was't found");
	}

	public StackableItems getStackableItems() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("StackableItems");
		if (plugin == null || !(plugin instanceof StackableItems)) {
			log.warning(prefix + "StackableItems not found on the server");
			return null;
		}
		return (StackableItems) plugin;
	}

	public void setupStackableItems() {
		if (getStackableItems() != null && Nodes.control_sitemst.getBoolean()) {
			FileConfiguration defaultItems;
			File defaultItemsFile;
			FileConfiguration chestItems;
			File chestItemsFile;
			Plugin plg = getStackableItems();
			defaultItemsFile = new File(plg.getDataFolder() + "/defaultItems.yml");
			defaultItems = YamlConfiguration.loadConfiguration(defaultItemsFile);
			chestItemsFile = new File(plg.getDataFolder() + "/chestItems.yml");
			chestItems = YamlConfiguration.loadConfiguration(chestItemsFile);
			try {
				defaultItems.set("ALL ITEMS MAX", 1);
				chestItems.set("ALL ITEMS MAX", 1);
				chestItems.save(chestItemsFile);
				defaultItems.save(defaultItemsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
