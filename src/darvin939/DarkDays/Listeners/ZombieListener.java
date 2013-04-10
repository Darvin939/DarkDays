package darvin939.DarkDays.Listeners;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Players.Memory.PlayerZombie;

public class ZombieListener implements Listener {

	// private DayZ plg;

	public ZombieListener(DarkDays plg) {
		// this.plg = plg;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		event.getDrops().clear();
		if (event.getEntity().getType() == EntityType.ZOMBIE) {
			Entity entity = event.getEntity();
			LivingEntity datZombie = (LivingEntity) entity;
			for (Entry<UUID, ArrayList<ItemStack>> set : PlayerZombie.getPZ().entrySet()) {
				if (datZombie.getUniqueId() == set.getKey()) {
					event.getDrops().addAll(set.getValue());
					PlayerZombie.kill(datZombie);
					datZombie.remove();
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKill(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player)) {
			if ((event.getDamager() instanceof Zombie))
				event.setDamage(Nodes.attack_strength.getInteger());

		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void cancelZombieFire(EntityCombustEvent event) {
		EntityType et = event.getEntityType();
		event.setCancelled(et == EntityType.ZOMBIE);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void allowZombieFireByPlayer(EntityCombustByEntityEvent event) {
		event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void allowZombieFireByBlock(EntityCombustByBlockEvent event) {
		event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void cancelAgroIfTargetIsNotPlaying(EntityTargetLivingEntityEvent event) {
		Entity target = event.getTarget();
		if (target.getType() == EntityType.PLAYER)
			event.setCancelled(!PlayerInfo.isPlaying((Player) target));
	}
}
