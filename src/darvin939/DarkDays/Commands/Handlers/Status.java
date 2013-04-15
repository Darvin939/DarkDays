package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Players.Memory.PlayerData;
import darvin939.DarkDays.Players.Memory.PlayerInfo;
import darvin939.DarkDays.Utils.Util;

public class Status extends Handler {

	public Status(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(Player p, String[] args) throws InvalidUsage {
		if (hasPermissions(p, "status", true))
			if (PlayerInfo.isPlaying(p)) {
				PlayerData pd = PlayerInfo.getPlayers().get(p.getUniqueId());
				Util.msg(p, "&b================= &2Your Progress &b=================", '/');
				Util.msg(p, "Current session:", '/');
				Util.msg(p, "  &7Players you bandaged:&6 " + pd.getPlayerHeals(), '/');
				Util.msg(p, "  &7Killed players:&6 " + pd.getPlayerKills(), '/');
				Util.msg(p, "  &7Killed zombies:&6 " + pd.getZombieKills(), '/');
				Util.msg(p, "Total:", '/');
				Util.msg(p, "  &7Players you bandaged:&6 " + pd.getPlayerHeals(), '/');
				Util.msg(p, "  &7Killed players:&6 " + pd.getPlayerKills(), '/');
				Util.msg(p, "  &7Killed zombies:&6 " + pd.getZombieKills(), '/');
			} else
				Config.FGU.PrintPxMsg(p, Config.FGU.MSG("game_noplay", "/dd spawn"));
		return true;
	}

}
