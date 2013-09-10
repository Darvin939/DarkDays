package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Utils.Util;

public class About extends Handler {
	public About(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		PluginDescriptionFile des = plugin.getDescription();
		Util.Print(p, "&2&l&oPlugin " + des.getName() + " v" + des.getVersion());
		Util.Print(p, "&6Author: &7Darvin939 (Sergey Mashoshin)");
		Util.Print(p, "&6Contact Email:&7 darvin212@gmail.com");
		return true;
	}
}