package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Sql.Players.DDPlayer;
import darvin939.DarkDays.Sql.Players.PlayerManager;

public class PC {
	private FileConfiguration cfgPlayers;
	private File cfgPlayersFile;
	private Logger log = Logger.getLogger("Minecraft");
	private PlayerManager pm;

	// constants
	public static final String DEATH = "death";
	public static final String SPAWNED = "spawned";
	public static final String NOVICE = "novice";
	public static final String HUNGER = "hunger";

	public PC(DarkDays plg) {
		if (Config.isSqlWrapper()) {
			pm = PlayerManager.getInstance();
		} else {
			cfgPlayersFile = new File(plg.getDataFolder() + "/players.yml");
			cfgPlayers = YamlConfiguration.loadConfiguration(cfgPlayersFile);
			saveConfig();
		}
	}

	public void addEffect(Player p, String effect) {
		if (Config.isSqlWrapper()) {
			DDPlayer player = pm.getPlayer(p);
			if (player.getEffects().isEmpty())
				player.addEffects(effect);
			else {
				player.addEffects(player.getEffects() + "," + effect);
			}
		} else {
			setParam(p, "effects", !((String) getParam(p, "effects")).isEmpty() ? getParam(p, "effects") + ", " + effect : effect);
		}
	}

	public void removeEffect(Player p, String effect) {
		if (Config.isSqlWrapper()) {
			DDPlayer player = pm.getPlayer(p);
			String effects = player.getEffects();
			if (effects.startsWith(effect))
				if (effects.startsWith(effect + ","))
					effects = effects.replace(effect + ",", "");
				else
					effects = effects.replace(effect, "");
			else
				effects = effects.replace("," + effect, "");
			player.addEffects(effects);
		} else {
			setParam(p, "effects", effect);
		}
	}

	public String[] getEffects(Player p) {
		if (Config.isSqlWrapper()) {
			DDPlayer player = pm.getPlayer(p);
			String effects = player.getEffects();
			return effects.split("\\,");
		} else {
			return ((String) getParam(p, "effects")).split(", ");
		}
	}

	public Object getData(Player p, String param) {
		if (Config.isSqlWrapper()) {
			return pm.getPlayer(p).getData().get(param);
		} else {
			return getParam(p, param);
		}
	}

	private Object getParam(Player p, String param) {
		try {
			cfgPlayers.load(cfgPlayersFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cfgPlayers.isConfigurationSection(p.getName())) {
			ConfigurationSection section = cfgPlayers.getConfigurationSection(p.getName());
			return section.get(param);
		}
		log.severe(DarkDays.prefix + "Error of receiving parameter from players.yml");
		return null;
	}

	public void setData(Player p, String param, Object value) {
		if (Config.isSqlWrapper()) {
			pm.getPlayer(p).getData().set(param, value);
		} else {
			setParam(p, param, value);
		}
	}

	private void setParam(Player p, String param, Object value) {
		if (!cfgPlayers.isConfigurationSection(p.getName()))
			cfgPlayers.createSection(p.getName());
		ConfigurationSection section = cfgPlayers.getConfigurationSection(p.getName());
		section.set(param, value);
		saveConfig();

	}

	public void saveConfig() {
		try {
			cfgPlayers.save(cfgPlayersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initialize(Player p) {
		if (Config.isSqlWrapper()) {
			pm.addPlayer(p);
		} else {
			if (!cfgPlayers.isConfigurationSection(p.getName())) {
				ConfigurationSection section = cfgPlayers.createSection(p.getName());
				section.set("death", false);
				section.set("novice", true);
				section.set("spawned", false);
				section.set("hunger", 0);
				saveConfig();
			}
			// PlayerInfo.setNovice(p, true);}
		}
	}
}
