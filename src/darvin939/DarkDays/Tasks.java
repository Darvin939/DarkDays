package darvin939.DarkDays;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.EntityVillager;
import net.minecraft.server.v1_7_R1.EntityZombie;
import net.minecraft.server.v1_7_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_7_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_7_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R1.PathfinderGoalSelector;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.Noise.Noise;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Utils.Util;

public class Tasks {

	public static final double maxExp = 0.2;
	private static final Integer fixConst = 6;

	public static HashMap<Player, Integer> player_thirst = new HashMap<Player, Integer>();
	public static HashMap<Player, Double> player_noise = new HashMap<Player, Double>();
	public static HashMap<Player, Location> player_loc = new HashMap<Player, Location>();
	public static List<UUID> speedZombies = new ArrayList<UUID>();
	private DarkDays plg;
	private Server server;

	public Tasks(DarkDays plugin) {
		plg = plugin;
		server = plg.getServer();
		run();
	}

	public void run() {
		// main task
		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				mainTask();
			}
		}, 20, 20);

		// chests fill task
		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				LootManager.fillTask();
			}
		}, Nodes.chest_regen.getInteger() * 200, Nodes.chest_regen.getInteger() * 200);

		// save task
		plg.getServer().getScheduler().runTaskTimer(plg, new Runnable() {
			public void run() {
				Config.getPC().saveAll();
				Config.getCC().saveAll();
			}
		}, 1000, 1000);

		/*
		 * plg.getServer().getScheduler().scheduleSyncRepeatingTask(plg, new
		 * Runnable() { public void run() { SignListener.entityRespawnTask(); }
		 * }, 200, 200);
		 */

	}

	public static double getNoise(Player p) {
		return ((player_noise.get(p)) - 1) * maxExp;
	}

	public void mainTask() {
		for (Player p : server.getOnlinePlayers())
			if (player_thirst.containsKey(p)) {
				if (!p.isDead() && PlayerInfo.isPlaying(p)) {
					updateThirst(p, 1);
					p.setLevel(player_thirst.get(p) / 10000);
					double original = getNoise(p);
					if (player_loc.get(p) != null)
						if ((int) player_loc.get(p).getX() == (int) p.getLocation().getX() && (int) player_loc.get(p).getZ() == (int) p.getLocation().getZ() && (int) player_loc.get(p).getY() == (int) p.getLocation().getY()) {
							if (original != 0) {
								Noise.smoothExp(original, 0, p);
							}
							Tasks.player_noise.put(p, 1.0);
						}
					player_loc.put(p, p.getLocation());

					if (player_thirst.get(p) <= 0) {
						Util.PrintMSGPx(p, "game_need_water");
						p.damage(1);
						player_thirst.put(p, 10 * Nodes.thirst_speed.getInteger());
					}
					double pvr = Math.pow(player_noise.get(p), 2);
					for (Entity e : p.getNearbyEntities(pvr, pvr, pvr)) {

						if (!(e instanceof Zombie))
							continue;
						// ((Zombie) e).setTarget(null);
						Location pl = p.getLocation();
						Location el = e.getLocation();
						Double dist = pl.distance(el);
						Zombie zmb = (Zombie) e;

						if (dist >= pvr) {
							zmb.setTarget(null);
							setSpeed(e, 0.23F);
						}

						if (dist < pvr) {
							zmb.setTarget(p);
						}

						if (dist > 16.0) {
							double nx = (pl.getX() + el.getX()) / 2.0;
							double ny = (pl.getY() + el.getY()) / 2.0;
							double nz = (pl.getZ() + el.getZ()) / 2.0;
							((EntityInsentient) ((CraftLivingEntity) e).getHandle()).getNavigation().a(nx, ny, nz, new Float(Nodes.zombie_speed.getDouble() / 6));
						}
						setSpeed(e);

					}
					if (player_thirst.get(p) < 0)
						player_thirst.put(p, 0);
				}
			}
	}

	public static void updateThirst(Player player, int thirst) {
		int nt = (player_thirst.get(player)) - fixConst * thirst * Nodes.thirst_speed.getInteger();
		player_thirst.put(player, nt);
	}

	private void setSpeed(Entity entity, float speed) {
		UUID id = ((LivingEntity) entity).getUniqueId();
		if (speedZombies.contains(id))
			return;
		speedZombies.add(id);
		EntityZombie zombie = ((CraftZombie) entity).getHandle();
		Field fGoalSelector;
		try {
			fGoalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
			fGoalSelector.setAccessible(true);
			// Float speed = 0.23F;
			speed = new Float(Nodes.zombie_speed.getDouble() / 10);
			PathfinderGoalSelector gs = new PathfinderGoalSelector(((CraftWorld) entity.getWorld()).getHandle() != null && ((CraftWorld) entity.getWorld()).getHandle().methodProfiler != null ? ((CraftWorld) entity.getWorld()).getHandle().methodProfiler : null);
			gs.a(0, new PathfinderGoalFloat(zombie));
			gs.a(1, new PathfinderGoalBreakDoor(zombie));
			gs.a(2, new PathfinderGoalMeleeAttack(zombie, EntityHuman.class, speed, false));
			gs.a(3, new PathfinderGoalMeleeAttack(zombie, EntityVillager.class, speed, true));
			gs.a(4, new PathfinderGoalMoveTowardsRestriction(zombie, speed));
			gs.a(5, new PathfinderGoalMoveThroughVillage(zombie, speed, false));
			gs.a(6, new PathfinderGoalRandomStroll(zombie, speed));
			gs.a(7, new PathfinderGoalLookAtPlayer(zombie, EntityHuman.class, 15.0F));
			gs.a(7, new PathfinderGoalRandomLookaround(zombie));
			fGoalSelector.set(zombie, gs);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void setSpeed(Entity entity) {
		Float speed = 0.23F;
		speed = new Float(Nodes.zombie_speed.getDouble() / 10);
		setSpeed(entity, speed);
	}

	public static void removeFromHashMaps(Player p) {
		if (player_thirst.containsKey(p))
			player_thirst.remove(p);
		if (player_noise.containsKey(p))
			player_noise.remove(p);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}

	public static void resetHashMaps(Player p) {
		player_noise.put(p, 1.0);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}
}
