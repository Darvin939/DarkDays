package darvin939.DarkDays.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Utils.Util;

public class BlockListener implements Listener {

	DarkDays plg;

	public BlockListener(DarkDays plugin) {
		plg = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (Config.getCC().isChest(p)) {
			Util.PrintMSG(p, "chest_cantDestroy");
			// Util.msg(p, "You can't destroy the loot chest!", 'p');
			event.setCancelled(true);
		}
	}
}
