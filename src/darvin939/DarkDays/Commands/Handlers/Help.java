package darvin939.DarkDays.Commands.Handlers;

import org.bukkit.command.CommandSender;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Commands.Handler;
import darvin939.DarkDays.Commands.InvalidUsage;
import darvin939.DarkDays.Utils.Util;

public class Help extends Handler {

	public Help(DarkDays plugin) {
		super(plugin);
	}

	@Override
	public boolean perform(CommandSender s, String[] args) throws InvalidUsage {
		String[] cmds = plugin.Commands.getCommands();
		Util.Print(s, "&b=================== &2DarkDays &b===================");
		for (String cmd : cmds) {
			Util.Print(s, "&6/dd " + cmd + " &f: " + plugin.Commands.getHelp(cmd));
		}
		Util.Print(s, "&7For more help of command type &6/dd &4<command> &6help");
		return true;
	}

}
