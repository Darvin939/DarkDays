package darvin939.DarkDays.Listeners;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Utils.Rnd;
import darvin939.DarkDays.Utils.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

   DarkDays plg;


   public BlockListener(DarkDays plugin) {
      this.plg = plugin;
   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public void onBlockBreak(BlockBreakEvent event) {
      Player p = event.getPlayer();
      if(Config.getCC().isChest(event.getBlock()).booleanValue()) {
         Util.PrintMSGPx(p, "chest_cantDestroy");
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public void onPlayerInteract(PlayerInteractEvent event) {
      if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
         Block b = event.getClickedBlock();
         if(Config.getCC().isChest(b).booleanValue() && Config.Nodes.chest_click.getBoolean().booleanValue()) {
            if(Config.Nodes.chest_spawnz.getBoolean().booleanValue() && Rnd.get(Config.Nodes.chest_spawnzperc.getInteger())) {
               ((Chest)b.getState()).getInventory().clear();
               LivingEntity zombie = (LivingEntity)b.getWorld().spawnEntity(b.getLocation(), EntityType.ZOMBIE);
               zombie.getEquipment().setHelmet(new ItemStack(Material.CHEST));
               zombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
               zombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
               zombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            }

            b.setType(Material.AIR);
         }
      }

   }
}
