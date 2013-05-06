package darvin939.DarkDays.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
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

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Region;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Players.Memory.PlayerInfo;

public class EntityListener implements Listener {
	DarkDays plg;
	public HashMap<Entity, Integer> zombie_damage = new HashMap<Entity, Integer>();

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
		UUID id = ((LivingEntity) entity).getUniqueId();
		if (Tasks.speedZombies.contains(id))
			Tasks.speedZombies.remove(id);
	}

	// Баг: когда зомби в лаве, он исчезает или не дохнет
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if ((e instanceof Zombie)) {
			if (zombie_damage.containsKey(e))
				zombie_damage.put(e, (zombie_damage.get(e) - event.getDamage()));
			else
				zombie_damage.put(e, Nodes.zombie_health.getInteger() - event.getDamage());
			if (zombie_damage.get(e) <= 20) {
				if (zombie_damage.get(e) <= 0) {
					((Zombie) e).setHealth(0);
					net.minecraft.server.v1_4_R1.Entity entity = ((CraftLivingEntity) e).getHandle();
					entity.die();
					zombie_damage.remove(e);
				} else
					((Zombie) e).setHealth(zombie_damage.get(e));
			} else
				((Zombie) e).setHealth(((Zombie) e).getMaxHealth());

		}
	}
}
