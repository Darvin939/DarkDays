package darvin939.DarkDays.Sql.Chests;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import darvin939.DarkDays.Utils.PatPeter.SQLibrary.Database;

public class ChestManager {
	private static final ChestManager instance = new ChestManager();
	public final Map<Location, DDChest> chests =  new HashMap<Location, DDChest>();
	public final static Map<Location, Long> chestsID =  new HashMap<Location, Long>();
	private final PreparedStatement insertChest;

	public static ChestManager getInstance() {
		return instance;
	}

	private ChestManager() {
		insertChest = Database.DATABASE.prepare("INSERT INTO `chests` (`x`,`y`,`z`,`world`) VALUES (?,?,?,?)");
	}

	public static void init() {
		final ResultSet rs = Database.DATABASE.query("SELECT `x`,`y`,`z`,`world`,`id` FROM `chests`");
		try {
			while (rs.next()) {
				final String worldName = rs.getString("world");
				if (worldName != null && !worldName.isEmpty()) {
					final World world = Bukkit.getWorld(worldName);
					if (world != null) {
						chestsID.put(new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")), rs.getLong("id"));
					}
				}
			}
			rs.close();
		} catch (final SQLException e) {
		}
	}

	public DDChest addChest(Location loc) {
		addExtChest(loc);
		final Long id = getChestID(loc);
		DDChest chest = new SQLChest(loc, id);
		chests.put(loc, chest);
		return chest;
	}

	public DDChest getChest(Location loc) {
		return chests.get(loc);
	}

	public Long getChestID(Location loc) {
		return chestsID.get(loc);
	}
	
	public Map<Location, Long> getChestsID() {
		return chestsID;
	}

	private void addExtChest(Location loc) {
		if (!chestsID.containsKey(loc)) {
			ResultSet rs = null;
			try {
				synchronized (insertChest) {
					insertChest.clearParameters();
					insertChest.setDouble(1, loc.getX());
					insertChest.setDouble(2, loc.getY());
					insertChest.setDouble(3, loc.getZ());
					insertChest.setString(4, loc.getWorld().getName());
					insertChest.executeUpdate();
					rs = insertChest.getGeneratedKeys();
					if (rs.next()) {
						chestsID.put(loc, rs.getLong(1));
					}
					if (rs != null) {
						rs.close();
					}
				}
			} catch (final SQLException e) {
				if (rs != null) {
					try {
						rs.close();
					} catch (final SQLException e1) {
					}
				}
			}
		}

	}
}
