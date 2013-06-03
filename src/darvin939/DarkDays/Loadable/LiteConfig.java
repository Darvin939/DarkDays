package darvin939.DarkDays.Loadable;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import darvin939.DarkDays.DarkDays;

public class LiteConfig {
	private File dir = null;
	private File cfgFile;
	private YamlConfiguration cfg;
	private Class<?> clazz;

	public LiteConfig(DarkDays plugin, Class<?> clazz) {
		this.clazz = clazz;
		String superName = clazz.getSuperclass().getSimpleName();
		if (superName.equalsIgnoreCase("effect"))
			dir = new File(plugin.getDataFolder(), "effects");
		if (superName.equalsIgnoreCase("item"))
			dir = new File(plugin.getDataFolder(), "items");
	}

	public FileConfiguration get() {
		if (dir != null) {
			cfgFile = new File(dir + File.separator + clazz.getSimpleName()+".yml");
			cfg = YamlConfiguration.loadConfiguration(cfgFile);
			return cfg;
		}
		return null;
	}

	public void save() {
		try {
			cfg.save(cfgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
