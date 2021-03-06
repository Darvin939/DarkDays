package darvin939.DarkDays.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;

public class Util {

	// ������� ���������
	public static void Print(CommandSender cs, String message) {
		if (cs instanceof Player) {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace(String.valueOf(ChatColor.COLOR_CHAR) + "f", "&7").replace("&f", "&f")));
	}

	// ������� ��������� MSG � ���������� ���������
	public static void PrintMSGPx(Player p, String message) {
		Print(p, DarkDays.getChatPfx() + Config.FGU.MSG(message));
	}

	// ������� ��������� MSG � ������� � ���������� ���������
	public static void PrintMSGPx(CommandSender cs, String message, String keys) {
		Print(cs, DarkDays.getChatPfx() + Config.FGU.MSG(message, keys));
	}

	// ������� ��������� MSG
	public static void PrintMSG(Player p, String message) {
		Print(p, Config.FGU.MSG(message));
	}

	public static String FCTU(String s) {
		if (s != null && !s.isEmpty() && s.length() > 1)
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		return s.toUpperCase();
	}

	public static String[] newArgs(String[] args) {
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, args.length - 1);
		return newArgs;
	}

	public static void unknownCmd(Player p, String s, String[] commands) {
		String handlerName = s.toLowerCase() + " ";
		String list = "";
		for (int i = 1; i < commands.length; i++) {
			list = list.isEmpty() ? "..." + handlerName + "&7<" + commands[i] : list + ", " + commands[i];
		}
		Print(p, Config.FGU.MSG("cmd_unknown", DarkDays.getCmdPfx() + handlerName + commands[0]));
		Print(p, Config.FGU.MSG("hlp_commands") + " &2" + list + "&7>");
	}

	public static void unknownCmd(Player p, Class<?> c, String[] commands) {
		unknownCmd(p, c.getSimpleName(), commands);
	}

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static void CS(String msg) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace(String.valueOf(ChatColor.COLOR_CHAR) + "f", "&7").replace("&f", "&f")));
	}

	public static void CSPx(String msg) {
		CS(DarkDays.getConsolePfx() + msg);
	}
}
