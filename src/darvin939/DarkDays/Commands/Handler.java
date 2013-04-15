package darvin939.DarkDays.Commands;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;

public abstract class Handler {

	protected final DarkDays plugin;

	public Handler(DarkDays plugin) {
		this.plugin = plugin;
	}

	public abstract boolean perform(Player p, String[] args) throws InvalidUsage;

	protected boolean hasPermissions(Player p, String command) {
		return plugin.hasPermissions(p, command);
	}
}
