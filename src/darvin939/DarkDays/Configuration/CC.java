package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Util;

public class CC {
	public FileConfiguration cfgChest;
	private File cfgChestFile;
	private Logger log = Logger.getLogger("Minecraft");
	private DarkDays plg;

	public CC(DarkDays plg) {
		this.plg = plg;
		cfgChestFile = new File(plg.getDataFolder() + "/chests.yml");
		cfgChest = YamlConfiguration.loadConfiguration(cfgChestFile);
		saveConfig();
	}

	public void saveConfig() {
		try {
			cfgChest.save(cfgChestFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getCfg() {
		return cfgChest;
	}

	public Object getParam(Location l, String value) {
		try {
			cfgChest.load(cfgChestFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cfgChest.isConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ())) {
			ConfigurationSection section = cfgChest.getConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
			return section.get(value);
		}
		log.severe(DarkDays.prefix + "Error of receiving parameter from chests.yml");
		return null;
	}

	public void setParam(Location l, String param, Object value) {
		if (!cfgChest.isConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ()))
			cfgChest.createSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
		if (param != null) {
			ConfigurationSection section = cfgChest.getConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
			section.set(param, value);
		}
		if (param == "del") {
			cfgChest.set("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ(), null);
		}
		saveConfig();
	}

	public String isChest(Player p) {
		if (p.getTargetBlock(null, 10).getType() == Material.CHEST) {
			Location l = p.getTargetBlock(null, 10).getLocation();
			if (cfgChest.isConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ())) {
				return "Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			}
		}
		return null;
	}

	public boolean getChestInfo(Player p, Location l) {
		if (cfgChest.isConfigurationSection("Chest-" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ())) {
			Util.msg(p, "&b===== &2Chest Info&b =====", '/');
			Util.msg(p, "LootID: &a" + getParam(l, "LootID"), '/');
			Util.msg(p, "Respawn Time: &a" + getParam(l, "Respawn"), '/');
			Util.msg(p, "Location: &ax:" + l.getBlockX() + " y:" + l.getBlockY() + " z:" + l.getBlockZ(), '/');
			// Str.msg(p, "Protected: &a" + getChestParam(l, "Protected"), '/');
			Util.msg(p, "&b===== &2Chest Info&b =====", '/');
			return true;
		}
		return false;
	}

	public Location getChestLoc(String section) {
		if (cfgChest.isConfigurationSection(section)) {
			String sloc = cfgChest.getString(section + ".Location");
			String[] coords = sloc.split(",");
			double x = Double.valueOf(coords[0]);
			double y = Double.valueOf(coords[1]);
			double z = Double.valueOf(coords[2]);
			World wolrld = plg.getServer().getWorld(cfgChest.getString(section + ".World"));
			return new Location(wolrld, x, y, z);
		}
		return null;
	}
}
