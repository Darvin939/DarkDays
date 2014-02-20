package darvin939.DarkDays.Regions;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SignRegionData {

	private int max;
	private boolean spawn;
	private Location loc;

	private final double signX;
	private final double signZ;
	private final Integer radius;
	private final double rs;
	private final double ds;

	private int mobCount = 0;

	public SignRegionData(Location loc, int ri, int mi, boolean sb) {
		this.loc = loc;
		this.max = mi;
		this.spawn = sb;

		signX = loc.getX();
		signZ = loc.getZ();
		this.radius = ri;

		rs = (radius * radius);
		ds = Math.sqrt(0.5 * rs);
	}

	public Integer getRadius() {
		return radius;
	}

	public void addMod() {
		if (this.mobCount < this.max)
			this.mobCount += 1;
	}

	public boolean isMaxMobCount() {
		return this.max == this.mobCount;
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

		if (signX < 0) {
			x = rnd.nextInt((int) (radius * 2)) - signX - radius;
		} else {
			x = rnd.nextInt((int) (radius * 2)) + signX - radius;
		}

		if (signZ < 0) {
			z = rnd.nextInt((int) (radius * 2)) - signZ - radius;
		} else {
			z = rnd.nextInt((int) (radius * 2)) + signZ - radius;
		}

		if (isInside(x, z))
			return new Double[] { x, z };

		return null;
	}
}
