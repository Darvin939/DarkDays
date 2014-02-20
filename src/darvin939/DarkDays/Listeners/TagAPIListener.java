package darvin939.DarkDays.Listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Players.Memory.PlayerData;

public class TagAPIListener implements Listener {
	public static ArrayList<Player> ptag = new ArrayList<Player>();
	private DarkDays plg;

	public TagAPIListener(DarkDays plg) {
		this.plg = plg;
	}

	public static void refreshPlayer(Player p1, Player p2) {
		if (DarkDays.isTagAPI())
			TagAPI.refreshPlayer(p1, p2);
	}

	public static void refreshPlayer(Player p) {
		if (DarkDays.isTagAPI())
			TagAPI.refreshPlayer(p);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if (Nodes.coloured_tegs.getBoolean()) {
			Player p = event.getNamedPlayer();
			String name = p.getDisplayName();
			if (PlayerData.getPlayerHeals(p) < PlayerData.getPlayerKills(p)) {
				event.setTag(ChatColor.RED.toString() + name);
				p.sendMessage("RED" + PlayerData.getPlayerHeals(p) + " - " + PlayerData.getPlayerKills(p));
			} else if (PlayerData.getPlayerHeals(p) > PlayerData.getPlayerKills(p)) {
				event.setTag(ChatColor.GREEN.toString() + name);
				p.sendMessage("GREEN" + PlayerData.getPlayerHeals(p) + " - " + PlayerData.getPlayerKills(p));
			} else
				event.setTag(name);
			for (Player op : plg.getServer().getOnlinePlayers()) {
				if (ptag.contains(op)) {
					event.setTag(name);
				}
			}
		}
	}
}
