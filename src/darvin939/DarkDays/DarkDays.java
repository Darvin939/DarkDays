package darvin939.DarkDays;

import haveric.stackableItems.StackableItems;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Commands.Parser;
import darvin939.DarkDays.Commands.Handlers.About;
import darvin939.DarkDays.Commands.Handlers.Chests;
import darvin939.DarkDays.Commands.Handlers.Help;
import darvin939.DarkDays.Commands.Handlers.Loot;
import darvin939.DarkDays.Commands.Handlers.Spawn;
import darvin939.DarkDays.Commands.Handlers.Status;
import darvin939.DarkDays.Commands.Handlers.Tag;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.BlockListener;
import darvin939.DarkDays.Listeners.EntityListener;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Listeners.TagAPIListener;
import darvin939.DarkDays.Listeners.Wand;
import darvin939.DarkDays.Listeners.ZombieListener;
import darvin939.DarkDays.Listeners.Noise.Noise;
import darvin939.DarkDays.Listeners.Noise.Surface;
import darvin939.DarkDays.Loadable.Effect;
import darvin939.DarkDays.Loadable.EffectManager;
import darvin939.DarkDays.Loadable.Item;
import darvin939.DarkDays.Loadable.ItemManager;
import darvin939.DarkDays.Regions.SignListener;
import darvin939.DarkDays.Utils.MetricsLite;
import darvin939.DarkDays.Utils.Util;

public class DarkDays extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile des;
	private static File datafolder;

	private PlayerListener plis = new PlayerListener(this);
	private Noise pnoise = new Noise(this);
	private EntityListener elis = new EntityListener(this);
	private BlockListener blis = new BlockListener(this);
	private Wand wlis = new Wand(this);
	private ZombieListener zlis = new ZombieListener(this);
	private TagAPIListener tlis = new TagAPIListener(this);
	//private SignListener slis = new SignListener(this);

	public Parser Commands = new Parser();
	private Config cfg;

	private static boolean sqlibrary = false;
	private static boolean tagAPI = false;

	private static EffectManager effects;
	private static ItemManager items;

	private static final String consolePfx = "[DarkDays] ";
	private static String chatPfx = "&b[DarkDays]&f ";
	private static final String premPfx = "darkdays";
	private static final String cmdPfx = "/dd ";

	public void onDisable() {
		log.info(consolePfx + "Plugin v." + des.getVersion() + " disabled");
		Config.getPC().saveAll();
		Config.getCC().saveAll();
		Config.FGU.SaveMSG();
		Config.save(new File(datafolder, "config.yml"));
	}

	public static boolean isSQLbrary() {
		return sqlibrary;
	}

	public static boolean isTagAPI() {
		return tagAPI;
	}

	public static String getConsolePfx() {
		return consolePfx;
	}

	public static String getCmdPfx() {
		return cmdPfx;
	}

	public static String getChatPfx() {
		return chatPfx;
	}

	public static void setChatPfx(String pfx) {
		chatPfx = pfx;
	}

	public static String getDataPath() {
		return datafolder.getAbsolutePath();
	}

	public static EffectManager getEffectManager() {
		return effects;
	}

	public static ItemManager getItemManager() {
		return items;
	}

	public Config getConfiguration() {
		return cfg;
	}

	public void onEnable() {
		des = getDescription();
		// LOGO
		{
			Util.CS("");
			Util.CS("$$$$$    $$$$   $$$$$   $$  $$  $$$$$    $$$$   $$  $$   $$$$ ");
			Util.CS("$$  $$  $$  $$  $$  $$  $$ $$   $$  $$  $$  $$   $$$$   $$    ");
			Util.CS("$$  $$  $$$$$$  $$$$$   $$$$    $$  $$  $$$$$$    $$     $$$$ ");
			Util.CS("$$  $$  $$  $$  $$  $$  $$ $$   $$  $$  $$  $$    $$        $$");
			Util.CS("$$$$$   $$  $$  $$  $$  $$  $$  $$$$$   $$  $$    $$     $$$$ ");
			Util.CS("");
		}

		// getLogger().info("Plugin " + des.getName() + " v" + des.getVersion()
		// + " enabled");
		if (getServer().getPluginManager().isPluginEnabled("SQLibrary")) {
			getLogger().info("Successfully hooked with SQLibrary! Now you can use the database.");
			sqlibrary = true;
		}
		if (getServer().getPluginManager().isPluginEnabled("TagAPI")) {
			getLogger().info("Successfully hooked with TagAPI!");
			tagAPI = true;
		}
		datafolder = getDataFolder();
		if (!datafolder.exists())
			datafolder.mkdir();

		PluginManager pm = getServer().getPluginManager();
		init();
		cfg = new Config(this, Nodes.verCheck.getBoolean(), Nodes.language.getString().toLowerCase(), premPfx, consolePfx);
		cfg.init();

		Config.getCC().loadChests();
		new Tasks(this);
		new Surface(getConfig());

		effects = new EffectManager(this);
		items = new ItemManager(this);
		printEffects(effects);
		printItems(items);
		setupStackableItems();
		registerEvents(pm);
		registerCommands();
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			log.info(consolePfx + "Failed to submit stats to the Metrics (mcstats.org)");
		}

	}

	public void init() {
		Config.extract(new String[] { "config.yml" });
		Config.load(new File(getDataPath(), "config.yml"));

		if (!Nodes.prefix.getString().toLowerCase().equalsIgnoreCase("darkdays")) {
			String px = "&b[" + Nodes.prefix.getString() + "]&f ";
			if (Nodes.prefix.getString().toLowerCase().equalsIgnoreCase("none"))
				px = "";
			getLogger().info("Found custom prefix [" + Nodes.prefix.getString() + "]. Use it");
			setChatPfx(px);
		}
		if (Nodes.zombie_smoothness.getInteger() > 19)
			Nodes.zombie_smoothness.setValue(19);
		else if (Nodes.zombie_smoothness.getInteger() < 1) {
			Nodes.zombie_smoothness.setValue(1);
		}
		Nodes.zombie_smoothness.setValue(20 - Nodes.zombie_smoothness.getInteger());
	}

	private void registerCommands() {
		// spawn
		Commands.add("/dd spawn", new Spawn(this));
		Commands.setPermission("spawn", "darkdays.spawn");
		Commands.setPermission("spawn.list", "darkdays.spawn.list");
		Commands.setPermission("spawn.set", "darkdays.spawn.set");
		Commands.setPermission("spawn.remove", "darkdays.spawn.remove");
		Commands.setHelp("spawn", Config.FGU.MSG("hlp_cmd_spawn"));
		Commands.setHelp("spawn.list", Config.FGU.MSG("hlp_cmd_spawn_list"));
		Commands.setHelp("spawn.set", Config.FGU.MSG("hlp_cmd_spawn_set"));
		Commands.setHelp("spawn.remove", Config.FGU.MSG("hlp_cmd_spawn_remove"));
		// status
		Commands.add("/dd status", new Status(this));
		Commands.setPermission("status", "darkdays.status");
		Commands.setHelp("status", Config.FGU.MSG("hlp_cmd_status"));
		// help
		Commands.add("/dd help", new Help(this));
		Commands.setPermission("help", "darkdays.help");
		Commands.setHelp("help", Config.FGU.MSG("hlp_cmd_help"));
		// tag
		if (tagAPI) {
			Commands.add("/dd tag", new Tag(this));
			Commands.setPermission("tag", "darkdays.tag");
			Commands.setHelp("tag", Config.FGU.MSG("hlp_cmd_tag"));
		}
		// chest
		Commands.add("/dd chest", new Chests(this));
		Commands.setPermission("chest", "darkdays.chest");
		Commands.setPermission("chest.add", "darkdays.chest.add");
		Commands.setPermission("chest.remove", "darkdays.chest.remove");
		Commands.setPermission("chest.set", "darkdays.chest.set");
		Commands.setHelp("chest", Config.FGU.MSG("hlp_cmd_chest"));
		Commands.setHelp("chest.add", Config.FGU.MSG("hlp_cmd_chest_add"));
		Commands.setHelp("chest.remove", Config.FGU.MSG("hlp_cmd_chest_remove"));
		Commands.setHelp("chest.set", Config.FGU.MSG("hlp_cmd_chest_set"));
		// loot
		Commands.add("/dd loot", new Loot(this));
		Commands.setPermission("loot", "darkdays.loot");
		Commands.setPermission("loot.new", "darkdays.loot.new");
		Commands.setPermission("loot.remove", "darkdays.loot.remove");
		Commands.setPermission("loot.list", "darkdays.loot.list");
		Commands.setPermission("loot.item", "darkdays.loot.item");
		Commands.setPermission("loot.falg", "darkdays.loot.flag");
		Commands.setPermission("loot.save", "darkdays.loot.save");
		Commands.setPermission("loot.durability", "darkdays.loot.durability");
		Commands.setHelp("loot", Config.FGU.MSG("hlp_cmd_loot"));
		Commands.setHelp("loot.new", Config.FGU.MSG("hlp_cmd_loot_new"));
		Commands.setHelp("loot.remove", Config.FGU.MSG("hlp_cmd_loot_remove"));
		Commands.setHelp("loot.list", Config.FGU.MSG("hlp_cmd_loot_list"));
		Commands.setHelp("loot.item", Config.FGU.MSG("hlp_cmd_loot_item"));
		Commands.setHelp("loot.flag", Config.FGU.MSG("hlp_cmd_loot_flag"));
		Commands.setHelp("loot.save", Config.FGU.MSG("hlp_cmd_loot_save"));
		Commands.setHelp("loot.durability", Config.FGU.MSG("hlp_cmd_loot_durability"));

		// about
		Commands.add("/dd about", new About(this));
		Commands.setHelp("about", Config.FGU.MSG("hlp_cmd_about"));
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(plis, this);
		pm.registerEvents(elis, this);
		pm.registerEvents(blis, this);
		pm.registerEvents(wlis, this);
		pm.registerEvents(zlis, this);
		pm.registerEvents(pnoise, this);
		pm.registerEvents(new SignListener(this), this);
		if (tagAPI)
			pm.registerEvents(tlis, this);
	}

	private void printItems(ItemManager items) {
		if (!items.getItems().isEmpty()) {
			String eff = "";
			for (Entry<String, Item> set : items.getItems().entrySet())
				eff = eff + set.getKey() + ", ";
			eff = eff.substring(0, eff.length() - 2);
			getLogger().info("Loaded items: " + eff);
		} else
			getLogger().info("No extra items was't found");
	}

	public void printEffects(EffectManager effect) {
		if (!effect.getEffects().isEmpty()) {
			String eff = "";
			for (Entry<String, Effect> set : effect.getEffects().entrySet())
				eff = eff + set.getKey() + ", ";
			eff = eff.substring(0, eff.length() - 2);
			getLogger().info("Loaded effects: " + eff);
		} else
			getLogger().info("No effects was't found");
	}

	public StackableItems getStackableItems() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("StackableItems");
		if (plugin == null || !(plugin instanceof StackableItems)) {
			getLogger().warning("StackableItems not found on the server");
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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String split = "/" + command.getName().toLowerCase();
		if (args.length > 0)
			split = split + " " + args[0];
		Handler handler = Commands.getHandler(split);
		if (split.equalsIgnoreCase("/dd") || args.length == 0) {
			Util.Print(sender, Config.FGU.MSG("hlp_topic", getCmdPfx() + "help"));
			return true;
		} else {

			if (handler == null) {
				Util.PrintMSGPx(sender, "cmd_unknown", getCmdPfx() + args[0]);
				Util.Print(sender, Config.FGU.MSG("hlp_commands") + " &2" + getCmdPfx() + "&7<" + Commands.getCommandsString() + "&7>");
				return true;
			}
			try {
				if (!handler.perform(sender, args))
					Util.Print(sender, Config.FGU.MSG("hlp_topic", getCmdPfx() + "help"));
				return true;
			} catch (InvalidUsage ex) {
				return false;
			}
		}
	}

	public boolean hasPermission(Player p, String command, Boolean mess) {
		if (Commands.hasPermission(command)) {
			if (p.hasPermission(Commands.getPermission(command)))
				return true;
			else {
				if (mess)
					Util.Print(p, Config.FGU.MSG("cmd_noperm", Commands.getPermission(command), 'f', '7'));
				return false;
			}
		}
		return false;
	}

	public boolean hasPermission(Player p, String perm) {
		return p.hasPermission(premPfx + "." + perm);
	}

	public void getHelp(Player p, String command) {
		Util.Print(p, "&b================= &2DarkDays Help &b=================");
		Util.Print(p, "&7Command:&6 " + getCmdPfx() + command.replaceAll("\\.", " "));
		Util.Print(p, Commands.getHelp(command));
	}
}
