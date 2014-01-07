package darvin939.DarkDays.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Utils.Util;
import darvin939.DeprecAPI.BlockAPI;
import darvin939.DeprecAPI.ItemAPI;

public class Wand implements Listener {

	private DarkDays plg;

	public Wand(DarkDays plugin) {
		plg = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		int wand = Nodes.wand_item.getInteger();
		Player p = event.getPlayer();
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && ItemAPI.get(p.getItemInHand().getType()).id() == wand && plg.hasPermissions(p, "wand")) {
			Block b = BlockAPI.getTargetBlock(p, 10);
			if (b.getType() == Material.CHEST)
				if (Config.getCC().getChestInfo(p, b.getLocation())) {
				} else
					Util.PrintMSGPx(p, "chest_normal");
			// event.setCancelled(true);
		}
	}
}
