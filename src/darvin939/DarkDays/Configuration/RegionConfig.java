package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import darvin939.DarkDays.DarkDays;

public class RegionConfig {
	private FileConfiguration cfgRegions;
	private File cfgRegionsFile;
	private Logger log = Logger.getLogger("Minecraft");

	public RegionConfig(DarkDays plg) {
		cfgRegionsFile = new File(plg.getDataFolder() + "/regions.yml");
		cfgRegions = YamlConfiguration.loadConfiguration(cfgRegionsFile);
		saveConfig();
	}

	public void saveConfig() {
		try {
			cfgRegions.save(cfgRegionsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getCfg() {
		return cfgRegions;
	}
	
	public void setParam(String reg, String param, Object value) {
		if (!cfgRegions.isConfigurationSection(reg))
			cfgRegions.createSection(reg);
		ConfigurationSection section = cfgRegions.getConfigurationSection(reg);
		section.set(param, Arrays.asList(value));
		saveConfig();
	}
	
	public Object getParam(String reg, String value) {
		try {
			cfgRegions.load(cfgRegionsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cfgRegions.isConfigurationSection(reg)) {
			ConfigurationSection section = cfgRegions.getConfigurationSection(reg);
			return section.getList(value).toString();
		}
		log.severe(DarkDays.getConsolePfx() + "Error of receiving parameter from regions.yml");
		return null;
	}
}