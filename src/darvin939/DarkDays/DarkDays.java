package darvin939.DarkDays;

import haveric.stackableItems.StackableItems;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import darvin939.DarkDays.Commands.Handlers.Chests;
import darvin939.DarkDays.Commands.Handlers.Help;
import darvin939.DarkDays.Commands.Handlers.Spawn;
import darvin939.DarkDays.Commands.Handlers.Status;
import darvin939.DarkDays.Commands.Handlers.Tag;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.BlockListener;
import darvin939.DarkDays.Listeners.EntityListener;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Listeners.Wand;
import darvin939.DarkDays.Listeners.ZombieListener;
import darvin939.DarkDays.Players.Effect;
import darvin939.DarkDays.Players.EffectManager;
import darvin939.DarkDays.Players.Item;
import darvin939.DarkDays.Players.ItemManager;
import darvin939.DarkDays.Utils.MetricsLite;
import darvin939.DarkDays.Utils.Util;

public class DarkDays extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile des;
	public static File datafolder;
	private FileConfiguration config;

	private PlayerListener plis = new PlayerListener(this);
	private EntityListener elis = new EntityListener(this);
	private BlockListener blis = new BlockListener(this);
	private Wand wlis = new Wand(this);
	private ZombieListener zlis = new ZombieListener(this);

	public Parser Commands = new Parser();

	private EffectManager effects;
	private ItemManager items;

	public static String prefix = "&b[DarkDays]&f ";
	public static final String premPrefix = "darkdays.";
	public static final String cmdPrefix = "/dd ";

	public void onDisable() {
		log.info(prefix + "Plugin v." + des.getVersion() + " disabled");

		Config.getPC().saveAll();
		Config.getCC().saveAll();
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
			getLogger().info("Successfully hooked with TagAPI!");
			getLogger().info("Plugin " + des.getName() + " v" + des.getVersion() + " enabled");
			datafolder = getDataFolder();
			if (!datafolder.exists())
				datafolder.mkdir();
			config = getConfig();
			PluginManager pm = getServer().getPluginManager();
			init();
			Config cfg = new Config(this, Nodes.verCheck.getBoolean(), Nodes.language.getString(), "darkdays", getPrefix());
			cfg.initOtherConfigs();

			Config.getCC().loadChests();
			new Tasks(this);

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
				log.info(prefix + "Failed to submit stats to the Metrics (mcstats.org)");
			}
		} else {
			log.severe(prefix + "TagAPI not found on the server! Shutting down DarkDays...");
		}
	}

	public void init() {
		Config.extract(new String[] { "config.yml" });
		Config.load(new File(getDataPath(), "config.yml"));

		if (!Nodes.prefix.getString().toLowerCase().equalsIgnoreCase("darkdays")) {
			String px = "&b[" + Nodes.prefix.getString() + "]&f ";
			getLogger().info("Found custom prefix [" + Nodes.prefix.getString() + "]. Use it");
			setPrefix(px);
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
		Commands.setHelp("spawn", Config.FGU.MSG("hlp_cmd_spawn"));
		Commands.setHelp("spawn.list", Config.FGU.MSG("hlp_cmd_spawn_list"));
		Commands.setHelp("spawn.set", Config.FGU.MSG("hlp_cmd_spawn_set"));
		// status
		Commands.add("/dd status", new Status(this));
		Commands.setPermission("status", "darkdays.status");
		Commands.setHelp("status", Config.FGU.MSG("hlp_cmd_status"));
		// help
		Commands.add("/dd help", new Help(this));
		Commands.setPermission("help", "darkdays.help");
		Commands.setHelp("help", Config.FGU.MSG("hlp_cmd_help"));
		// tag
		Commands.add("/dd tag", new Tag(this));
		Commands.setPermission("tag", "darkdays.tag");
		Commands.setHelp("tag", Config.FGU.MSG("hlp_cmd_tag"));
		// chest
		Commands.add("/dd chest", new Chests(this));
		Commands.setPermission("chest", "darkdays.chest");
		Commands.setPermission("chest.add", "darkdays.chest.add");
		Commands.setPermission("chest.remove", "darkdays.chest.remove");
		Commands.setPermission("chest.loot", "darkdays.chest.loot");
		Commands.setHelp("chest", Config.FGU.MSG("hlp_cmd_chest"));
		Commands.setHelp("chest.add", Config.FGU.MSG("hlp_cmd_chest_add"));
		Commands.setHelp("chest.remove", Config.FGU.MSG("hlp_cmd_chest_remove"));
		Commands.setHelp("chest.loot", Config.FGU.MSG("hlp_cmd_chest_loot"));
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(plis, this);
		pm.registerEvents(elis, this);
		pm.registerEvents(blis, this);
		pm.registerEvents(wlis, this);
		pm.registerEvents(zlis, this);
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

	public boolean setLocation(Player p, String type) {
		if (new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ()).getBlock().getType() != Material.AIR) {
			Location loc = p.getLocation();
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY();
			double z = loc.getBlockZ() + 0.5;
			if (type.equalsIgnoreCase("lobby")) {
				String section = "Spawns." + Util.FCTU(type);
				if (!config.isConfigurationSection(section))
					config.createSection(section);
				config.set(section + ".x", x);
				config.set(section + ".y", y);
				config.set(section + ".z", z);
				saveConfig();
				return true;
			} else {
				int spawnid = 0;
				int spawnidx = -1;
				while (config.contains("Spawns.Spawn" + spawnid)) {
					spawnidx = spawnid;
					spawnid++;
				}
				spawnidx = spawnidx + 1;
				String section = "Spawns.Spawn" + spawnidx;
				config.createSection(section);
				config.set(section + ".x", x);
				config.set(section + ".y", y);
				config.set(section + ".z", z);
				saveConfig();
				return true;
			}
		}
		return false;
	}

	/* Util.unknownCmd() */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String split = "/" + command.getName().toLowerCase();
		if (args.length > 0)
			split = split + " " + args[0];
		Handler handler = Commands.getHandler(split);
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (split.equalsIgnoreCase("/dd") || args.length == 0) {
				Config.FGU.PrintPxMsg(p, Config.FGU.MSG("hlp_topic", DarkDays.cmdPrefix + "help"));
				return true;
			} else {

				if (handler == null) {
					Config.FGU.PrintMsg(p, Config.FGU.MSG("cmd_unknown", DarkDays.cmdPrefix + args[0]));
					Util.msg(p, Config.FGU.MSG("hlp_commands") + " &2" + DarkDays.cmdPrefix + "&7<" + Commands.getCommandsString() + "&7>", '/');
					return true;
				}
				try {
					return handler.perform(p, args);
				} catch (InvalidUsage ex) {
					return false;
				}
			}

		}
		Config.FGU.SC("You must be a Player");
		return true;
	}

	public boolean hasPermissions(Player p, String command, Boolean mess) {
		if (Commands.hasPermission(command)) {
			if (p.hasPermission(Commands.getPermission(command)))
				return true;
			else {
				if (mess)
					Config.FGU.PrintMsg(p, Config.FGU.MSG("cmd_noperm", Commands.getPermission(command), 'f', '7'));
				return false;
			}
		}
		return false;
	}

	public boolean hasPermissions(Player p, String perm) {
		return p.hasPermission(DarkDays.premPrefix + perm);
	}

	public void getHelp(Player p, String command) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b================= &2DarkDays Help &b================="));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Command:&6 " + DarkDays.cmdPrefix + command.replaceAll("\\.", " ")));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', Commands.getHelp(command)));
	}
}
