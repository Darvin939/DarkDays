package darvin939.DarkDays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegionSelector;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Utils.Util;

public class Region {
	private static Logger log = Logger.getLogger("Minecraft");
	private static List<Polygonal2DRegionSelector> regions = new ArrayList<Polygonal2DRegionSelector>();
	private static int matID = 88;

	public static WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			log.warning(DarkDays.prefix + "WorldEdit not found on the server");
			return null;
		}
		return (WorldEditPlugin) plugin;
	}

	public static HashMap<String, List<Points>> getPoints() {
		HashMap<String, List<Points>> result = new HashMap<String, List<Points>>();
		for (String section : getSections()) {
			String data = ((String) Config.getRC().getParam(section, "Points")).replace("[[", "").replace("]]", "");
			String[] coords = data.split("\\}, \\{");
			List<Points> list = new ArrayList<Points>();
			if (coords.length > 0)
				for (String zx : coords) {
					String[] coord = zx.split(", ");
					if (coord.length > 1) {
						if (!coord[0].startsWith("ymin") && !coord[1].replace("}", "").startsWith("ymax")) {
							int z = Integer.parseInt(coord[0].replace("{", "").replace("z: ", ""));
							int x = Integer.parseInt(coord[1].replace("}", "").replace("x: ", ""));
							list.add(new Points(Double.valueOf(z), Double.valueOf(x)));

						} else if (coord[0].startsWith("ymin") && coord[1].replace("}", "").startsWith("ymax")) {
							int ymin = Integer.parseInt(coord[0].replace("{", "").replace("ymin: ", ""));
							int ymax = Integer.parseInt(coord[1].replace("}", "").replace("ymax: ", ""));
							list.add(new Points(String.valueOf(ymin), String.valueOf(ymax)));
						}
					}
				}
			result.put(section, list);
		}
		return result;
	}

	public static boolean canSpawn(CreatureSpawnEvent event) {
		try {
			if (Nodes.enable_regions.getBoolean()) {
				Entity e = event.getEntity();
				Location l = e.getLocation();
				String wName = e.getWorld().getName();
				if (Config.getRC().getCfg().isConfigurationSection(wName) && event.getSpawnReason() != SpawnReason.CUSTOM) {
					for (String section : Config.getRC().getCfg().getConfigurationSection(wName).getKeys(false)) {
						if (new Location(l.getWorld(), l.getX(), 0, l.getZ()).getBlock().getType() == Material.getMaterial(matID) && Boolean.valueOf(((String) Config.getRC().getParam(wName + "." + section, "SpawnZombie")).replace("[", "").replace("]", ""))) {
							int min = 0, max = e.getWorld().getMaxHeight();
							for (Points point : getPoints().get(wName + "." + section)) {
								if (point.max != null && point.min != null) {
									max = Integer.parseInt(point.max);
									min = Integer.parseInt(point.min);
								}
							}
							if (l.getY() > min && l.getY() <= max)
								return true;
						}
					}
				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static class Points {
		double x;
		double z;
		String min;
		String max;

		public Points(String min, String max) {
			this.min = min;
			this.max = max;
		}

		public Points(double z, double x) {
			this.x = x;
			this.z = z;
		}
	}

	public static void setProtect() {
		for (Polygonal2DRegionSelector poly : regions) {
			try {
				for (Vector v : poly.getRegion()) {
					Vector2D v2d = v.toVector2D();
					Location loc = new Location(BukkitUtil.toWorld(poly.getRegion().getWorld()), v2d.getX(), 0, v2d.getZ());
					loc.getBlock().setType(Material.getMaterial(matID));

				}
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
			}
		}
	}

	private static String[] pointsFormatter(String points, Boolean height) {
		String[] pointgroup = points.split(" \\* ");
		String[] point = pointgroup[0].split(" - ");
		String[] list = new String[height ? point.length + 1 : point.length];
		for (int i = 0; i < point.length; i++) {
			point[i] = point[i].replace("(", "").replace(")", "");
			String[] zx = point[i].split(", ");
			list[i] = "{z: " + zx[0] + ", x: " + zx[1] + "}";
		}
		if (height) {
			pointgroup[1] = pointgroup[1].replace("(", "").replace(")", "");
			String[] y = pointgroup[1].split(" - ");
			list[point.length] = "{ymin: " + y[0] + ", ymax: " + y[1] + "}";
		}
		return list;
	}

	public static void save(Player p, String name, boolean spawn, boolean height) {
		if (getSelectionPoints(p, height) != null) {
			name = p.getWorld().getName() + "." + name;
			Config.getRC().setParam(name, "Points", getSelectionPoints(p, height));
			Config.getRC().setParam(name, "SpawnZombie", spawn);
			getRegions(p.getWorld());
			setProtect();
		}
	}

	private static List<String> getSections() {
		List<String> result = new ArrayList<String>();
		Set<String> worlds = Config.getRC().getCfg().getKeys(false);
		for (String world : worlds)
			for (String section : Config.getRC().getCfg().getConfigurationSection(world).getKeys(false)) {
				section = world + "." + section;
				result.add(section);
			}

		return result;
	}

	public static void getRegions(World w) {
		int min = 0, max = w.getMaxHeight();
		for (String section : getSections()) {
			List<BlockVector2D> list = new ArrayList<BlockVector2D>();
			for (Points point : getPoints().get(section)) {
				min = 0;
				max = w.getMaxHeight();
				if (point.max == null && point.min == null)
					list.add(new BlockVector2D(point.z, point.x));
				else {
					min = Integer.parseInt(point.min);
					max = Integer.parseInt(point.max);
				}
			}
			LocalWorld lw = BukkitUtil.getLocalWorld(w);
			regions.add(new Polygonal2DRegionSelector(lw, list, min, max));
		}
	}

	public static String[] getSelectionPoints(Player p, Boolean height) {
		if (getWorldEdit() != null)
			if (getWorldEdit().getSelection(p) != null) {
				return pointsFormatter(getWorldEdit().getSelection(p).getRegionSelector().getIncompleteRegion().toString(), height);
			} else
				Util.msg(p, "Region not selected!", 'e');
		else
			Util.msg(p, "WorldEdit was not found!", 'e');
		return null;
	}
}
