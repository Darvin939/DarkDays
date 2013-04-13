package darvin939.DarkDays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Players.Memory.PlayerZombie;

public class Tasks {

	public static double maxExp = 0.20;

	public static HashMap<Player, Integer> player_hunger = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> player_noise = new HashMap<Player, Integer>();
	public static HashMap<Player, Location> player_loc = new HashMap<Player, Location>();
	public static HashMap<Entity, List<Entity>> alphas = new HashMap<Entity, List<Entity>>();
	public static HashMap<Entity, Entity> betas = new HashMap<Entity, Entity>();
	public static HashMap<Entity, Integer> zombie_damage = new HashMap<Entity, Integer>();
	private static DarkDays plg;
	private static Server server;

	public Tasks(DarkDays plugin) {
		plg = plugin;
		server = plg.getServer();
		run();
	}

	public void run() {
		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				zombieSpeedChange();
				// zombieGroup();
			}
		}, Nodes.zombie_smoothness.getLong(), Nodes.zombie_smoothness.getLong());

		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				makeThirst();
			}
		}, 20, 20);

		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				Loot.fillTask();
			}
		}, Nodes.chest_regen.getInteger() * 200, Nodes.chest_regen.getInteger() * 200);

		plg.getServer().getScheduler().runTaskTimer(plg, new Runnable() {
			public void run() {
				Config.getPC().saveAll();
				Config.getCC().saveAll();
			}
		}, 3 * 100, 3 * 100);

	}

	public static void zombieSpeedChange() {

		for (Player p : server.getOnlinePlayers())
			if (!p.isDead() && PlayerInfo.isPlaying(p)) {
				for (Entity e : p.getNearbyEntities(100.0, 100.0, 100.0))
					for (Entry<UUID, ArrayList<ItemStack>> set : PlayerZombie.getPZ().entrySet())
						if (((LivingEntity) e).getUniqueId() != set.getKey()) {
							if (e.getType().equals(EntityType.ZOMBIE)) {
								double y = e.getLocation().getY();
								if (y == (int) y) {
									Vector velo = e.getVelocity();
									velo.setX(speedUp(velo.getX()));
									velo.setY(speedUp(velo.getY()));
									velo.setZ(speedUp(velo.getZ()));
									e.setVelocity(velo);
								}
							}
						}
			}
	}

	public static double speedUp(double dimension) {
		if (dimension != 0.0) {
			return dimension * (1.0 + (Nodes.zombie_speed.getDouble() - dimension));
		}
		return 0.0;
	}

	public static void makeThirst() {
		for (Player p : server.getOnlinePlayers())
			if (player_hunger.containsKey(p)) {
				if (!p.isDead() && PlayerInfo.isPlaying(p)) {
					depleteThirst(p, 1);
					p.setLevel(player_hunger.get(p).intValue() / 10000);
					float original = (float) (((Tasks.player_noise.get(p)).intValue() - 1) * maxExp);
					if (player_loc.get(p) != null)
						if ((int) player_loc.get(p).getX() == (int) p.getLocation().getX() && (int) player_loc.get(p).getZ() == (int) p.getLocation().getZ() && (int) player_loc.get(p).getY() == (int) p.getLocation().getY()) {
							PlayerListener.smoothExp(original, 0, p);
							if (p.getExp() == 0 && PlayerListener.getThread(p.getName()) != null)
								PlayerListener.getThread(p.getName()).interrupt();
							Tasks.player_noise.put(p, 1);
						}
					player_loc.put(p, p.getLocation());
					if (p.getLevel() <= 1) {
						Config.FGU.PrintPxMsg(p, "game_need_water");
						p.damage(1);
						player_hunger.put(p, 5000);
					}
					double pvr = Math.pow(player_noise.get(p), 2);
					for (Entity e : p.getNearbyEntities(pvr, pvr, pvr)) {
						if ((!(e instanceof Zombie)) || (!alphas.containsKey(e)))
							continue;
						Location pl = p.getLocation();
						Location el = e.getLocation();

						if (pl.distance(el) > pvr) {
							((Zombie) e).setTarget(null);
						}
						((Zombie) e).setTarget(p);
						if (pl.distance(el) > 16.0) {
							double nx = (pl.getX() + el.getX()) / 2.0;
							double ny = (pl.getY() + el.getY()) / 2.0;
							double nz = (pl.getZ() + el.getZ()) / 2.0;
							walkTo((LivingEntity) e, nx, ny, nz, (float) (Nodes.zombie_speed.getDouble() / 4.0));
						}
						walkTo((LivingEntity) e, pl.getX(), pl.getY(), pl.getZ(), (float) (Nodes.zombie_speed.getDouble() / 4.0));
					}
				}
			}
	}

	public static void depleteThirst(Player player, int thirst) {
		int nt = (player_hunger.get(player)).intValue() - 4 * thirst * Nodes.thirst_speed.getInteger();
		player_hunger.put(player, nt);
	}

	public static void zombieGroup() {
		for (Player p : server.getOnlinePlayers())
			for (Entity e : p.getNearbyEntities(100.0, 100.0, 100.0)) {
				if ((!alphas.containsKey(e)) || (alphas.get(e) == null) || (((List<Entity>) alphas.get(e)).isEmpty()))
					continue;
				for (Entity b : alphas.get(e)) {
					Location l = e.getLocation();
					int distance = (int) l.distance(b.getLocation());
					if (distance > 8)
						b.teleport(e);
					else if (distance > 4)
						walkTo((LivingEntity) b, l.getX(), l.getY(), l.getZ(), (float) (Nodes.zombie_speed.getDouble() / 4.0));
					if (((Creature) e).getTarget() != null)
						((Creature) b).setTarget(((Creature) e).getTarget());
				}
			}
	}

	public static boolean walkTo(LivingEntity livingEntity, double x, double y, double z, float speed) {
		return ((CraftLivingEntity) livingEntity).getHandle().getNavigation().a(x, y, z, speed);
	}

	public static boolean findPack(Entity e) {
		for (Entity ne : e.getNearbyEntities(200.0, 200.0, 200.0)) {
			if ((!alphas.containsKey(ne)) || (alphas.get(ne) == null) || ((alphas.get(ne)).isEmpty()) || ((alphas.get(ne)).size() >= 8))
				continue;
			List<Entity> list = alphas.get(ne);
			list.add(e);
			alphas.put(ne, list);
			betas.put(e, ne);
			return true;
		}

		return false;
	}

	public static void removeFromHashMaps(Player p) {
		if (player_hunger.containsKey(p))
			player_hunger.remove(p);
		if (player_noise.containsKey(p))
			player_noise.remove(p);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}

	public static void resetHashMaps(Player p) {
		// player_hunger.put(p, (Integer) Config.getParam(p, "hunger"));
		player_noise.put(p, 1);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}
}
