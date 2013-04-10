/**
 * Database Handler
 * Abstract superclass for all subclass database files.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package darvin939.DarkDays.Utils.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;

import darvin939.DarkDays.Configuration.Config.Nodes;
import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Utils.PatPeter.SQLibrary.DatabaseConfig.DatabaseType;
import darvin939.DarkDays.Utils.PatPeter.SQLibrary.DatabaseConfig.Parameter;

public abstract class Database {
	public static Database DATABASE;
	protected Logger log;
	protected final String PREFIX;
	protected final String DATABASE_PREFIX;
	protected boolean connected;
	protected Connection connection;
	static {
		initDb();
	}

	public static void initDb() {
		if (DATABASE != null) {
			DATABASE.close();
		}
		final DatabaseConfig config = new DatabaseConfig();
		Database db;
		final String dbWrapper = Nodes.MYSQL_DBWRAPPER.getString();
		if (dbWrapper.equalsIgnoreCase("mysql")) {
			config.setType(DatabaseType.MYSQL);
			try {
				config.setParameter(Parameter.HOSTNAME, Nodes.MYSQL_HOST.getString());
				config.setParameter(Parameter.PASSWORD, Nodes.MYSQL_PASS.getString());
				config.setParameter(Parameter.USER, Nodes.MYSQL_USER.getString());
				config.setParameter(Parameter.PORT_NUMBER, Nodes.MYSQL_PORT.toString());
				config.setParameter(Parameter.DATABASE, Nodes.MYSQL_DATABASE.getString());
			} catch (final NullPointerException e) {
			} catch (final InvalidConfigurationException e) {
			}
		} else if (dbWrapper.equalsIgnoreCase("sqlite")) {
			config.setType(DatabaseType.SQLITE);
			try {
				config.setParameter(Parameter.DB_LOCATION,DarkDays.getDataPath());
				config.setParameter(Parameter.DB_NAME, "darkdays");

			} catch (final NullPointerException e) {
			} catch (final InvalidConfigurationException e) {
			}
		}
		config.setLog(Logger.getLogger("Minecraft"));
		try {
			config.setParameter(Parameter.DB_PREFIX, "[DarkDays Database]");
		} catch (final NullPointerException e1) {

		} catch (final InvalidConfigurationException e1) {

		}

		try {
			if (config.getType() == null) {
				db = null;
			} else {
				db = DatabaseFactory.createDatabase(config);
			}
		} catch (final InvalidConfigurationException e) {
			//ACLogger.severe("Problem while trying to load the Database", e);
			db = null;
		}
		DATABASE = db;
		//DebugLog.INSTANCE.info("Database initialization done");
	}

	// http://dev.mysql.com/doc/refman/5.6/en/sql-syntax.html
	// http://sqlite.org/lang.html
	protected enum Statements {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data manipulation statements
		CREATE, ALTER, DROP, TRUNCATE, RENAME, // Data definition statements
		// MySQL-specific
		START, COMMIT, ROLLBACK, SAVEPOINT, LOCK, UNLOCK, // MySQL Transactional and Locking Statements
		PREPARE, EXECUTE, DEALLOCATE, // Prepared Statements
		SET, SHOW, // Database Administration
		DESCRIBE, EXPLAIN, HELP, USE, // Utility Statements
		// SQLite-specific
		ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, RELEASE, VACUUM
	}

	public int lastUpdate;

	/*
	 * MySQL, SQLite
	 */
	public Database(final Logger log, final String prefix, final String dp) {
		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp;
		this.connected = false;
		this.connection = null;
	}

	protected void writeInfo(final String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + this.DATABASE_PREFIX + toWrite);
		}
	}

	protected void writeError(final String toWrite, final boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			} else {
				this.log.warning(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		}
	}

	abstract void initialize() throws SQLException;

	public abstract void open() throws SQLException;

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (final SQLException ex) {
				this.writeError("SQL exception in close(): " + ex, true);
			}
		}
	}

	public Connection getConnection() {
		return this.connection;
	}

	public boolean checkConnection() {
		if (connection != null) {
			return true;
		}
		return false;
	}

	public abstract ResultSet query(String query);

	public PreparedStatement prepare(final String query) {
		try {
			final PreparedStatement ps;
			synchronized (connection) {
				ps = connection.prepareStatement(query);
			}
			return ps;
		} catch (final SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				this.writeError("SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return null;
	}

	protected Statements getStatement(final String query) {
		final String trimmedQuery = query.trim();
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT")) {
			return Statements.SELECT;
		} else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT")) {
			return Statements.INSERT;
		} else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE")) {
			return Statements.UPDATE;
		} else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE")) {
			return Statements.DELETE;
		} else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE")) {
			return Statements.CREATE;
		} else if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER")) {
			return Statements.ALTER;
		} else if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP")) {
			return Statements.DROP;
		} else if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE")) {
			return Statements.TRUNCATE;
		} else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME")) {
			return Statements.RENAME;
		} else if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO")) {
			return Statements.DO;
		} else if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE")) {
			return Statements.REPLACE;
		} else if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD")) {
			return Statements.LOAD;
		} else if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER")) {
			return Statements.HANDLER;
		} else if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL")) {
			return Statements.CALL;
		} else {
			return Statements.SELECT;
		}
	}

	public boolean createTable(final String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("Parameter 'query' empty or null in createTable().", true);
				return false;
			}
			synchronized (connection) {
				statement = connection.createStatement();
				statement.execute(query);
			}
			return true;
		} catch (final SQLException ex) {
			this.writeError(ex.getMessage(), true);
			return false;
		}
	}

	public boolean checkTable(final String table) {
		DatabaseMetaData dbm = null;
		try {
			synchronized (connection) {
				dbm = this.connection.getMetaData();
			}

			final ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next()) {
				return true;
			} else {
				return false;
			}
		} catch (final SQLException e) {
			this.writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
			return false;
		}
	}

	public abstract boolean wipeTable(String table);

	public abstract DatabaseType getType();

	private boolean isConnectionValid() {
		synchronized (this.connection) {
			if (checkConnection()) {
				try {
					return this.connection.isValid(3);
				} catch (final SQLException e) {
					//DebugLog.INSTANCE.log(Level.INFO, "Problem when checking connection state", e);
				}
			}
		}
		return false;
	}

	private void reconnect() {
		synchronized (this.connection) {
			try {
				this.connection.close();
			} catch (final SQLException e) {
			}
			this.connection = null;
			try {
				open();
			} catch (final SQLException e) {
				writeError("Problem while reconnection to the database :\n" + e.getMessage(), true);
			}
		}
	}

	public void autoReconnect() {
		if (!isConnectionValid()) {
			reconnect();
		}
	}
}