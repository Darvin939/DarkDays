package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Players.Memory.PlayerData;
import darvin939.DarkDays.Utils.Util;

public class Status extends Handler {

	public Status(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (hasPermission(p, "status", true))
				if (PlayerData.isPlaying(p)) {
					Util.Print(p, "&b================= &2Your Progress &b=================");
					Util.Print(p, "Current session:");
					Util.Print(p, "  &7Players you bandaged:&6 " + PlayerData.getPlayerHeals(p));
					Util.Print(p, "  &7Killed players:&6 " + PlayerData.getPlayerKills(p));
					Util.Print(p, "  &7Killed zombies:&6 " + PlayerData.getZombieKills(p));
					// Util.Print(p, "Total:");
					// Util.Print(p, "  &7Players you bandaged:&6 " +
					// pd.getPlayerHeals());
					// Util.Print(p, "  &7Killed players:&6 " +
					// pd.getPlayerKills());
					// Util.Print(p, "  &7Killed zombies:&6 " +
					// pd.getZombieKills());
				} else
					Util.PrintMSGPx(p, "game_noplay", "/dd spawn");
			return true;
		}
		s.sendMessage("You must be a Player to do this");
		return true;
	}

}
