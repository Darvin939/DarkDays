package darvin939.DarkDays;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityVillager;
import net.minecraft.server.v1_4_R1.EntityZombie;
import net.minecraft.server.v1_4_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_4_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_4_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_4_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_4_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_4_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_4_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_4_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_4_R1.PathfinderGoalSelector;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Utils.Util;

public class Tasks {

	public static final double maxExp = 0.20;
	private static final Integer fixConst = 6;

	public static HashMap<Player, Integer> player_hunger = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> player_noise = new HashMap<Player, Integer>();
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
		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				mainTask();
			}
		}, 20, 20);

		server.getScheduler().scheduleSyncRepeatingTask(plg, new Runnable() {
			public void run() {
				LootManager.fillTask();
			}
		}, Nodes.chest_regen.getInteger() * 200, Nodes.chest_regen.getInteger() * 200);

		plg.getServer().getScheduler().runTaskTimer(plg, new Runnable() {
			public void run() {
				Config.getPC().saveAll();
				Config.getCC().saveAll();
			}
		}, 3 * 100, 3 * 100);

	}

	public void mainTask() {
		for (Player p : server.getOnlinePlayers())
			if (player_hunger.containsKey(p)) {
				if (!p.isDead() && PlayerInfo.isPlaying(p)) {
					updateThirst(p, 1);
					p.setLevel(player_hunger.get(p) / 10000);
					float original = (float) (((Tasks.player_noise.get(p)).intValue() - 1) * maxExp);
					if (player_loc.get(p) != null)
						if ((int) player_loc.get(p).getX() == (int) p.getLocation().getX() && (int) player_loc.get(p).getZ() == (int) p.getLocation().getZ() && (int) player_loc.get(p).getY() == (int) p.getLocation().getY()) {
							PlayerListener.smoothExp(original, 0, p);
							if (p.getExp() == 0 && PlayerListener.getThread(p.getName()) != null)
								PlayerListener.getThread(p.getName()).interrupt();
							Tasks.player_noise.put(p, 1);
						}
					player_loc.put(p, p.getLocation());

					if (player_hunger.get(p) <= 0) {
						Util.PrintPxMSG(p, "game_need_water");
						p.damage(1);
						player_hunger.put(p, 10 * Nodes.thirst_speed.getInteger());
					}
					double pvr = Math.pow(player_noise.get(p), 2);
					for (Entity e : p.getNearbyEntities(pvr, pvr, pvr)) {

						if (!(e instanceof Zombie))
							continue;
						((Zombie) e).setTarget(null);
						Location pl = p.getLocation();
						Location el = e.getLocation();

						if (pl.distance(el) > pvr) {
							((Zombie) e).setTarget(null);
						} else
							((Zombie) e).setTarget(p);

						if (pl.distance(el) > 16.0) {
							double nx = (pl.getX() + el.getX()) / 2.0;
							double ny = (pl.getY() + el.getY()) / 2.0;
							double nz = (pl.getZ() + el.getZ()) / 2.0;
							((CraftLivingEntity) e).getHandle().getNavigation().a(nx, ny, nz, new Float(Nodes.zombie_speed.getDouble() / 4));
						}
						setSpeed(e);

					}
					if (player_hunger.get(p) < 0)
						player_hunger.put(p, 0);
				}
			}
	}

	public static void updateThirst(Player player, int thirst) {
		int nt = (player_hunger.get(player)).intValue() - fixConst * thirst * Nodes.thirst_speed.getInteger();
		player_hunger.put(player, nt);
	}

	private void setSpeed(Entity entity) {
		UUID id = ((LivingEntity) entity).getUniqueId();
		if (speedZombies.contains(id))
			return;
		speedZombies.add(id);
		EntityZombie zombie = ((CraftZombie) entity).getHandle();
		Field fGoalSelector;
		try {
			fGoalSelector = EntityLiving.class.getDeclaredField("goalSelector");
			fGoalSelector.setAccessible(true);
			Float speed = 0.23F;
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

	public static void removeFromHashMaps(Player p) {
		if (player_hunger.containsKey(p))
			player_hunger.remove(p);
		if (player_noise.containsKey(p))
			player_noise.remove(p);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}

	public static void resetHashMaps(Player p) {
		player_noise.put(p, 1);
		if (player_loc.containsKey(p))
			player_loc.remove(p);
	}
}
