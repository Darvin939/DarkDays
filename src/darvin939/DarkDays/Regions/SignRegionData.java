package darvin939.DarkDays.Regions;

import org.bukkit.Location;

public class SignRegionData {

	private int ri, mi;
	private boolean sb;
	private Location loc;

	public SignRegionData(Location loc, int ri, int mi, boolean sb) {
		this.loc = loc;
		this.ri = ri;
		this.mi = mi;
		this.sb = sb;
	}

	public Integer getRadius() {
		return ri;
	}

	public Integer getMax() {
		return mi;
	}

	public boolean canSpawn() {
		return sb;
	}

	public Location getLocation() {
		return loc;
	}

	public boolean equals(SignRegionData srd) {
		
		if (this.loc == srd.getLocation() && this.ri == srd.getRadius() && this.sb == srd.canSpawn() && this.mi == srd.getMax()) {
			return true;
		}
		return false;
	}
}
