package darvin939.DarkDays.Loadable;

import org.bukkit.entity.Player;

public abstract interface AbsEffect {

	public abstract int getPercent();

	public abstract int getDelay();

	public abstract void setTime(int power);

	public abstract int getTime();

	public abstract void setDelay(int delay);

	public abstract void setPercent(int percent);

	public abstract String getName();

	public abstract String getMessage();

	public abstract void setMessage(String key, String msg);

	public abstract void sendMessage(Player p);
}
