package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.command.CommandSender;
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
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		PluginDescriptionFile des = plugin.getDescription();
		Util.Print(s, "&2&l&oPlugin " + des.getName() + " v" + des.getVersion());
		Util.Print(s, "&6Author: &7Darvin939 (Sergey Mashoshin. Russia, Moscow)");
		Util.Print(s, "&6Contact Email:&7 darvin212@gmail.com");
		return true;
	}
}