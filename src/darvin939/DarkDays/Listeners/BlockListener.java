package darvin939.DarkDays.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Utils.Util;

public class BlockListener implements Listener {

	DarkDays plg;

	public BlockListener(DarkDays plugin) {
		plg = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (Config.getCC().isChest(event.getBlock())) {
			Util.PrintMSGPx(p, "chest_cantDestroy");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if (Config.getCC().isChest(b)) {
				if (Nodes.chest_click.getBoolean()) {
					b.setType(Material.AIR);
				}
			}
		}
	}
}