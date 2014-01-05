package darvin939.DarkDays.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.H2;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config.Nodes;

public class DBInitLite {
	public static Database DATABASE;
	Logger log = Logger.getLogger("Minecraft");
	String prefix = "[DarkDays Database]";
	String host = Nodes.MYSQL_HOST.toString();
	Integer port = Nodes.MYSQL_PORT.getInteger();
	String database = Nodes.MYSQL_DATABASE.getString();
	String username = Nodes.MYSQL_USER.getString();
	String pass = Nodes.MYSQL_PASS.toString();
	String dbWrapper = Nodes.MYSQL_DBWRAPPER.getString();
	String datapath = DarkDays.getDataPath();

	public DBInitLite() {
		if (DATABASE != null) {
			DATABASE.close();
		}
		Database db = null;

		if (dbWrapper.equalsIgnoreCase("mysql")) {
			db = new MySQL(log, prefix, host, port, database, username, pass);

		}
		if (dbWrapper.equalsIgnoreCase("sqlite")) {
			db = new SQLite(log, prefix, datapath, "darkdays", ".sqlite");

		}
		if (dbWrapper.equalsIgnoreCase("h2")) {
			db = new H2(log, prefix, datapath, "darkdays", ".h2");

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
