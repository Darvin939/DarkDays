package darvin939.DarkDays.Chests.Sql;

import org.bukkit.Location;

public abstract class DDChest {

	protected Location loc;
	private int hashCode;

	protected DDChest(Location loc) {
		this.loc = loc;
		int result = 7;
		result = 41 * result + (loc == null ? 0 : loc.hashCode());

		hashCode = result;
	}
	
	public abstract Location getLoc();

	public abstract String getLoot();

	public abstract void addChest(String data);

	public abstract void addChest();

	public abstract void removeChest();

	public abstract void addLoot(String data);
	
	public String getName() {	
		return "Chest-" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	public int hashCode() {
		return hashCode;
	}
}
