package darvin939.DarkDays.Listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Region;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Players.Memory.PlayerInfo;

public class EntityListener implements Listener {
	DarkDays plg;

	public EntityListener(DarkDays plugin) {
		plg = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		Entity e = event.getEntity();
		if (Region.canSpawn(event)) {
			if (Nodes.only_zombies.getBoolean()) {
				if (!event.getEntityType().equals(EntityType.ZOMBIE)) {
					if ((e instanceof Monster) || e instanceof Animals) {
						e.getWorld().spawnEntity(e.getLocation(), EntityType.ZOMBIE);
						event.setCancelled(true);
					} else {
						event.setCancelled(true);
					}
				}
				if (event.getEntityType().equals(EntityType.ZOMBIE))
					if (Tasks.alphas.isEmpty()) {
						Tasks.alphas.put(e, null);
					} else if (!Tasks.findPack(e))
						Tasks.alphas.put(e, null);
			}
		} else
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		event.setDroppedExp(0);
		Entity entity = event.getEntity();
		if ((entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) entity.getLastDamageCause();
			if (damage.getDamager().getType() == EntityType.PLAYER) {
				Player killer = (Player) damage.getDamager();
				PlayerInfo.addZombieKill(killer);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if ((e instanceof Zombie)) {
			if (Tasks.zombie_damage.containsKey(e))
				Tasks.zombie_damage.put(e, (Tasks.zombie_damage.get(e) - event.getDamage()));
			else
				Tasks.zombie_damage.put(e, Nodes.zombie_health.getInteger() - event.getDamage());

			if (Tasks.zombie_damage.get(e) <= 20)
				if (Tasks.zombie_damage.get(e) <= 0) {
					event.setDamage(0);
					Tasks.zombie_damage.remove(e);
				} else
					((Zombie) e).setHealth(Tasks.zombie_damage.get(e));
			else
				((Zombie) e).setHealth(((Zombie) e).getMaxHealth());

		}
	}
}
