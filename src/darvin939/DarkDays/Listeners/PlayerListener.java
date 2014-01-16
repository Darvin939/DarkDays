package darvin939.DarkDays.Listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Configuration.PlayerConfig;
import darvin939.DarkDays.Loadable.Effect;
import darvin939.DarkDays.Loot.LootManager;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Players.Memory.PlayerZombie;
import darvin939.DarkDays.Utils.Util;

public class PlayerListener implements Listener {
	DarkDays plg;

	public PlayerListener(DarkDays plg) {
		this.plg = plg;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof Chest && event.getPlayer() instanceof Player) {
			Chest chest = (Chest) event.getInventory().getHolder();
			Block b = chest.getBlock();
			if (Config.getCC().isChest(b))
				if (Nodes.chest_disappear.getBoolean() && Config.getCC().getLoot(b.getLocation()) != null) {
					if (LootManager.isChestEmpty(b)) {
						chest.getInventory().clear();
						Location chestloc = new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ());
						chestloc.getBlock().setType(Material.AIR);
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(EntityRegainHealthEvent event) {
		if (Nodes.disable_health_regen.getBoolean())
			if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		Config.FGU.UpdateMsg(p);
		Config.getPC().initialize(p);

		boolean novice = (boolean) Config.getPC().getData(p, PlayerConfig.DEATH);
		boolean death = (boolean) Config.getPC().getData(p, PlayerConfig.NOVICE);
		if (novice || death) {
			resetPlayer(p);
			Config.getSpawnCfg().getSpawnLoc(p);
		}
		if ((boolean) Config.getPC().getData(p, PlayerConfig.SPAWNED)) {
			Tasks.player_thirst.put(p, (int) Config.getPC().getData(p, PlayerConfig.HUNGER));
			Tasks.player_noise.put(p, 1.0);
			PlayerInfo.addPlayer(p);
			p.teleport(PlayerConfig.fix(p));

			setupEffects(p);
		}
		for (Player op : plg.getServer().getOnlinePlayers()) {
			if (!op.equals(p)) {
				TagAPIListener.refreshPlayer(op, p);
			}
		}
	}

	public void setupEffects(Player p) {
		for (Entry<Method, Object> set : DarkDays.getEffectManager().getRunMethods().entrySet()) {
			for (String effect : Config.getPC().getEffects(p)) {
				if (((Effect) set.getValue()).getName().equalsIgnoreCase(effect)) {
					try {
						DarkDays.getEffectManager().addTaskID(p, effect, Integer.parseInt(String.valueOf(set.getKey().invoke(set.getValue(), p))));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		// Config.getPC().initialize(p);
		boolean novice = (boolean) Config.getPC().getData(p, PlayerConfig.DEATH);
		boolean death = (boolean) Config.getPC().getData(p, PlayerConfig.NOVICE);
		if (novice || death) {
			resetPlayer(p);
			event.setRespawnLocation(Config.getSpawnCfg().getSpawnLoc(p));
		}
		if ((boolean) Config.getPC().getData(p, PlayerConfig.SPAWNED)) {
			Tasks.player_thirst.put(p, (int) Config.getPC().getData(p, PlayerConfig.HUNGER));
			Tasks.player_noise.put(p, 1.0);
			PlayerInfo.addPlayer(p);
			event.setRespawnLocation(PlayerConfig.fix(p));
			return;
		}
		p.getInventory().clear();
		for (Player op : plg.getServer().getOnlinePlayers()) {
			if (!op.equals(p)) {
				TagAPIListener.refreshPlayer(op, p);
			}
		}
	}

	// This method doesn't work with the plugin AdminCmd!
	private void resetPlayer(Player p) {
		Tasks.resetHashMaps(p);
		Config.getPC().setData(p, PlayerConfig.DEATH, false);
		Config.getPC().setData(p, PlayerConfig.SPAWNED, false);
		Config.getPC().setData(p, PlayerConfig.NOVICE, true);
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setLevel(0);
		p.setExp(0);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		onPlayerExit(p);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		onPlayerExit(p);
	}

	private void onPlayerExit(Player p) {
		if (Tasks.player_thirst.containsKey(p))
			Config.getPC().setData(p, PlayerConfig.HUNGER, Tasks.player_thirst.get(p));
		else
			Config.getPC().setData(p, PlayerConfig.HUNGER, 309999);
		Config.getPC().saveAll();
		Tasks.removeFromHashMaps(p);
		DarkDays.getEffectManager().pauseEffects(p);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKill(EntityDamageByEntityEvent event) {
		if (Nodes.coloured_tegs.getBoolean())
			if ((event.getDamager() instanceof Player)) {
				Player d = (Player) event.getDamager();
				if ((event.getEntity() instanceof Player)) {
					Player h = (Player) event.getEntity();
					if (event.getDamage() > h.getHealth()) {
						PlayerInfo.addPlayerKill(d);
						TagAPIListener.refreshPlayer(d);
						Util.Print(d, "You killed " + h.getName() + "!");
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Config.getPC().setData(p, PlayerConfig.DEATH, true);
		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent dEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

			if (dEvent.getDamager() instanceof Player && PlayerInfo.isPlaying(p)) {
				Player d = (Player) dEvent.getDamager();
				PlayerInfo.addPlayerKill(d);
				TagAPIListener.refreshPlayer(d);
				Util.Print(d, "You killed " + p.getName() + "!");
			}
			if (dEvent.getDamager() instanceof Zombie) {
				Location loc = p.getLocation();
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();

				boolean kill = true;
				for (Entity e : p.getNearbyEntities(50, 50, 50))
					if (e instanceof Player)
						kill = false;
				if (kill) {
					for (Entity e : p.getNearbyEntities(50, 50, 50)) {
						if (e instanceof Zombie) {
							net.minecraft.server.v1_7_R1.Entity entity = ((CraftLivingEntity) e).getHandle();
							entity.die();
						}
					}
				}

				LivingEntity datZombie = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);

				if (Nodes.zombie_pickup.getBoolean()) {
					datZombie.getEquipment().setHelmet(p.getEquipment().getHelmet());
					datZombie.getEquipment().setChestplate(p.getEquipment().getChestplate());
					datZombie.getEquipment().setBoots(p.getEquipment().getBoots());
					datZombie.getEquipment().setLeggings(p.getEquipment().getLeggings());
				}

				ItemStack item[] = p.getInventory().getContents();
				for (int i = 0; i < item.length; i++) {
					items.add(item[i]);
				}
				p.setLevel(0);
				event.setDroppedExp(0);
				for (Player op : plg.getServer().getOnlinePlayers()) {
					if (!op.equals(p)) {
						TagAPIListener.refreshPlayer(op, p);
					}
				}
				PlayerZombie.add(datZombie, items);
			}

		}
		PlayerInfo.removePlayer(p);
		event.getDrops().clear();
		DarkDays.getEffectManager().cancelEffects(p);
	}

}
