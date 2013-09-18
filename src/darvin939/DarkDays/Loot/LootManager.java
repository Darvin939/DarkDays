package darvin939.DarkDays.Loot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.Utils.Util;

public class LootManager {
	private static FileConfiguration cfg = Config.getLC().getCfg();

	{
		cfg = Config.getLC().getCfg();
	}

	public static List<String> getEffects(String list, String item) {
		return cfg.getStringList(list + ".items." + item + ".effects");
	}

	public static List<String> getItemList(String list) {
		Set<String> keys = cfg.getConfigurationSection(list + ".items").getKeys(false);
		return new ArrayList<String>(keys);
	}

	public static Material getMaterial(String stringMaterial) {
		if (Util.isInteger(stringMaterial)) {
			return Material.getMaterial(Integer.parseInt(stringMaterial));
		}
		return Material.getMaterial(stringMaterial.toUpperCase());
	}

	private static ItemStack[] shuffleItems(Chest chest, ItemStack[] items) {
		ItemStack[] storableItems = new ItemStack[chest.getInventory().getSize()];
		int slot = 0;
		for (int i = 0; i < items.length; i++) {
			if ((items[i] != null) && (slot < storableItems.length)) {
				storableItems[slot] = items[i];
				slot++;
			}
		}
		Collections.shuffle(Arrays.asList(storableItems));
		return storableItems;
	}

	public static void delChestOtherItems(Chest chest, ItemStack item) {
		int id = 0;
		for (ItemStack i : chest.getInventory().getContents()) {
			if (i != null && i.getType() != null && !i.getType().equals(Material.AIR)) {
				if (i.getType() == item.getType()) {
					chest.getInventory().setItem(id, new ItemStack(Material.AIR));
				}
			}
			if (id < chest.getInventory().getContents().length - 1)
				id++;
		}
	}

	public static boolean isChestEmpty(Block block) {
		boolean ret = true;
		if (block.getState() instanceof Chest) {
			Chest chest = (Chest) block.getState();
			boolean skip = false;
			for (ItemStack item : chest.getInventory().getContents()) {
				if (item != null && item.getType() != null && !item.getType().equals(Material.AIR)) {
					for (String list : getItemList(Config.getCC().getLoot(block.getLocation()))) {
						if (Util.isInteger(list)) {
							if (item.getType() == Material.getMaterial(Integer.valueOf(list))) {
								ret = false;
								skip = true;
							}
						} else {
							if (item.getType() == Material.getMaterial(list.toUpperCase())) {
								ret = false;
								skip = true;
							}
						}
					}
					if (!skip) {
						delChestOtherItems(chest, item);
					}
					skip = false;
				}
			}
		}
		return ret;
	}

	public static void fillTask() {
		for (Entry<Location, String> set : Config.getCC().getChests().entrySet()) {
			Block block = set.getKey().getBlock();
			if (block.getChunk().isLoaded() && !set.getValue().isEmpty()) {
				Chest chest;
				if (!(block.getState() instanceof Chest)) {
					Location chestloc = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
					chestloc.getBlock().setType(Material.CHEST);
					chest = (Chest) chestloc.getBlock().getState();
				} else
					chest = (Chest) block.getState();
				if (cfg.contains(Util.FCTU(Config.getCC().getLoot(set.getKey()))))
					if (isChestEmpty(block) && Nodes.chest_empty.getBoolean())
						fillChest(chest, Config.getCC().getLoot(set.getKey()));
			} else if (!(block.getState() instanceof Chest)) {
				Location chestloc = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
				chestloc.getBlock().setType(Material.CHEST);
			}
		}
	}

	public static void setLoot(Player p, String list) {
		list = Util.FCTU(list);
		if (cfg.isConfigurationSection(list)) {
			Block block = p.getTargetBlock(null, 10);
			if (block.getState() instanceof Chest) {
				Config.getCC().setLoot(block.getLocation(), list);
				fillChest((Chest) block.getState(), list);
				Config.FGU.PrintMSG(p, "loot_set", list);
			}
		} else
			Util.PrintPxMSG(p, "loot_error");
	}

	private static void fillChest(Chest chest, String list) {
		chest.getInventory().setContents(shuffleItems(chest, getContents(list)));
	}

	public static int[] getItemAmount(String list, String item) {
		String[] properties = cfg.getString(list + ".items." + item + ".spawn").split(",");
		String[] stringAmount = properties[1].split("-");
		int[] amount = new int[stringAmount.length];
		for (int i = 0; i < amount.length; i++) {
			amount[i] = Integer.parseInt(stringAmount[i]);
		}
		return amount;
	}

	public static int getItemChance(String list, String item) {
		String[] properties = cfg.getString(list + ".items." + item + ".spawn").split(",");
		return Integer.parseInt(properties[0]);
	}

	public static short getDurability(String list, String item) {
		return (short) cfg.getInt(list + ".items." + item + ".durability");
	}

	public static ItemStack[] getContents(String list) {
		List<String> itemList = getItemList(list);
		Random random = new Random();
		ItemStack[] items = new ItemStack[itemList.size()];
		for (int i = 0; i < itemList.size(); i++) {
			String item = itemList.get(i);
			if (cfg.isSet(list + ".items." + item + ".spawn") && !cfg.getString(list + ".items." + item + ".spawn").isEmpty()) {
				Material material = getMaterial(item);

				int[] amountRange = getItemAmount(list, item);
				int min = amountRange[0];
				int max = amountRange[1] - amountRange[0];
				int percent = getItemChance(list, item);
				int amount = 0;

				if (max > 0) {
					max = random.nextInt(max);
				}

				amount = max + min;
				int chance = random.nextInt(100);

				if ((percent < chance) || (amount <= 0))
					continue;
				try {
					items[i] = new ItemStack(material, amount);
					items[i].setDurability(getDurability(list, item));

					if (material.equals(Material.POTION))
						applyEffect(items[i], new ArrayList<String>(getEffects(list, item)));
					else {
						enchant(items[i], new ArrayList<String>(getEffects(list, item)));
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}
		return items;
	}

	private static void applyEffect(ItemStack item, ArrayList<String> list) {
		Random random = new Random();
		ArrayList<Object[]> effects = new ArrayList<Object[]>();
		for (int i = 0; i < list.size(); i++) {
			String[] properties = ((String) list.get(i)).split(",");
			if (properties.length == 4) {
				PotionType effect = PotionType.valueOf(properties[0].toUpperCase());
				if (effect != null) {
					String[] range = properties[1].split("-");
					int minLvl = Integer.parseInt(range[0]);
					int maxLvl = Integer.parseInt(range[1]);
					// int chance = Integer.parseInt(properties[2]);
					boolean splash = Boolean.parseBoolean(properties[2]);
					boolean extend = Boolean.parseBoolean(properties[3]);
					Object[] potion = { effect, Integer.valueOf(minLvl), Integer.valueOf(maxLvl), Boolean.valueOf(splash), Boolean.valueOf(extend) };
					// Object[] potion = { effect, Integer.valueOf(minLvl),
					// Integer.valueOf(maxLvl), Integer.valueOf(chance),
					// Boolean.valueOf(splash), Boolean.valueOf(extend) };
					effects.add(potion);
				}
			}
		}
		if (effects.isEmpty()) {
			return;
		}
		int index = random.nextInt(effects.size());
		PotionType effect = (PotionType) ((Object[]) effects.get(index))[0];
		int minLvl = ((Integer) ((Object[]) effects.get(index))[1]).intValue();
		int maxLvl = ((Integer) ((Object[]) effects.get(index))[2]).intValue();
		int lvl = minLvl;
		// int chance = ((Integer) ((Object[])
		// effects.get(index))[3]).intValue();
		boolean splash = ((Boolean) ((Object[]) effects.get(index))[3]).booleanValue();
		boolean extend = ((Boolean) ((Object[]) effects.get(index))[4]).booleanValue();

		if (minLvl != maxLvl) {
			lvl = minLvl + random.nextInt(maxLvl - minLvl);
		}
		if (lvl > effect.getMaxLevel())
			lvl = effect.getMaxLevel();
		else if (lvl < 1) {
			lvl = 1;
		}
		// if (random.nextInt(100) <= chance) {
		Potion potion = new Potion(effect, lvl);
		if (splash) {
			potion.splash();
		}
		if (extend) {
			potion.extend();
		}
		potion.apply(item);
		// }
	}

	private static List<Enchantment> availableEnchantments(ItemStack item) {
		List<Enchantment> enchantments = new ArrayList<Enchantment>();
		for (int i = 0; i < Enchantment.values().length; i++) {
			if (Enchantment.values()[i].canEnchantItem(item)) {
				enchantments.add(Enchantment.values()[i]);
			}
		}

		return enchantments;
	}

	private static void enchant(ItemStack item, ArrayList<String> enchantments) {
		Random random = new Random();
		try {
			List<Enchantment> availableEnchantments = availableEnchantments(item);

			for (int j = 0; j < enchantments.size(); j++) {
				String[] properties = ((String) enchantments.get(j)).split(",");
				if (properties.length == 3) {
					String[] range = properties[1].split("-");
					Enchantment enchantment = Enchantment.getByName(properties[0].toUpperCase());
					if (availableEnchantments.contains(enchantment)) {
						int chance = Integer.parseInt(properties[2]);
						int minLvl = Integer.parseInt(range[0]);
						int maxLvl = Integer.parseInt(range[1]);
						int lvl = minLvl;
						if (minLvl != maxLvl) {
							lvl = minLvl + random.nextInt(maxLvl - minLvl);
						}
						if (lvl > enchantment.getMaxLevel())
							lvl = enchantment.getMaxLevel();
						else if (lvl < enchantment.getStartLevel()) {
							lvl = enchantment.getStartLevel();
						}
						if (random.nextInt(100) <= chance)
							item.addEnchantment(enchantment, lvl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
