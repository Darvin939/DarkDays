package darvin939.DarkDays.Commands.Handlers;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.PC;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Utils.Util;

public class Spawn extends Handler {

	private String[] args;
	private Player p;

	public Spawn(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		this.args = args;
		this.p = p;
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("help")) {
				getHelp(p, "spawn");
				return true;
			}
			if (args[1].equalsIgnoreCase("set")) {
				if (hasPermissions(p, "spawn.set", true))
					set();
				return true;
			}
			if (args[1].equalsIgnoreCase("list")) {
				if (hasPermissions(p, "spawn.list", true))
					list();
				return true;
			}
			Util.unknownCmd(p, getClass(), new String[] { args[1], "set", "list", "help" });
			return true;
		} else {
			if (hasPermissions(p, "spawn", true))
				if (!(boolean) Config.getPC().getData(p, PC.SPAWNED)) {
					int spawnid = 0;
					FileConfiguration cfg = plugin.getConfig();
					Random rnd = new Random();
					if (cfg.isConfigurationSection("Spawns")) {
						while (cfg.contains("Spawns.Spawn" + spawnid)) {
							spawnid++;
						}
						if (spawnid > 0) {
							int spawn = rnd.nextInt(spawnid - 1);
							double x = cfg.getDouble("Spawns.Spawn" + spawn + ".x");
							double y = cfg.getDouble("Spawns.Spawn" + spawn + ".y");
							double z = cfg.getDouble("Spawns.Spawn" + spawn + ".z");
							Location loc = new Location(p.getWorld(), x, y, z);
							Util.PrintPxMSG(p, "game_start");
							Config.getPC().setData(p, PC.SPAWNED, true);
							Config.getPC().setData(p, PC.NOVICE, false);
							p.teleport(loc);
							Config.getPC().setData(p, PC.HUNGER, 309999);
							Tasks.player_hunger.put(p, 309999);
							Tasks.player_noise.put(p, 1);
							PlayerInfo.addPlayer(p);
							p.getInventory().clear();
							// p.getInventory().addItem(getKit("Start"));
						}
					}
				} else
					Util.PrintPxMSG(p,"game_alrady");
			return true;
		}
	}

	private void set() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length == 2) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "spawn.set");
			if (nargs[1].equalsIgnoreCase("lobby"))
				if (plugin.setLocation(p, "Lobby"))
					Util.PrintPxMSG(p, "spawn_lobby_new");
				else
					Util.PrintPxMSG(p, "spawn_lobby_error");
		} else if (nargs.length == 1)
			if (plugin.setLocation(p, "Spawn"))
				Util.PrintPxMSG(p, "spawn_new");
			else
				Util.PrintPxMSG(p, "spawn_error");

	}

	private void list() {
		String[] nargs = Util.newArgs(args);
		if (nargs.length > 1) {
			if (nargs[1].equalsIgnoreCase("help"))
				getHelp(p, "spawn.list");
		} else {
			FileConfiguration cfg = plugin.getConfig();
			Util.Print(p, "&b================ &2DarkDays Spawns&b ===============");
			if (cfg.isConfigurationSection("Spawns.Lobby")) {
				double x = cfg.getDouble("Spawns.Lobby.x", 0);
				double y = cfg.getDouble("Spawns.Lobby.y", 60);
				double z = cfg.getDouble("Spawns.Lobby.z", 0);
				Util.Print(p, "&6Lobby &7(" + x + " , " + y + " , " + z + ")");
			} else
				Util.PrintPxMSG(p, "spawn_lobby_nf");

			int spawnid = 0;
			boolean spawnsfound = false;
			while (cfg.contains("Spawns.Spawn" + spawnid)) {
				spawnsfound = true;
				double x = cfg.getDouble("Spawns.Spawn" + spawnid + ".x", 0);
				double y = cfg.getDouble("Spawns.Spawn" + spawnid + ".y", 60);
				double z = cfg.getDouble("Spawns.Spawn" + spawnid + ".z", 0);
				Util.Print(p, "&6Spawn" + spawnid + " &7(" + x + " , " + y + " , " + z + ")");
				spawnid++;
			}
			if (!spawnsfound)
				Util.PrintPxMSG(p, "spawn_nf");
		}

	}

}
