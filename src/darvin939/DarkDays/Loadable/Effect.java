package darvin939.DarkDays.Loadable;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;

public class Effect extends LoadUtils implements AbsEffect {
	protected final String name;
	private int percent;
	protected final DarkDays plugin;
	private int delay;
	private int power;

	public Effect(DarkDays plugin, String name) {
		this.name = name;
		this.plugin = plugin;
	}

	@Override
	public int getDelay() {
		return delay;
	}

	@Override
	public void setDelay(int delay) {
		this.delay = delay;
	}

	@Override
	public int getPercent() {
		return percent;
	}

	@Override
	public void setPercent(int percent) {
		this.percent = percent;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isPercent() {
		return isPercent(name);
	}

	@Override
	public void setTime(int power) {
		this.power = power;
	}

	@Override
	public int getTime() {
		return power;
	}

	public boolean isEffect(Player p) {
		if (DarkDays.getEffectManager().isEffect(p, getName()))
			return true;
		return false;
	}

	public void addEffect(Player p, Integer id) {
		DarkDays.getEffectManager().setEffect(p, getName(), id);
	}
}
