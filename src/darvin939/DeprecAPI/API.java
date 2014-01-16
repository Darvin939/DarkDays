package darvin939.DeprecAPI;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class API extends JavaPlugin{
	private Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile des;

	public void onEnable() {
		des = getDescription();
		log.info("[DeprecAPI] " + des.getName() + " v" + des.getVersion() + " enabled");
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			log.info("[DeprecAPI] Failed to submit stats to the Metrics (mcstats.org)");
		}
	}

	public void onDisable() {
		log.info("[DeprecAPI]" + des.getName() + " v" + des.getVersion() + " disabled");
	}
}
