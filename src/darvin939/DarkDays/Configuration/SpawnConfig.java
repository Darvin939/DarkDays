package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Util;

public class SpawnConfig {
	private FileConfiguration cfgSpawn;
	private File cfgSpawnFile;

	public SpawnConfig(DarkDays plg) {
		cfgSpawnFile = new File(plg.getDataFolder() + "/spawns.yml");
		cfgSpawn = YamlConfiguration.loadConfiguration(cfgSpawnFile);
		saveConfig();
	}

	public void saveConfig() {
		try {
			cfgSpawn.save(cfgSpawnFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getCfg() {
		return cfgSpawn;
	}

	public void addSpawn(Location loc, String type) {
		add(loc, type);
	}

	public void addSpawn(Player p) {
		if (add(p.getLocation(), "Spawn"))
			Util.PrintMSGPx(p, "spawn_new");
		else
			Util.PrintMSGPx(p, "spawn_error");
	}

	public void addLobby(Player p) {
		if (add(p.getLocation(), "Lobby"))
			Util.PrintMSGPx(p, "spawn_lobby_new");
		else
			Util.PrintMSGPx(p, "spawn_lobby_error");
	}

	public Location getSpawnLoc(Player p) {
		double x, y, z;
		String section = p.getWorld().getName() + ".Lobby";
		if (cfgSpawn.isConfigurationSection(section)) {
			x = cfgSpawn.getDouble(section + ".x");
			y = cfgSpawn.getDouble(section + ".y");
			z = cfgSpawn.getDouble(section + ".z");
			Location loc = new Location(p.getWorld(), x, y, z);
			return loc;
		}
		return p.getWorld().getSpawnLocation();
	}

	private boolean add(Location loc, String type) {
		if (new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).getBlock().getType() != Material.AIR) {
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY();
			double z = loc.getBlockZ() + 0.5;
			String world = loc.getWorld().getName();

			if (!cfgSpawn.isConfigurationSection(world))
				cfgSpawn.createSection(world);

			if (type.equalsIgnoreCase("lobby")) {
				String section = world + "." + Util.FCTU(type);
				if (!cfgSpawn.isConfigurationSection(section))
					cfgSpawn.createSection(section);
				cfgSpawn.set(section + ".x", x);
				cfgSpawn.set(section + ".y", y);
				cfgSpawn.set(section + ".z", z);
				saveConfig();
				return true;
			} else {
				int spawnid = 0;
				int spawnidx = -1;
				while (cfgSpawn.contains(world + "." + "Spawn" + spawnid)) {
					spawnidx = spawnid;
					spawnid++;
				}
				spawnidx = spawnidx + 1;
				String section = world + "." + "Spawn" + spawnidx;
				cfgSpawn.createSection(section);
				cfgSpawn.set(section + ".x", x);
				cfgSpawn.set(section + ".y", y);
				cfgSpawn.set(section + ".z", z);
				saveConfig();
				return true;
			}
		}
		return false;
	}

	public boolean removeSpawn(World world, String name) {
		String section = world.getName() + "." + Util.FCTU(name.toLowerCase());
		if (cfgSpawn.isConfigurationSection(section)) {
			cfgSpawn.set(section, null);
			saveConfig();
			return true;
		}
		return false;
	}
}
