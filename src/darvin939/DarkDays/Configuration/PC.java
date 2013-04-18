package darvin939.DarkDays.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Players.Memory.PlayerLoadData;
import darvin939.DarkDays.Players.Sql.DDPlayer;
import darvin939.DarkDays.Players.Sql.PlayerManager;

public class PC {
	private FileConfiguration cfgPlayers;
	private File cfgPlayersFile;

	private HashMap<Player, String> effects = new HashMap<Player, String>();
	private HashMap<Player, PlayerLoadData> data = new HashMap<Player, PlayerLoadData>();
	private static DarkDays plg;
	// constants
	public static final String DEATH = "death";
	public static final String SPAWNED = "spawned";
	public static final String NOVICE = "novice";
	public static final String HUNGER = "hunger";
	public static final String EFFECTS = "effects";

	public PC(DarkDays plugin) {
		plg = plugin;
		if (!Config.isSqlWrapper()) {
			cfgPlayersFile = new File(plg.getDataFolder() + "/players.yml");
			cfgPlayers = YamlConfiguration.loadConfiguration(cfgPlayersFile);
			saveConfig();
		}

	}

	public void addEffect(Player p, String effect) {
		String SE = effects.get(p) != null ? effects.get(p) : "";
		if (Config.isSqlWrapper()) {
			if (SE.isEmpty())
				effects.put(p, effect);
			else {
				effects.put(p, effects.get(p) + "," + effect);
			}
		} else {
			effects.put(p, !SE.isEmpty() ? SE + ", " + effect : effect);
		}
	}

	public static void fixLocation(Player p) {
		p.teleport(fix(p));
	}

	public static void fixLocation(PlayerRespawnEvent event) {
		event.setRespawnLocation(fix(event.getPlayer()));
	}

	public static Location fix(Player p) {
		Location loc = p.getLocation();
		if (Config.isSqlWrapper()) {
			loc = PlayerManager.getInstance().getPlayer(p).getLoc();
		}
		Double x = loc.getX();
		Double y = loc.getY();
		Double z = loc.getZ();

		if (new Location(p.getWorld(), x, y - 1, z).getBlock().getType() == Material.AIR) {
			for (int i = y.intValue(); i > 0; i--) {
				Material m = new Location(p.getWorld(), x, i, z).getBlock().getType();
				if (m != Material.AIR) {
					Material m1 = new Location(p.getWorld(), x, i + 1, z).getBlock().getType();
					Material m2 = new Location(p.getWorld(), x, i + 2, z).getBlock().getType();
					if (m1 == Material.AIR && m2 == Material.AIR) {
						y = (double) i + 1;
					}
				}
			}
		}

		if (new Location(p.getWorld(), x, y + 1, z).getBlock().getType() != Material.AIR) {
			for (int i = y.intValue(); i < p.getWorld().getMaxHeight(); i++) {
				System.out.println("2) " + i);
				Material m1 = new Location(p.getWorld(), x, i, z).getBlock().getType();
				Material m2 = new Location(p.getWorld(), x, i + 1, z).getBlock().getType();
				if (m1 == Material.AIR && m2 == Material.AIR) {
					y = (double) i;
				}
			}
		}
		return new Location(p.getWorld(), x, y, z);
	}

	public void removeEffect(Player p, String effect) {
		if (effects.get(p) != null || !effects.get(p).isEmpty()) {
			String e = effects.get(p);
			if (e.startsWith(effect))
				if (e.startsWith(effect + ","))
					e = e.replace(effect + ",", "");
				else
					e = e.replace(effect, "");
			else
				e = e.replace("," + effect, "");
			effects.put(p, e);
		}
	}

	public String[] getEffects(Player p) {
		return effects.get(p) != null ? effects.get(p).split("\\,") : null;
	}

	public String getEffect(Player p, String effect) {
		for (String e : getEffects(p)) {
			if (e.equalsIgnoreCase(effect))
				return e;
		}
		return "";
	}

	public Object getData(Player p, String param) {
		return data.get(p).get(param);
	}

	public void setData(Player p, String param, Object value) {
		data.get(p).set(param, value);
	}
	
	public void saveConfig() {
		try {
			cfgPlayers.save(cfgPlayersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Location getSpawnLoc(Player p) {
		int x, y, z;
		if (plg.getConfig().isConfigurationSection("Spawns.Lobby")) {
			ConfigurationSection section = plg.getConfig().getConfigurationSection("Spawns.Lobby");
			x = section.getInt("x");
			y = section.getInt("y");
			z = section.getInt("z");
			Location loc = new Location(p.getWorld(), x, y, z);
			return loc;
		}
		return p.getWorld().getSpawnLocation();
	}

	public void saveAll() {
		if (Config.isSqlWrapper()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				DDPlayer player = PlayerManager.getInstance().getPlayer(p);
				player.addData(data.get(p));
				player.addEffects(effects.get(p));
				player.addPlayer();
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!cfgPlayers.isConfigurationSection(p.getName()))
					cfgPlayers.createSection(p.getName());
				ConfigurationSection section = cfgPlayers.getConfigurationSection(p.getName());
				PlayerLoadData PLD = data.get(p);
				section.set(HUNGER, PLD.getHunger());
				section.set(DEATH, PLD.isDeath());
				section.set(NOVICE, PLD.isNovice());
				section.set(SPAWNED, PLD.isSpawned());
				section.set(EFFECTS, effects.get(p));
				saveConfig();
			}
		}
	}

	public void initialize(Player p) {
		if (Config.isSqlWrapper()) {
			// PlayerManager.getInstance().addPlayer(p);
			// DDPlayer player = PlayerManager.getInstance().getPlayer(p);
			DDPlayer player = PlayerManager.getInstance().addPlayer(p);
			player.addPlayer();
			effects.put(p, player.getEffects());
			data.put(p, player.getData());
		} else {
			if (!cfgPlayers.isConfigurationSection(p.getName())) {
				ConfigurationSection s = cfgPlayers.createSection(p.getName());
				s.set(DEATH, false);
				s.set(NOVICE, true);
				s.set(SPAWNED, false);
				s.set(HUNGER, 0);
				s.set(EFFECTS, "");
				saveConfig();
			}
			ConfigurationSection s = cfgPlayers.getConfigurationSection(p.getName());
			data.put(p, new PlayerLoadData(s.getInt(HUNGER), s.getBoolean(DEATH), s.getBoolean(NOVICE), s.getBoolean(SPAWNED)));
			effects.put(p, s.getString(EFFECTS));
			// PlayerInfo.setNovice(p, true);}
		}
	}
}
