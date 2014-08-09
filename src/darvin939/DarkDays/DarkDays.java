package darvin939.DarkDays;

import haveric.stackableItems.StackableItems;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import darvin939.DarkDays.Utils.Debug.Debug;

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
		this.log.info("[DarkDays] Plugin v." + this.des.getVersion() + " disabled");
		Config.getPC().saveAll();
		Config.getCC().saveAll();
		Config.FGU.SaveMSG();
		Config.save(new File(datafolder, "config.yml"));
		Debug.stopLogging();
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
		return this.cfg;
	}

	public void onEnable() {
		this.des = this.getDescription();
		Util.CS("");
		Util.CS("$$$$$    $$$$   $$$$$   $$  $$  $$$$$    $$$$   $$  $$   $$$$ ");
		Util.CS("$$  $$  $$  $$  $$  $$  $$ $$   $$  $$  $$  $$   $$$$   $$    ");
		Util.CS("$$  $$  $$$$$$  $$$$$   $$$$    $$  $$  $$$$$$    $$     $$$$ ");
		Util.CS("$$  $$  $$  $$  $$  $$  $$ $$   $$  $$  $$  $$    $$        $$");
		Util.CS("$$$$$   $$  $$  $$  $$  $$  $$  $$$$$   $$  $$    $$     $$$$ ");
		Util.CS("");
		if (this.getServer().getPluginManager().isPluginEnabled("SQLibrary")) {
			this.getLogger().info("Successfully hooked with SQLibrary! Now you can use the database.");
			sqlibrary = true;
		}

		if (this.getServer().getPluginManager().isPluginEnabled("TagAPI")) {
			this.getLogger().info("Successfully hooked with TagAPI!");
			tagAPI = true;
		}

		datafolder = this.getDataFolder();
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}

		PluginManager pm = this.getServer().getPluginManager();
		this.init();
		this.cfg = new Config(this, Config.Nodes.verCheck.getBoolean().booleanValue(), Config.Nodes.language.getString().toLowerCase(), premPfx, consolePfx);
		this.cfg.init();
		Config.getCC().loadChests();
		new Tasks(this);
		new Surface(this.getConfig());
		effects = new EffectManager(this);
		items = new ItemManager(this);
		this.printEffects(effects);
		this.printItems(items);
		this.setupStackableItems();
		this.registerEvents(pm);
		this.registerCommands();
		new Debug(this.getDataFolder().getAbsolutePath());

		try {
			MetricsLite e = new MetricsLite(this);
			e.start();
		} catch (IOException var3) {
			this.log.info("[DarkDays] Failed to submit stats to the Metrics (mcstats.org)");
		}

	}

	public void init() {
		Config.extract(new String[] { "config.yml" });
		Config.load(new File(getDataPath(), "config.yml"));
		if (!Config.Nodes.prefix.getString().toLowerCase().equalsIgnoreCase("darkdays")) {
			String px = "&b[" + Config.Nodes.prefix.getString() + "]&f ";
			if (Config.Nodes.prefix.getString().toLowerCase().equalsIgnoreCase("none")) {
				px = "";
			}

			this.getLogger().info("Found custom prefix [" + Config.Nodes.prefix.getString() + "]. Use it");
			setChatPfx(px);
		}

		if (Config.Nodes.zombie_smoothness.getInteger().intValue() > 19) {
			Config.Nodes.zombie_smoothness.setValue(Integer.valueOf(19));
		} else if (Config.Nodes.zombie_smoothness.getInteger().intValue() < 1) {
			Config.Nodes.zombie_smoothness.setValue(Integer.valueOf(1));
		}

		Config.Nodes.zombie_smoothness.setValue(Integer.valueOf(20 - Config.Nodes.zombie_smoothness.getInteger().intValue()));
	}

	private void registerCommands() {
		this.Commands.add("/dd spawn", new Spawn(this));
		this.Commands.setPermission("spawn", "darkdays.spawn");
		this.Commands.setPermission("spawn.list", "darkdays.spawn.list");
		this.Commands.setPermission("spawn.set", "darkdays.spawn.set");
		this.Commands.setPermission("spawn.remove", "darkdays.spawn.remove");
		this.Commands.setHelp("spawn", Config.FGU.MSG("hlp_cmd_spawn"));
		this.Commands.setHelp("spawn.list", Config.FGU.MSG("hlp_cmd_spawn_list"));
		this.Commands.setHelp("spawn.set", Config.FGU.MSG("hlp_cmd_spawn_set"));
		this.Commands.setHelp("spawn.remove", Config.FGU.MSG("hlp_cmd_spawn_remove"));
		this.Commands.add("/dd status", new Status(this));
		this.Commands.setPermission("status", "darkdays.status");
		this.Commands.setHelp("status", Config.FGU.MSG("hlp_cmd_status"));
		this.Commands.add("/dd help", new Help(this));
		this.Commands.setPermission("help", "darkdays.help");
		this.Commands.setHelp("help", Config.FGU.MSG("hlp_cmd_help"));
		if (tagAPI) {
			this.Commands.add("/dd tag", new Tag(this));
			this.Commands.setPermission("tag", "darkdays.tag");
			this.Commands.setHelp("tag", Config.FGU.MSG("hlp_cmd_tag"));
		}

		this.Commands.add("/dd chest", new Chests(this));
		this.Commands.setPermission("chest", "darkdays.chest");
		this.Commands.setPermission("chest.add", "darkdays.chest.add");
		this.Commands.setPermission("chest.remove", "darkdays.chest.remove");
		this.Commands.setPermission("chest.set", "darkdays.chest.set");
		this.Commands.setHelp("chest", Config.FGU.MSG("hlp_cmd_chest"));
		this.Commands.setHelp("chest.add", Config.FGU.MSG("hlp_cmd_chest_add"));
		this.Commands.setHelp("chest.remove", Config.FGU.MSG("hlp_cmd_chest_remove"));
		this.Commands.setHelp("chest.set", Config.FGU.MSG("hlp_cmd_chest_set"));
		this.Commands.add("/dd loot", new Loot(this));
		this.Commands.setPermission("loot", "darkdays.loot");
		this.Commands.setPermission("loot.new", "darkdays.loot.new");
		this.Commands.setPermission("loot.remove", "darkdays.loot.remove");
		this.Commands.setPermission("loot.list", "darkdays.loot.list");
		this.Commands.setPermission("loot.item", "darkdays.loot.item");
		this.Commands.setPermission("loot.falg", "darkdays.loot.flag");
		this.Commands.setPermission("loot.save", "darkdays.loot.save");
		this.Commands.setPermission("loot.durability", "darkdays.loot.durability");
		this.Commands.setHelp("loot", Config.FGU.MSG("hlp_cmd_loot"));
		this.Commands.setHelp("loot.new", Config.FGU.MSG("hlp_cmd_loot_new"));
		this.Commands.setHelp("loot.remove", Config.FGU.MSG("hlp_cmd_loot_remove"));
		this.Commands.setHelp("loot.list", Config.FGU.MSG("hlp_cmd_loot_list"));
		this.Commands.setHelp("loot.item", Config.FGU.MSG("hlp_cmd_loot_item"));
		this.Commands.setHelp("loot.flag", Config.FGU.MSG("hlp_cmd_loot_flag"));
		this.Commands.setHelp("loot.save", Config.FGU.MSG("hlp_cmd_loot_save"));
		this.Commands.setHelp("loot.durability", Config.FGU.MSG("hlp_cmd_loot_durability"));
		this.Commands.add("/dd about", new About(this));
		this.Commands.setHelp("about", Config.FGU.MSG("hlp_cmd_about"));
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this.plis, this);
		pm.registerEvents(this.elis, this);
		pm.registerEvents(this.blis, this);
		pm.registerEvents(this.wlis, this);
		pm.registerEvents(this.zlis, this);
		pm.registerEvents(this.pnoise, this);
		pm.registerEvents(new SignListener(this), this);
		if (tagAPI) {
			pm.registerEvents(this.tlis, this);
		}

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
		if (plugin != null && plugin instanceof StackableItems) {
			return (StackableItems) plugin;
		} else {
			this.getLogger().warning("StackableItems not found on the server");
			return null;
		}
	}

	public void setupStackableItems() {
		if (this.getStackableItems() != null && Config.Nodes.control_sitemst.getBoolean().booleanValue()) {
			StackableItems plg = this.getStackableItems();
			File defaultItemsFile = new File(plg.getDataFolder() + "/defaultItems.yml");
			YamlConfiguration defaultItems = YamlConfiguration.loadConfiguration(defaultItemsFile);
			File chestItemsFile = new File(plg.getDataFolder() + "/chestItems.yml");
			YamlConfiguration chestItems = YamlConfiguration.loadConfiguration(chestItemsFile);

			try {
				defaultItems.set("ALL ITEMS MAX", Integer.valueOf(1));
				chestItems.set("ALL ITEMS MAX", Integer.valueOf(1));
				chestItems.save(chestItemsFile);
				defaultItems.save(defaultItemsFile);
			} catch (IOException var7) {
				var7.printStackTrace();
				Debug.INSTANCE.info(var7.toString());
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String split = "/" + command.getName().toLowerCase();
		if (args.length > 0) {
			split = split + " " + args[0];
		}

		Handler handler = this.Commands.getHandler(split);
		if (!split.equalsIgnoreCase("/dd") && args.length != 0) {
			if (handler == null) {
				Util.PrintMSGPx(sender, "cmd_unknown", getCmdPfx() + args[0]);
				Util.Print(sender, Config.FGU.MSG("hlp_commands") + " &2" + getCmdPfx() + "&7<" + this.Commands.getCommandsString() + "&7>");
				return true;
			} else {
				try {
					if (!handler.perform(sender, args)) {
						Util.Print(sender, Config.FGU.MSG("hlp_topic", getCmdPfx() + "help"));
					}

					return true;
				} catch (InvalidUsage var8) {
					return false;
				}
			}
		} else {
			Util.Print(sender, Config.FGU.MSG("hlp_topic", getCmdPfx() + "help"));
			return true;
		}
	}

	public boolean hasPermission(Player p, String command, Boolean mess) {
		if (this.Commands.hasPermission(command)) {
			if (p.hasPermission(this.Commands.getPermission(command))) {
				return true;
			} else {
				if (mess.booleanValue()) {
					Util.Print(p, Config.FGU.MSG("cmd_noperm", this.Commands.getPermission(command), 'f', '7'));
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public boolean hasPermission(Player p, String perm) {
		return p.hasPermission(premPfx + perm);
	}

	public void getHelp(Player p, String command) {
		Util.Print(p, "&b================= &2DarkDays Help &b=================");
		Util.Print(p, "&7Command:&6 " + getCmdPfx() + command.replaceAll("\\.", " "));
		Util.Print(p, this.Commands.getHelp(command));
	}
}
