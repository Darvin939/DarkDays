package darvin939.DeprecAPI;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BlockAPI {

	public static Block getTargetBlock(Player player, int sightrange) throws NullPointerException {
		Location location = player.getEyeLocation();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		Vector direction = location.getDirection().normalize();
		double vx = direction.getX();
		double vy = direction.getY();
		double vz = direction.getZ();

		for (int i = 0; i < sightrange; i++) {
			x += vx;
			y += vy;
			z += vz;
			location.setX(x);
			location.setY(y);
			location.setZ(z);
			Block block = location.getBlock();
			if (block.getType() == Material.AIR)
				continue;
			return block;
		}
		return location.getBlock();
	}
}
