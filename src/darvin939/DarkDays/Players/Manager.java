package darvin939.DarkDays.Players;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.Configuration.Config;

public class Manager {

	public static void minusOne(Player p) {
		int am = p.getItemInHand().getAmount();
		if (am > 1)
			p.getItemInHand().setAmount(am - 1);
		else
			p.setItemInHand(new ItemStack(Material.AIR, 0));
	}

	public boolean isPercent(String effect) {
		try {
			Random random = new Random();
			int percent;
			percent = Config.getEffectManager().getEffects().get(effect).getPercent();
			int chance = random.nextInt(98) + 1;
			if (percent > chance) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
