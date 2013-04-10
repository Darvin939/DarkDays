package darvin939.DarkDays.Players;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import darvin939.DarkDays.DarkDays;

public class Effect implements AbsEffect {
	private String name;
	private String description;
	private int percent;
	private DarkDays plugin;
	private int delay;

	public Effect(DarkDays plugin, String name) {
		this.name = name;
		this.plugin = plugin;
	}

	public String getDescription() {
		return description;
	}

	public int getEffectDelay() {
		return delay;
	}

	public void setEffectDelay(int delay) {
		this.delay = delay;
	}

	public int getPercent() {
		return percent;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public String getName() {
		return name;
	}

	public Integer run() {
		return plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (EffectManager.getEffect(p, name) != null) {
						// msg("applyeffect");
						p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), 200, 1));
					}
				}
			}
		}, 200, 200);
	}
}
