package darvin939.DarkDays.Loadable;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Tasks;
import darvin939.DarkDays.Configuration.Config;

public class Item extends LoadUtils implements AbsItem {
	private String name;
	private String description;
	private DarkDays plugin;
	private String drinkmsg;
	private String material;

	public Item(DarkDays plugin, String name) {
		this.name = name;
		this.plugin = plugin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}
	
	public boolean isPercent() {
		return isPercent(name);
	}

	public void setDrinkMSG(String drinkmsg) {
		this.drinkmsg = drinkmsg;
	}

	public String getDrinkMSG() {
		return drinkmsg;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMaterial() {
		return material;
	}

	public void drink(final Player p, final int hs) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if (p.getInventory().getItem(hs).getType() == Material.getMaterial(getMaterial())) {
					if (((Integer) Tasks.player_hunger.get(p)).intValue() > 15000)
						Tasks.player_hunger.put(p, Integer.valueOf(209999));
					else {
						Tasks.player_hunger.put(p, Integer.valueOf(((Integer) Tasks.player_hunger.get(p)).intValue() + 5000));
					}
					Config.FGU.PrintPxMsg(p, getDrinkMSG());
				}
			}
		}, 35);
	}

}
