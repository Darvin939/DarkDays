package darvin939.DarkDays.Loadable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.Rnd;

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
		int percent = DarkDays.getEffectManager().getEffects().get(effect).getPercent();
		return Rnd.get(percent);
	}
	

}
