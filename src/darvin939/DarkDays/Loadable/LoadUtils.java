package darvin939.DarkDays.Loadable;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;

public class LoadUtils {
	// For items
	public void minusOne(Player p) {
		int am = p.getItemInHand().getAmount();
		if (am > 1)
			p.getItemInHand().setAmount(am - 1);
		else
			p.setItemInHand(new ItemStack(Material.AIR, 0));
	}

	// For effects
	public boolean isPercent(String effect) {
		try {
			Random random = new Random();
			int percent;
			percent = DarkDays.getEffectManager().getEffects().get(effect).getPercent();
			int chance = random.nextInt(98) + 1;
			if (percent > chance) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void createConfig(DarkDays plugin, Class<?> clazz) {
		plugin.getConfiguration().SC(clazz.getSuperclass().getSimpleName());
		
		//cfgSpawnFile = new File(plugin.getDataFolder() + "/spawns.yml");
		//cfgSpawn = YamlConfiguration.loadConfiguration(cfgSpawnFile);
	}
}
