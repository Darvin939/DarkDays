package darvin939.DarkDays.Loot;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class Data {
	Map<Material, ItemData> items = new HashMap<Material, ItemData>();
	PotionData potion = null;
	String name;

	public Data(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Map<Material, ItemData> getItems() {
		return items;
	}

	public ItemData getItem(Material item) {
		if (items.containsKey(item))
			return items.get(item);
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PotionData getPotion() {
		return potion;
	}

	public void setPotion(String type, String value) {
		if (this.potion == null)
			this.potion = new PotionData("", "");
		if (type.equalsIgnoreCase("spawn"))
			this.potion.spawn = value;
		if (type.equalsIgnoreCase("effects"))
			this.potion.effects = value;
	}

	public void addItem(Material item, ItemData data) {
		this.items.put(item, data);
	}
}