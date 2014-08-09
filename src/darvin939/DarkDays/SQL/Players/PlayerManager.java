package darvin939.DarkDays.SQL.Players;

import darvin939.DarkDays.SQL.DBInitLite;
import darvin939.DarkDays.SQL.Players.DDPlayer;
import darvin939.DarkDays.SQL.Players.SQLPlayer;
import darvin939.DarkDays.Utils.Debug.Debug;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerManager {

	private static final PlayerManager instance = new PlayerManager();
	public final Map<UUID, DDPlayer> players = new HashMap<UUID, DDPlayer>();
	public final static Map<UUID, Long> playersID = new HashMap<UUID, Long>();
	private final PreparedStatement insertPlayer = DBInitLite.prepareStatement("INSERT INTO `players` (`username`) VALUES (?)", 1);

	public static PlayerManager getInstance() {
		return instance;
	}

	public static void init() {
		ResultSet rs = DBInitLite.query("SELECT `username`,`id` FROM `players`");

		try {
			while (rs.next()) {
				playersID.put(UUID.fromString(rs.getString("username")), Long.valueOf(rs.getLong("id")));
			}

			rs.close();
		} catch (SQLException var2) {
			Debug.INSTANCE.severe(var2.toString());
		}

	}

	public DDPlayer addPlayer(Player player) {
		addExtPlayer(player.getUniqueId());
		Long id = getPlayerID(player);
		SQLPlayer play = new SQLPlayer(player, id);
		players.put(player.getUniqueId(), play);
		return play;
	}

	public DDPlayer getPlayer(Player player) {
		return (DDPlayer) players.get(player.getUniqueId());
	}

	public Long getPlayerID(Player player) {
		return (Long) playersID.get(player.getUniqueId());
	}

	private void addExtPlayer(UUID uuid) {
		if (!playersID.containsKey(uuid)) {
			ResultSet rs = null;

			try {
				synchronized (insertPlayer) {
					insertPlayer.clearParameters();
					insertPlayer.setString(1, uuid.toString());
					insertPlayer.executeUpdate();
					rs = insertPlayer.getGeneratedKeys();
					if (rs.next()) {
						playersID.put(uuid, Long.valueOf(rs.getLong(1)));
					}

					if (rs != null) {
						rs.close();
					}
				}
			} catch (SQLException var7) {
				var7.printStackTrace();
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException var5) {
						var5.printStackTrace();
					}
				}

				Debug.INSTANCE.severe(var7.toString());
			}
		}

	}
}
