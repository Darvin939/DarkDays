package darvin939.DarkDays.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;

public abstract class Handler {

	protected final DarkDays plugin;

	public Handler(DarkDays plugin) {
		this.plugin = plugin;
	}

	public abstract boolean perform(CommandSender sender, String[] args) throws InvalidUsage;

	protected boolean hasPermission(Player p, String command, Boolean mess) {
		return plugin.hasPermission(p, command, mess);
	}
	
	protected void getHelp(Player p, String command){
		plugin.getHelp(p, command);
	}
}
