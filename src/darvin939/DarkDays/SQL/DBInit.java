package darvin939.DarkDays.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.DBMS;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Factory.DatabaseConfig;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;
import lib.PatPeter.SQLibrary.Factory.Parameter;
import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config.Nodes;

public class DBInit {
	public static Database DATABASE;

	public DBInit() {
		if (DATABASE != null) {
			DATABASE.close();
		}
		DatabaseConfig config = new DatabaseConfig();
		Database db = null;
		final String dbWrapper = Nodes.MYSQL_DBWRAPPER.getString();
		if (dbWrapper.equalsIgnoreCase("mysql")) {
			config.setType(DBMS.MySQL);
			try {
				config.setParameter(Parameter.HOST, Nodes.MYSQL_HOST.getString());
				config.setParameter(Parameter.PASSWORD, String.valueOf(Nodes.MYSQL_PASS.getInteger()));
				config.setParameter(Parameter.USERNAME, Nodes.MYSQL_USER.getString());
				config.setParameter(Parameter.PORT, Nodes.MYSQL_PORT.toString());
				config.setParameter(Parameter.DATABASE, Nodes.MYSQL_DATABASE.getString());
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else if (dbWrapper.equalsIgnoreCase("sqlite")) {
			config.setType(DBMS.SQLite);
			try {
				config.setParameter(Parameter.LOCATION, DarkDays.getDataPath());
				config.setParameter(Parameter.FILENAME, "darkdays");
			} catch (lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		config.setLog(Logger.getLogger("Minecraft"));
		try {
			try {
				config.setParameter(Parameter.PREFIX, "[DarkDays Database]");
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
