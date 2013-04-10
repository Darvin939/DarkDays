package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import darvin939.DarkDays.DarkDays;

public class RC {
	private FileConfiguration cfgZones;
	private File cfgZonesFile;
	private Logger log = Logger.getLogger("Minecraft");

	public RC(DarkDays plg) {
		cfgZonesFile = new File(plg.getDataFolder() + "/regions.yml");
		cfgZones = YamlConfiguration.loadConfiguration(cfgZonesFile);
		saveConfig();
	}

	public void saveConfig() {
		try {
			cfgZones.save(cfgZonesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getCfg() {
		return cfgZones;
	}
	
	public void setParam(String reg, String param, Object value) {
		if (!cfgZones.isConfigurationSection(reg))
			cfgZones.createSection(reg);
		ConfigurationSection section = cfgZones.getConfigurationSection(reg);
		section.set(param, Arrays.asList(value));
		saveConfig();
	}
	
	public Object getParam(String reg, String value) {
		try {
			cfgZones.load(cfgZonesFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cfgZones.isConfigurationSection(reg)) {
			ConfigurationSection section = cfgZones.getConfigurationSection(reg);
			return section.getList(value).toString();
		}
		log.severe(DarkDays.prefix + "Error of receiving parameter from zones.yml");
		return null;
	}
}