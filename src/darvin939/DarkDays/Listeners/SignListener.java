package darvin939.DarkDays.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import darvin939.DarkDays.DarkDays;

public class SignListener implements Listener {
	DarkDays plg;

	public SignListener(DarkDays plg) {
		this.plg = plg;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		if (ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[dd]"))
			if (!plg.hasPermissions(p, "sign")) {
				event.setLine(0, "{dd}");
			} else {
				setLine(event, 0, "&9[DarkDays]");

				boolean r = false, s = false, m = false, error = false;
				int ri = 0, mi = 0;
				boolean sb = false;

				for (int i = 0; i < 4; i++) {
					String line = ChatColor.stripColor(event.getLine(i));
					String[] params = line.split(" ");
					if (params.length > 0) {
						for (String pr : params) {
							if (pr.startsWith("radius=") || pr.startsWith("r=")) {
								if (!r) {
									try {
										ri = Integer.parseInt(pr.replace("radius=", "").replace("r=", ""));
										r = true;

									} catch (NumberFormatException e) {
										error = true;
									}
								}
							}
							if (pr.startsWith("spawn=") || pr.startsWith("s=")) {
								if (!s) {
									String srepl = pr.replace("spawn=", "").replace("s=", "");
									if (srepl.equalsIgnoreCase("true") || srepl.equalsIgnoreCase("false")) {
										sb = Boolean.parseBoolean(srepl);
										s = true;
									} else
										error = true;
								}
							}
							if (pr.startsWith("max=") || pr.startsWith("m=") || pr.startsWith("maxzombies=")) {
								if (!m) {
									try {
										mi = Integer.parseInt(pr.replace("max=", "").replace("m=", "").replace("maxzombies=", ""));
										m = true;
									} catch (NumberFormatException e) {
										error = true;
									}
								}
							}
						}
					}
				}
				if (!r || error) {
					clearParamLines(event);
					setLine(event, 2, "{&4Error&0}");
				} else {
					clearParamLines(event);
					setLine(event, 1, "Radius=&6" + ri);
					setLine(event, 2, "Spawn=&6" + sb);
					if (sb)
						setLine(event, 3, "MaxZmbs=&6" + mi);
				}

			}
		if ((ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[DarkDays]"))) {
			event.setLine(0, "{DarkDays}");
			clearParamLines(event);
		}
	}

	public void clearParamLines(SignChangeEvent event) {
		event.setLine(1, "");
		event.setLine(2, "");
		event.setLine(3, "");
	}

	public void setLine(SignChangeEvent event, int n, String text) {
		event.setLine(n, ChatColor.translateAlternateColorCodes('&', text));
	}
}
