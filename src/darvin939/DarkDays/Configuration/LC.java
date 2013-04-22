package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import darvin939.DarkDays.DarkDays;

public class LC {
	private FileConfiguration cfgLoot;
	private File cfgLootFile;

	public LC(DarkDays plg) {
		cfgLootFile = new File(plg.getDataFolder() + "/loot.yml");
		cfgLoot = YamlConfiguration.loadConfiguration(cfgLootFile);
		saveConfig();
	}

	public void saveConfig() {
		try {
			cfgLoot.save(cfgLootFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getCfg() {
		try {
			cfgLoot.load(cfgLootFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return cfgLoot;
	}
}
