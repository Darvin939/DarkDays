package darvin939.DarkDays.Commands.Handlers;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Listeners.PlayerListener;
import darvin939.DarkDays.Utils.Util;

public class Tag extends Handler {

	public Tag(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		ArrayList<Player> ptag = PlayerListener.ptag;
		if (args.length > 1) {
			if (hasPermissions(p, "tag", true)) {
				if (args[1].equalsIgnoreCase("enable")) {
					Util.Print(p, Config.FGU.MSG("tag_ends", "enabled"));
					ptag.remove(p);
					for (Player op : plugin.getServer().getOnlinePlayers()) {
						if (!op.equals(p)) {
							TagAPI.refreshPlayer(op, p);
						}
					}
				}
				if (args[1].equalsIgnoreCase("disable")) {
					Util.Print(p, Config.FGU.MSG("tag_ends", "disabled"));
					ptag.add(p);
					for (Player op : plugin.getServer().getOnlinePlayers()) {
						if (!op.equals(p)) {
							TagAPI.refreshPlayer(op, p);
						}
					}
				}
				if (args[1].equalsIgnoreCase("help"))
					getHelp(p, "tag");
			}
		}
		return true;
	}

}
