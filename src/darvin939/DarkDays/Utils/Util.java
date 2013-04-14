package darvin939.DarkDays.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;

public class Util {

	public static void msg(Player p, String message, char type) {
		if (!Config.FGU.msglist.contains(message)) {
			String rMessage = "";
			switch (type) {
			case 'e':
				rMessage = "&c[Error]&f " + message;
				break;
			case 'p':
				rMessage = "&b" + DarkDays.prefix + "&f" + message;
				break;
			case '/':
				rMessage = message;
			}
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', rMessage));
		} else
			Config.FGU.PrintMsg(p, message);
	}

	public static String FCTU(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String[] newArgs(String[] args) {
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, args.length - 1);
		return newArgs;
	}
}
