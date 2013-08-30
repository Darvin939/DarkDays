package darvin939.DarkDays.Loadable;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Randomizer;

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
		int percent;
		percent = DarkDays.getEffectManager().getEffects().get(effect).getPercent();
		if (Randomizer.isPercent(percent)) {
			Integer[] i = Randomizer.getPeriod(percent);
			int r = new Random().nextInt(i[1]) + i[0];
			if (percent >= r) {
				return true;
			}
		}
		return false;
	}
}
