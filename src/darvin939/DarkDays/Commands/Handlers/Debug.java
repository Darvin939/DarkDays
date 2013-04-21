package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;

public class Debug extends Handler {
	public Debug(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		// if (p.getName().equalsIgnoreCase("darvin939") &&
		// Bukkit.getOnlineMode()) {
		if (p.getName().equalsIgnoreCase("darvin939")) {
			// Dubug code here
			p.sendMessage("Cancel Effect");
			DarkDays.getEffectManager().cancelEffect(p, "Bleeding");
		} else
			p.sendMessage("This command only for the developer!");
		return true;
	}
}
