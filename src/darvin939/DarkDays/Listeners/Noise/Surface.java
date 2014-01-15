package darvin939.DarkDays.Listeners.Noise;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DeprecAPI.ItemAPI;

public class Surface {

	private static HashMap<Integer, String[]> list = new HashMap<Integer, String[]>();
	private static double cnst = 0.1;

	public Surface(FileConfiguration cfg) {
		if (cfg.isConfigurationSection("Noise")) {
			cnst = Nodes.noise_multiplier.getDouble();
			if (Nodes.noise_enable.getBoolean()) {
				for (int i = 1; i < 6; i++) {
					if (cfg.getString("Noise.L" + i) != null) {
						list.put(i, cfg.getString("Noise.L" + i).replaceAll(" ", "").split(","));
					} else
						break;
				}
			}
		}
	}

	private static double check(Player p) {
		for (Entry<Integer, String[]> s : list.entrySet()) {
			for (String ids : s.getValue()) {
				try {
					int id = Integer.parseInt(ids);
					if (getBlockID(p) == id) {
						return s.getKey() * cnst;
					}
				} catch (NumberFormatException e) {
				}

			}
		}
		return 0;
	}

	public static void setNoise(Player p, char type) {
		double noise = 1.0;
		switch (type) {
		case 'a':
			noise = 1.7;
			break;
		case 'b':
			noise = 2.4;
			break;
		case 'c':
			noise = 3.1;
			break;
		case 'd':
			noise = 3.8;
			break;
		case 'e':
			noise = 4.5;
			break;
		}

		Tasks.player_noise.put(p, noise + check(p));
	}

	public static Integer getBlockID(Player p) {
		Location l = p.getLocation();
		return ItemAPI.get(new Location(l.getWorld(), l.getX(), l.getY() - 1, l.getZ()).getBlock().getType()).id();
	}

}
