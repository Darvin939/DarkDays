package darvin939.DarkDays.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Players.Memory.PlayerZombie;
import darvin939.DarkDays.Regions.RegionManager;

public class ZombieListener implements Listener {

	public HashMap<Entity, Integer> zombie_damage = new HashMap<Entity, Integer>();

	public ZombieListener(DarkDays plg) {
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		event.getDrops().clear();
		event.setDroppedExp(0);
		Entity entity = event.getEntity();
		if (event.getEntity().getType() == EntityType.ZOMBIE) {
			LivingEntity datZombie = (LivingEntity) entity;
			for (Entry<UUID, ArrayList<ItemStack>> set : PlayerZombie.get().entrySet()) {
				if (datZombie.getUniqueId() == set.getKey()) {
					event.getDrops().addAll(set.getValue());
					PlayerZombie.kill(datZombie);
					datZombie.remove();
					break;
				}
			}
		}
		if ((entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) entity.getLastDamageCause();
			if (damage.getDamager().getType() == EntityType.PLAYER) {
				Player killer = (Player) damage.getDamager();
				PlayerInfo.addZombieKill(killer);
			}
		}
		UUID id = ((LivingEntity) entity).getUniqueId();
		if (Tasks.speedZombies.contains(id))
			Tasks.speedZombies.remove(id);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKill(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player)) {
			if ((event.getDamager() instanceof Zombie))
				event.setDamage(Nodes.attack_strength.getInteger());

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		Entity e = event.getEntity();
		if (RegionManager.canSpawn(event)) {
			if (Nodes.only_zombies.getBoolean()) {
				if (!event.getEntityType().equals(EntityType.ZOMBIE)) {
					if ((e instanceof Monster) || e instanceof Animals) {
						e.getWorld().spawn(e.getLocation(), (Class<? extends Entity>)Zombie.class);
						event.setCancelled(true);
					} else {
						event.setCancelled(true);
					}
				}
			}
		} else
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if ((e instanceof Zombie)) {
			if (zombie_damage.containsKey(e))
				zombie_damage.put(e, (int) (zombie_damage.get(e) - event.getDamage()));
			else
				zombie_damage.put(e, (int) (Nodes.zombie_health.getInteger() - event.getDamage()));
			if (zombie_damage.get(e) <= 20) {
				if (zombie_damage.get(e) <= 0) {
					((Zombie) e).setHealth(0);
					net.minecraft.server.v1_6_R2.Entity entity = ((CraftLivingEntity) e).getHandle();
					entity.die();
					zombie_damage.remove(e);
				} else
					((Zombie) e).setHealth(zombie_damage.get(e));
			} else
				((Zombie) e).setHealth(((Zombie) e).getMaxHealth());

		}
	}
}
