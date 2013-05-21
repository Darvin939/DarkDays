package darvin939.DarkDays.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.DBMS;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Factory.DatabaseConfig;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;
import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config.Nodes;

public class DBInit {
	public static Database DATABASE;

	public DBInit() {
		if (DATABASE != null) {
			DATABASE.close();
		}
		final DatabaseConfig config = new DatabaseConfig();
		Database db = null;
		final String dbWrapper = Nodes.MYSQL_DBWRAPPER.getString();
		if (dbWrapper.equalsIgnoreCase("mysql")) {
			config.setType(DBMS.MySQL);
			try {
				config.setParameter(DatabaseConfig.Parameter.HOSTNAME, Nodes.MYSQL_HOST.getString());
				config.setParameter(DatabaseConfig.Parameter.PASSWORD, Nodes.MYSQL_PASS.getString());
				config.setParameter(DatabaseConfig.Parameter.USERNAME, Nodes.MYSQL_USER.getString());
				config.setParameter(DatabaseConfig.Parameter.PORTNMBR, Nodes.MYSQL_PORT.toString());
				config.setParameter(DatabaseConfig.Parameter.DATABASE, Nodes.MYSQL_DATABASE.getString());
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else if (dbWrapper.equalsIgnoreCase("sqlite")) {
			config.setType(DBMS.SQLite);
			try {
				config.setParameter(DatabaseConfig.Parameter.LOCATION, DarkDays.getDataPath());
				config.setParameter(DatabaseConfig.Parameter.FILENAME, "darkdays");
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		config.setLog(Logger.getLogger("Minecraft"));
		try {
			try {
				config.setParameter(DatabaseConfig.Parameter.PREFIX, "[DarkDays Database]");
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} catch (final NullPointerException e1) {

		}

		if (config.getType() == null) {
			db = null;
		} else {
			try {
				db = DatabaseFactory.createDatabase(config);
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		DATABASE = db;
	}

	public static PreparedStatement prepare(String query) {
		try {
			return DATABASE.prepare(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PreparedStatement prepareStatement(String query, int statement) {
		try {
			return DATABASE.getConnection().prepareStatement(query, statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet query(String query) {
		try {
			return DATABASE.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
