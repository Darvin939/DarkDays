package darvin939.DarkDays.Commands.Handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Configuration.PlayerConfig;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Utils.Util;

public class Spawn extends Handler {

	private String[] args;
	private Player p;
	private ConfigurationSection cfg = Config.getSpawnCfg().getCfg();

	public Spawn(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		this.args = args;
		if (s instanceof Player) {
			p = (Player) s;
			if (cfg.isConfigurationSection(p.getWorld().getName()))
				cfg = cfg.getConfigurationSection(p.getWorld().getName());
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("help")) {
					getHelp(p, "spawn");
					return true;
				}
				if (args[1].equalsIgnoreCase("set")) {
					if (hasPermission(p, "spawn.set", true))
						set();
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (hasPermission(p, "spawn.remove", true))
						remove();
					return true;
				}
				if (args[1].equalsIgnoreCase("list")) {
					if (hasPermission(p, "spawn.list", true))
						list();
					return true;
				}
				Util.unknownCmd(p, getClass(), new String[] { args[1], "set", "list", "help" });
				return true;
			} else {
				if (hasPermission(p, "spawn", true))
					if (!(boolean) Config.getPC().getData(p, PlayerConfig.SPAWNED)) {
						int spawnid = 0;
						// FileConfiguration cfg =
						// Config.getSpawnCfg().getCfg();
						Random rnd = new Random();
						formatConig();
						while (cfg.contains("Spawn" + spawnid)) {
							spawnid++;
						}
						if (spawnid > 0) {
							int spawn = 0;
							if (spawnid > 1)
								spawn = rnd.nextInt(spawnid - 1);
							double x = cfg.getDouble("Spawn" + spawn + ".x");
							double y = cfg.getDouble("Spawn" + spawn + ".y");
							double z = cfg.getDouble("Spawn" + spawn + ".z");
							Location loc = new Location(p.getWorld(), x, y, z);
							Util.PrintMSGPx(p, "game_start");
							Config.getPC().setData(p, PlayerConfig.SPAWNED, true);
							Config.getPC().setData(p, PlayerConfig.NOVICE, false);
							p.teleport(loc);
							Config.getPC().setData(p, PlayerConfig.HUNGER, 309999);
							Tasks.player_thirst.put(p, 309999);
							Tasks.player_noise.put(p, 1.0);
							PlayerInfo.addPlayer(p);
							p.getInventory().clear();
							
							if (Nodes.spawn_withBlindness.getBoolean())
								p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
							// kit
							if (Config.getLC().getCfg().isConfigurationSection("Kit"))
								p.getInventory().addItem(LootManager.getContents("Kit"));

						}

					} else
						Util.PrintMSGPx(p, "game_alrady");
			}
			return true;
		}
		s.sendMessage("You must be a Player to do this");
		return true;
	}

	private void remove() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length == 2) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "spawn.set");
			else if (Config.getSpawnCfg().removeSpawn(p.getWorld(), nargs[1])) {
				formatConig();
				Util.PrintMSG(p, "spawn_remove_success", Util.FCTU(nargs[1].toLowerCase()));
			} else
				Util.PrintMSG(p, "spawn_remove_fail", Util.FCTU(nargs[1].toLowerCase()));

		} else
			getHelp(p, "spawn.remove");
	}

	private void formatConig() {
		List<Location> spawns = new ArrayList<Location>();
		for (String s : cfg.getKeys(false)) {
			if (s.startsWith("Spawn")) {
				spawns.add(new Location(p.getWorld(), cfg.getDouble(s + ".x"), cfg.getDouble(s + ".y"), cfg.getDouble(s + ".z")));
				Config.getSpawnCfg().removeSpawn(p.getWorld(), s);
			}
		}
		for (int i = 0; i < spawns.size(); i++) {
			Config.getSpawnCfg().addSpawn(spawns.get(i), "Spawn");
		}
	}

	private void set() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length == 2) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "spawn.set");
			if (nargs[1].equalsIgnoreCase("lobby"))
				Config.getSpawnCfg().addLobby(p);
		} else if (nargs.length == 1)
			Config.getSpawnCfg().addSpawn(p);
	}

	private void list() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "spawn.list");
		} else {
			// FileConfiguration cfg = Config.getSpawnCfg().getCfg();
			Util.Print(p, "&b================ &2DarkDays Spawns&b ===============");
			if (cfg.isConfigurationSection("Lobby")) {
				double x = cfg.getDouble("Lobby.x", 0);
				double y = cfg.getDouble("Lobby.y", 60);
				double z = cfg.getDouble("Lobby.z", 0);
				Util.Print(p, "&6Lobby &7(" + x + " , " + y + " , " + z + ")");
			} else
				Util.PrintMSGPx(p, "spawn_lobby_nf");
			formatConig();
			int spawnid = 0;
			boolean spawnsfound = false;
			while (cfg.contains("Spawn" + spawnid)) {
				spawnsfound = true;
				double x = cfg.getDouble("Spawn" + spawnid + ".x");
				double y = cfg.getDouble("Spawn" + spawnid + ".y");
				double z = cfg.getDouble("Spawn" + spawnid + ".z");
				Util.Print(p, "&6Spawn" + spawnid + " &7(" + x + " , " + y + " , " + z + ")");
				spawnid++;
			}
			if (!spawnsfound)
				Util.PrintMSGPx(p, "spawn_nf");
		}

	}

}
