package darvin939.DarkDays.Loadable;

import java.util.List;

import org.bukkit.entity.Player;

public abstract interface AbsEffect {

	public abstract int getPercent();

	public abstract int getDelay();

	public abstract void setTime(int power);

	public abstract int getTime();

	public abstract void setDelay(int delay);

	public abstract void setPercent(int percent);

	public abstract String getName();

	public abstract void setMessage(String key, String msg);

	public abstract void sendMessage(Player p);

	public abstract void setMessage(String key, List<?> list);
}
