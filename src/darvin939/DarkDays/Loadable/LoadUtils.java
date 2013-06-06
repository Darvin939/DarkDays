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
		if (Randomizer.isCPercent(percent)) {
			int r = new Random().nextInt(Randomizer.getPeriod(percent)[1]) + Randomizer.getPeriod(percent)[0];
			System.out.println("Okey :"+r);
			if (percent >= r) {
				return true;
			}
		}
		return false;
	}
}
