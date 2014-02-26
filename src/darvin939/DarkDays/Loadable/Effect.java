package darvin939.DarkDays.Loadable;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Players.Memory.PlayerData;
import darvin939.DarkDays.Utils.Util;

public class Effect extends LoadUtils implements AbsEffect {
	protected final String name;
	private int percent;
	protected final DarkDays plugin;
	private int delay;
	private int power;
	private List<?> list;
	private String msg;
	private String msgR;

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

	@Override
	public void setMessage(String key, String msg) {
		this.msg = key;
		plugin.getConfiguration().addMSG(key, msg);
	}

	@Override
	public void setMessage(String key, List<?> list) {
		int count = 0;
		this.list = list;
		this.msgR = key + "_";
		for (Object s : list) {
			count++;
			plugin.getConfiguration().addMSG(key + "_" + count, String.valueOf(s));
		}
	}

	public String getMessage() {
		return msg;
	}

	public String getMessageR() {
		return msgR;
	}

	@Override
	public void sendMessage(Player p) {
		Util.PrintMSGPx(p, msg);
	}

	public void randomMessage(Player p) {
		if (PlayerData.isPlaying(p)) {
			Random r = new Random();
			if (r.nextBoolean()) {
				Util.PrintMSG(p, msgR + (r.nextInt(list.size() - 1) + 1));
			}
		}
	}
}
