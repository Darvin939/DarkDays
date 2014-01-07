package darvin939.DarkDays.Commands.Handlers;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Listeners.TagAPIListener;
import darvin939.DarkDays.Utils.Util;

public class Tag extends Handler {

	public Tag(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		ArrayList<Player> ptag = TagAPIListener.ptag;
		if (s instanceof Player) {
			Player p = (Player) s;
			if (args.length > 1) {
				if (hasPermissions(p, "tag", true)) {
					if (args[1].equalsIgnoreCase("enable")) {
						Util.PrintMSG(p, "tag_ends", "enabled");
						ptag.remove(p);
						for (Player op : plugin.getServer().getOnlinePlayers()) {
							if (!op.equals(p)) {
								TagAPI.refreshPlayer(op, p);
							}
						}
						return true;
					}
					if (args[1].equalsIgnoreCase("disable")) {
						Util.PrintMSG(p, "tag_ends", "disabled");
						ptag.add(p);
						for (Player op : plugin.getServer().getOnlinePlayers()) {
							if (!op.equals(p)) {
								TagAPI.refreshPlayer(op, p);
							}
						}
						return true;
					}
				}
				if (args[1].equalsIgnoreCase("help")) {
					getHelp(p, "tag");
					return true;
				}
				Util.unknownCmd(p, getClass(), new String[] { args[1], "enable", "disable" });
			} else {
				getHelp(p, "tag");
				
			}
			return true;
		}
		s.sendMessage("You must be a Player to do this");
		return true;
	}
}
