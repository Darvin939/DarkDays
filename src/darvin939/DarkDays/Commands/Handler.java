package darvin939.DarkDays.Commands;

import org.bukkit.command.CommandSender;

import darvin939.DarkDays.DarkDays;

public abstract class Handler {

	protected final DarkDays plugin;

	public Handler(DarkDays plugin) {
		this.plugin = plugin;
	}

	public abstract boolean perform(CommandSender sender, String[] args) throws InvalidUsage;

	protected boolean hasPermissions(CommandSender sender, String command) {
		return plugin.hasPermissions(sender, command);
	}
}
