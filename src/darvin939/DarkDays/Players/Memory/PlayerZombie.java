package darvin939.DarkDays.Players.Memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class PlayerZombie {
	private static HashMap<UUID, ArrayList<ItemStack>> pz = new HashMap<UUID, ArrayList<ItemStack>>();

	public static void add(LivingEntity datZombie, ArrayList<ItemStack> items) {
		pz.put(datZombie.getUniqueId(), items);
	}

	public static ArrayList<ItemStack> getInventory(Entity zombie) {
		return pz.get(zombie.getUniqueId());
	}

	public static void kill(Entity zombie) {
		pz.remove(zombie.getUniqueId());
	}

	public static HashMap<UUID, ArrayList<ItemStack>> getPZ() {
		return pz;
	}
}
