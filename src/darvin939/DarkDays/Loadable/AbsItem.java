package darvin939.DarkDays.Loadable;

import org.bukkit.entity.Player;

public abstract interface AbsItem {
	
	public abstract String getName();
	
	public abstract String getMessage();

	public abstract int getItem();
	
	public abstract void setDepend(String depend);
	
	public abstract String getDepend();

	public abstract void setMessage(String key, String msg);

	public abstract void setItem(int item);

	public abstract void sendMessage(Player p);
}
