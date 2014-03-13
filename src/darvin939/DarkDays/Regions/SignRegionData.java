package darvin939.DarkDays.Regions;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class SignRegionData {

	private int max;
	private boolean spawn;
	private Location loc;

	private final double signX;
	private final double signZ;
	private final Integer radius;
	private final double rs;
	private final double ds;
	private ArrayList<UUID> mobs;
	
	public SignRegionData(Location loc, int ri, int mi, boolean sb) {
		this.loc = loc;
		this.max = mi;
		this.spawn = sb;

		signX = loc.getX();
		signZ = loc.getZ();
		this.radius = ri;

		rs = (radius * radius);
		ds = Math.sqrt(0.5 * rs);
		mobs = new ArrayList<UUID>();;
	}

	public Integer getRadius() {
		return radius;
	}

	public void addMob(Entity e) {
		if (mobs.size() < this.max)
			mobs.add(e.getUniqueId());
	}

	public void removeMob(UUID uuid) {
		if (mobs.contains(uuid))
			mobs.remove(uuid);
	}
	
	public ArrayList<UUID> getMobsUUID() {
		return mobs;
	}

	public boolean isMaxMobCount() {
		return mobs.size() == this.max;
	}

	public Integer getMax() {
		return max;
	}

	public boolean canSpawn() {
		return spawn;
	}

	public Location getLocation() {
		return loc;
	}

	public boolean isInside(double x, double z) {
		double X = Math.abs(signX - x);
		double Z = Math.abs(signZ - z);
		if ((X < ds) && (Z < ds))
			return true;
		if ((X >= radius) || (Z >= radius)) {
			return false;
		}
		return X * X + Z * Z < rs;
	}

	public Block getHighestBlock() {
		return getWorld().getHighestBlockAt(loc);
	}

	public boolean isChunkLoaded() {
		return getWorld().isChunkLoaded(loc.getChunk());
	}

	public World getWorld() {
		return loc.getWorld();
	}

	public boolean isInside(Location loc) {
		return isInside(loc.getX(), loc.getZ());
	}

	public Double[] getRandomPoint() {
		Random rnd = new Random();
		double x = 0, z = 0;

		x = rnd.nextInt((int) (radius * 2)) + signX - radius;
		z = rnd.nextInt((int) (radius * 2)) + signZ - radius;

		if (isInside(x, z))
			return new Double[] { x, z };

		return null;
	}
}
