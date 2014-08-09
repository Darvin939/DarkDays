package darvin939.DarkDays.SQL;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import darvin939.DarkDays.Utils.Debug.Debug;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.H2;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

public class DBInitLite {

   public static Database DATABASE;
   Logger log = Logger.getLogger("Minecraft");
   String prefix = "[DarkDays Database]";
   String host;
   Integer port;
   String database;
   String username;
   String pass;
   String dbWrapper;
   String datapath;


   public DBInitLite() {
      this.host = Config.Nodes.MYSQL_HOST.toString();
      this.port = Config.Nodes.MYSQL_PORT.getInteger();
      this.database = Config.Nodes.MYSQL_DATABASE.getString();
      this.username = Config.Nodes.MYSQL_USER.getString();
      this.pass = Config.Nodes.MYSQL_PASS.toString();
      this.dbWrapper = Config.Nodes.MYSQL_DBWRAPPER.getString();
      this.datapath = DarkDays.getDataPath();
      if(DATABASE != null) {
         DATABASE.close();
      }

      Object db = null;
      if(this.dbWrapper.equalsIgnoreCase("mysql")) {
         db = new MySQL(this.log, this.prefix, this.host, this.port.intValue(), this.database, this.username, this.pass);
      }

      if(this.dbWrapper.equalsIgnoreCase("sqlite")) {
         db = new SQLite(this.log, this.prefix, this.datapath, "darkdays", ".sqlite");
      }

      if(this.dbWrapper.equalsIgnoreCase("h2")) {
         db = new H2(this.log, this.prefix, this.datapath, "darkdays", ".h2");
      }

      DATABASE = (Database)db;
   }

   public static PreparedStatement prepare(String query) {
      try {
         return DATABASE.prepare(query);
      } catch (SQLException var2) {
         var2.printStackTrace();
         Debug.INSTANCE.severe(var2.toString());
         return null;
      }
   }

   public static PreparedStatement prepareStatement(String query, int statement) {
      try {
         return DATABASE.getConnection().prepareStatement(query, statement);
      } catch (SQLException var3) {
         var3.printStackTrace();
         Debug.INSTANCE.severe(var3.toString());
         return null;
      }
   }

   public static ResultSet query(String query) {
      try {
         return DATABASE.query(query);
      } catch (SQLException var2) {
         var2.printStackTrace();
         Debug.INSTANCE.severe(var2.toString());
         return null;
      }
   }
}
