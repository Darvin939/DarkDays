package darvin939.DarkDays.SQL;

import darvin939.DarkDays.DarkDays;
import darvin939.DarkDays.Configuration.Config;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.DBMS;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Factory.DatabaseConfig;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;
import lib.PatPeter.SQLibrary.Factory.InvalidConfigurationException;
import lib.PatPeter.SQLibrary.Factory.Parameter;

public class DBInit {

   public static Database DATABASE;


   public DBInit() {
      if(DATABASE != null) {
         DATABASE.close();
      }

      DatabaseConfig config = new DatabaseConfig();
      Database db = null;
      String dbWrapper = Config.Nodes.MYSQL_DBWRAPPER.getString();
      if(dbWrapper.equalsIgnoreCase("mysql")) {
         config.setType(DBMS.MySQL);

         try {
            config.setParameter(Parameter.HOST, Config.Nodes.MYSQL_HOST.getString());
            config.setParameter(Parameter.PASSWORD, String.valueOf(Config.Nodes.MYSQL_PASS.getInteger()));
            config.setParameter(Parameter.USERNAME, Config.Nodes.MYSQL_USER.getString());
            config.setParameter(Parameter.PORT, Config.Nodes.MYSQL_PORT.toString());
            config.setParameter(Parameter.DATABASE, Config.Nodes.MYSQL_DATABASE.getString());
         } catch (InvalidConfigurationException var9) {
            var9.printStackTrace();
         }
      } else if(dbWrapper.equalsIgnoreCase("sqlite")) {
         config.setType(DBMS.SQLite);

         try {
            config.setParameter(Parameter.LOCATION, DarkDays.getDataPath());
            config.setParameter(Parameter.FILENAME, "darkdays");
         } catch (InvalidConfigurationException var8) {
            var8.printStackTrace();
         }
      }

      config.setLog(Logger.getLogger("Minecraft"));

      try {
         try {
            config.setParameter(Parameter.PREFIX, "[DarkDays Database]");
         } catch (InvalidConfigurationException var6) {
            var6.printStackTrace();
         }
      } catch (NullPointerException var7) {
         ;
      }

      if(config.getType() == null) {
         db = null;
      } else {
         try {
            db = DatabaseFactory.createDatabase(config);
         } catch (InvalidConfigurationException var5) {
            var5.printStackTrace();
         }
      }

      DATABASE = db;
   }

   public static PreparedStatement prepare(String query) {
      try {
         return DATABASE.prepare(query);
      } catch (SQLException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static PreparedStatement prepareStatement(String query, int statement) {
      try {
         return DATABASE.getConnection().prepareStatement(query, statement);
      } catch (SQLException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static ResultSet query(String query) {
      try {
         return DATABASE.query(query);
      } catch (SQLException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
