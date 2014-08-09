package darvin939.DarkDays.Listeners;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Players.Memory.PlayerData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityListener implements Listener {

   public EntityListener(DarkDays plugin) {}

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void cancelZombieFire(EntityCombustEvent event) {
      EntityType et = event.getEntityType();
      event.setCancelled(et == EntityType.ZOMBIE);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void allowZombieFireByPlayer(EntityCombustByEntityEvent event) {
      event.setCancelled(false);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void allowZombieFireByBlock(EntityCombustByBlockEvent event) {
      event.setCancelled(false);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void cancelAgroIfTargetIsNotPlaying(EntityTargetLivingEntityEvent event) {
      LivingEntity target = event.getTarget();
      if(target.getType() == EntityType.PLAYER) {
         event.setCancelled(!PlayerData.isPlaying((Player)target));
      }

   }
}
