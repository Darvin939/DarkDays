package darvin939.DarkDays.Chests.Sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import darvin939.DarkDays.Utils.PatPeter.SQLibrary.Database;

public class SQLChest extends DDChest {

	private static PreparedStatement DELETE_CHEST;
	private static PreparedStatement INSERT_CHEST;
	private static PreparedStatement UPDATE_LOOT;
	private static PreparedStatement GET_CHEST;
	private static PreparedStatement GET_LOOT;
	private Long id;
	private Location chestloc = null;
	private String loot = "";

	public SQLChest(Location loc, Long id) {
		super(loc);
		this.id = id;
		init();
	}

	public static void createTables() {
		Database.DATABASE.query("CREATE TABLE IF NOT EXISTS `chests` (`id` INT NOT NULL AUTO_INCREMENT ,`x` DOUBLE NOT NULL ,`y` DOUBLE NOT NULL ,`z` DOUBLE NOT NULL ,`loot_id` VARCHAR(45) DEFAULT NULL, `world` VARCHAR(45) NOT NULL ,PRIMARY KEY (`id`) );");
	}

	public static void initPrep() {
		INSERT_CHEST = Database.DATABASE.prepare("REPLACE INTO `chests` (`x`, `y`, `z`, `loot_id`, `world`) VALUES (?,?,?,?,?)");
		DELETE_CHEST = Database.DATABASE.prepare("DELETE FROM `chests` WHERE `id`=?");

		UPDATE_LOOT = Database.DATABASE.prepare("UPDATE `chests` SET `loot_id` = ? WHERE `chests`.`id` = ?;");

		GET_CHEST = Database.DATABASE.prepare("SELECT `x`,`y`,`z`,`world` FROM `chests` WHERE `id` = ?");
		GET_LOOT = Database.DATABASE.prepare("SELECT `loot_id` FROM `chests` WHERE `id` = ?");
	}

	public void addLoot(String data) {
		synchronized (GET_LOOT) {
			try {
				GET_LOOT.clearParameters();
				GET_LOOT.setLong(1, id);
				ResultSet rs;
				synchronized (GET_LOOT.getConnection()) {
					rs = GET_LOOT.executeQuery();
				}
				if (rs.next()) {
					updateLoot(data);
				}
			} catch (final SQLException e) {

			}
		}

	}

	private void updateLoot(String data) {
		synchronized (UPDATE_LOOT) {
			try {
				UPDATE_LOOT.clearParameters();
				UPDATE_LOOT.setString(1, data);
				UPDATE_LOOT.setLong(2, id);
				synchronized (UPDATE_LOOT.getConnection()) {
					UPDATE_LOOT.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addChest(String data) {
		synchronized (INSERT_CHEST) {
			try {
				INSERT_CHEST.clearParameters();
				INSERT_CHEST.setDouble(1, loc.getX());
				INSERT_CHEST.setDouble(2, loc.getY());
				INSERT_CHEST.setDouble(3, loc.getZ());
				INSERT_CHEST.setString(4, data.isEmpty() ? "" : data);
				INSERT_CHEST.setString(5, loc.getWorld().getName());
				synchronized (INSERT_CHEST.getConnection()) {
					INSERT_CHEST.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addChest() {
		addChest("");
	}

	public void removeChest() {

		synchronized (DELETE_CHEST) {
			try {
				DELETE_CHEST.clearParameters();
				DELETE_CHEST.setLong(1, id);
				synchronized (DELETE_CHEST.getConnection()) {
					DELETE_CHEST.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void init() {
		synchronized (GET_CHEST) {
			try {
				GET_CHEST.clearParameters();
				GET_CHEST.setLong(1, id);
				ResultSet rs;
				synchronized (GET_CHEST.getConnection()) {
					rs = GET_CHEST.executeQuery();
				}
				if (rs.next()) {
					final String worldName = rs.getString("world");
					if (worldName != null && !worldName.isEmpty()) {
						final World world = Bukkit.getWorld(worldName);
						if (world != null) {
							chestloc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
						}
					}
				}
			} catch (final SQLException e) {

			}
		}
		synchronized (GET_LOOT) {
			try {
				GET_LOOT.clearParameters();
				GET_LOOT.setLong(1, id);
				ResultSet rs;
				synchronized (GET_LOOT.getConnection()) {
					rs = GET_LOOT.executeQuery();
				}
				if (rs.next()) {
					loot = rs.getString("loot_id");
				}
			} catch (final SQLException e) {

			}
		}
	}

	public Location getLoc() {
		return chestloc;
	}

	public String getLoot() {
		return loot;
	}

}
