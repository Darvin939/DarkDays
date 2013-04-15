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

public class Wand implements Listener {

	private DarkDays plg;

	public Wand(DarkDays plugin) {
		plg = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		int wand = Nodes.wand_item.getInteger();
		Player p = event.getPlayer();
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand().getTypeId() == wand && plg.hasPermissions(p, "wand")) {
			Block b = p.getTargetBlock(null, 10);
			if (b.getType() == Material.CHEST)
				if (Config.getCC().getChestInfo(p, b.getLocation())) {
				} else
					Config.FGU.PrintMsg(p, Config.FGU.MSG("chest_normal"));
			event.setCancelled(true);
		}
	}
}
