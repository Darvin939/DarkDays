package darvin939.DarkDays.Sql.Players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import darvin939.DarkDays.Utils.PatPeter.SQLibrary.Database;

public class SQLPlayer extends DDPlayer {
	private static PreparedStatement INSERT_PLAYER;
	private static PreparedStatement DELETE_PLAYER;
	private static PreparedStatement INSERT_DATA;
	private static PreparedStatement DELETE_DATA;
	private static PreparedStatement INSERT_EFFECTS;
	private static PreparedStatement DELETE_EFFECTS;
	private static PreparedStatement GET_PLAYER;
	private static PreparedStatement GET_DATA;
	private static PreparedStatement GET_EFFECTS;
	private static PreparedStatement UPDATE_DATA;
	private static PreparedStatement UPDATE_EFFECTS;
	private Location playerloc = null;
	private String effects = "";
	private SQLPlayerData data = null;
	private Long id;

	public SQLPlayer(String name, Long id) {
		super(name);
		this.id = id;
		init();
	}

	public SQLPlayer(Player player, Long id) {
		super(player);
		this.id = id;
		init();
	}

	public static void createTables() {
		Database.DATABASE
				.query("CREATE TABLE IF NOT EXISTS `players_data` (`id` INT NOT NULL AUTO_INCREMENT ,`username` VARCHAR(45) NOT NULL ,`hunger` INT NOT NULL DEFAULT 209999 ,`death` VARCHAR(10) NOT NULL DEFAULT 0 ,`novice` VARCHAR(10) NOT NULL DEFAULT 1 ,`spawned` VARCHAR(10) NOT NULL DEFAULT 0 ,PRIMARY KEY (`id`) );");
		Database.DATABASE
				.query("CREATE TABLE IF NOT EXISTS `players` (`id` INT NOT NULL AUTO_INCREMENT ,`username` VARCHAR(45) NOT NULL ,`x` DOUBLE DEFAULT NULL ,`y` DOUBLE DEFAULT NULL ,`z` DOUBLE DEFAULT NULL ,`yaw` FLOAT DEFAULT NULL ,`pitch` FLOAT DEFAULT NULL ,`world` VARCHAR(45) DEFAULT NULL ,PRIMARY KEY (`id`) );");
		Database.DATABASE.query("CREATE TABLE IF NOT EXISTS `players_effects` (`id` INT NOT NULL AUTO_INCREMENT ,`username` VARCHAR(45) NOT NULL ,`key` VARCHAR(45) NOT NULL ,PRIMARY KEY (`id`) );");
	}

	public static void initPrep() {
		INSERT_PLAYER = Database.DATABASE.prepare("REPLACE INTO `players` (`username`, `x`, `y`, `z`, `yaw`,  `pitch`, `world`) VALUES (?,?,?,?,?,?,?)");
		DELETE_PLAYER = Database.DATABASE.prepare("DELETE FROM `players` WHERE `id`=?");
		INSERT_DATA = Database.DATABASE.prepare("REPLACE INTO `players_data` (`username`, `hunger`, `death`, `novice`, `spawned`) VALUES (?,?,?,?,?)");
		DELETE_DATA = Database.DATABASE.prepare("DELETE FROM `players_data` WHERE `id`=?");
		INSERT_EFFECTS = Database.DATABASE.prepare("REPLACE INTO `players_effects` (`username`, `key`) VALUES (?,?)");
		DELETE_EFFECTS = Database.DATABASE.prepare("DELETE FROM `players_effects` WHERE `id`=?");

		UPDATE_DATA = Database.DATABASE.prepare("UPDATE `players_data` SET `hunger` = ?, `death` = ?, `novice` = ?, `spawned` = ? WHERE `players_data`.`id` = ?;");
		UPDATE_EFFECTS = Database.DATABASE.prepare("UPDATE `players_effects` SET `key` = ? WHERE `players_effects`.`id` = ?;");

		GET_PLAYER = Database.DATABASE.prepare("SELECT `x`,`y`,`z`,`yaw`,`pitch`,`world` FROM `players` WHERE `id` = ?");
		GET_DATA = Database.DATABASE.prepare("SELECT `hunger`,`death`,`novice`,`spawned` FROM `players_data` WHERE `id` = ?");
		GET_EFFECTS = Database.DATABASE.prepare("SELECT `key` FROM `players_effects` WHERE `id` = ?");
	}

	private void updateData(String[] data) {
		synchronized (UPDATE_DATA) {
			try {
				UPDATE_DATA.clearParameters();
				UPDATE_DATA.setInt(1, Integer.parseInt(data[0]));
				UPDATE_DATA.setString(2, data[1]);
				UPDATE_DATA.setString(3, data[2]);
				UPDATE_DATA.setString(4, data[3]);
				UPDATE_DATA.setLong(5, id);
				synchronized (UPDATE_DATA.getConnection()) {
					UPDATE_DATA.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateEffects(String data) {
		synchronized (UPDATE_EFFECTS) {
			try {
				UPDATE_EFFECTS.clearParameters();
				UPDATE_EFFECTS.setString(1, data);
				UPDATE_EFFECTS.setLong(2, id);
				synchronized (UPDATE_EFFECTS.getConnection()) {
					UPDATE_EFFECTS.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void init() {
		synchronized (GET_PLAYER) {
			try {
				GET_PLAYER.clearParameters();
				GET_PLAYER.setLong(1, id);
				ResultSet rs;
				synchronized (GET_PLAYER.getConnection()) {
					rs = GET_PLAYER.executeQuery();
				}
				if (rs.next()) {
					final String worldName = rs.getString("world");
					if (worldName != null && !worldName.isEmpty()) {
						final World world = Bukkit.getWorld(worldName);
						if (world != null) {
							playerloc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
						} else {

						}
					}
				}
			} catch (final SQLException e) {

			}
		}
		synchronized (GET_DATA) {
			try {
				GET_DATA.clearParameters();
				GET_DATA.setLong(1, id);
				ResultSet rs;
				synchronized (GET_DATA.getConnection()) {
					rs = GET_DATA.executeQuery();
				}
				if (rs.next()) {
					data = new SQLPlayerData(rs.getInt("hunger"), Boolean.parseBoolean(rs.getString("death")), Boolean.parseBoolean(rs.getString("novice")), Boolean.parseBoolean(rs.getString("spawned")));
				}else
					data = new SQLPlayerData(209999, false, true, false);
			} catch (final SQLException e) {

			}
		}
		synchronized (GET_EFFECTS) {
			try {
				GET_EFFECTS.clearParameters();
				GET_EFFECTS.setLong(1, id);
				ResultSet rs;
				synchronized (GET_EFFECTS.getConnection()) {
					rs = GET_EFFECTS.executeQuery();
				}
				if (rs.next()) {
					effects = rs.getString("key");
				}
			} catch (final SQLException e) {

			}
		}
	}

	public Location getLoc() {
		return playerloc;
	}

	public SQLPlayerData getData() {
		return data;
	}

	public String getEffects() {
		return effects;
	}

	public void addPlayer() {
		synchronized (INSERT_PLAYER) {
			try {
				INSERT_PLAYER.clearParameters();
				INSERT_PLAYER.setString(1, name);
				INSERT_PLAYER.setDouble(2, player.getLocation().getX());
				INSERT_PLAYER.setDouble(3, player.getLocation().getY());
				INSERT_PLAYER.setDouble(4, player.getLocation().getZ());
				INSERT_PLAYER.setFloat(5, player.getLocation().getYaw());
				INSERT_PLAYER.setFloat(6, player.getLocation().getPitch());
				INSERT_PLAYER.setString(6, player.getWorld().getName());
				synchronized (INSERT_PLAYER.getConnection()) {
					INSERT_PLAYER.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void removePlayer() {

		synchronized (DELETE_PLAYER) {
			try {
				DELETE_PLAYER.clearParameters();
				DELETE_PLAYER.setLong(1, id);
				synchronized (DELETE_PLAYER.getConnection()) {
					DELETE_PLAYER.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addData(String[] data) {
		synchronized (GET_DATA) {
			try {
				GET_DATA.clearParameters();
				GET_DATA.setLong(1, id);
				ResultSet rs;
				synchronized (GET_DATA.getConnection()) {
					rs = GET_DATA.executeQuery();
				}
				if (rs.next()) {
					updateData(data);
				} else {
					synchronized (INSERT_DATA) {
						try {
							INSERT_DATA.clearParameters();
							INSERT_DATA.setString(1, name);
							INSERT_DATA.setInt(2, Integer.parseInt(data[0]));
							INSERT_DATA.setString(3, data[1]);
							INSERT_DATA.setString(4, data[2]);
							INSERT_DATA.setString(5, data[3]);
							synchronized (INSERT_DATA.getConnection()) {
								INSERT_DATA.executeUpdate();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (final SQLException e) {

			}
		}

	}

	public void removeData() {

		synchronized (DELETE_DATA) {
			try {
				DELETE_DATA.clearParameters();
				DELETE_DATA.setLong(1, id);
				synchronized (DELETE_DATA.getConnection()) {
					DELETE_DATA.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEffects(String data) {
		synchronized (GET_EFFECTS) {
			try {
				GET_EFFECTS.clearParameters();
				GET_EFFECTS.setLong(1, id);
				ResultSet rs;
				synchronized (GET_EFFECTS.getConnection()) {
					rs = GET_EFFECTS.executeQuery();
				}
				if (rs.next()) {
					updateEffects(data);
				} else {
					synchronized (INSERT_EFFECTS) {
						try {
							INSERT_EFFECTS.clearParameters();
							INSERT_EFFECTS.setString(1, name);
							INSERT_EFFECTS.setString(2, data);
							synchronized (INSERT_EFFECTS.getConnection()) {
								INSERT_EFFECTS.executeUpdate();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (final SQLException e) {

			}
		}
	}

	public void removeEffects() {

		synchronized (DELETE_EFFECTS) {
			try {
				DELETE_EFFECTS.clearParameters();
				DELETE_EFFECTS.setLong(1, id);
				synchronized (DELETE_EFFECTS.getConnection()) {
					DELETE_EFFECTS.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
