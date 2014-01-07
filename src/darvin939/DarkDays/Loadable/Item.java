package darvin939.DarkDays.Loadable;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Util;

public class Item extends LoadUtils implements AbsItem {
	protected final String name;
	protected final DarkDays plugin;
	private String message;
	private int item;
	private String depend;

	public Item(DarkDays plugin, String name) {
		this.name = name;
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setMessage(String key, String msg) {
		this.message = key;
		plugin.getConfiguration().addMSG(key, msg);
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	
	@Override
	public void sendMessage(Player p) {
		Util.PrintMSGPx(p, message);
	}
	
	
	@Override
	public void setItem(int item) {
		this.item = item;
	}

	@Override
	public int getItem() {
		return item;
	}

	@Override
	public void setDepend(String depend) {
		this.depend = depend;

	}

	@Override
	public String getDepend() {
		return depend;
	}

}
