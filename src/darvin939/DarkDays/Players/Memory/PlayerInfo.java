package darvin939.DarkDays.Players.Memory;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerInfo {
	private static HashMap<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();

	public static HashMap<UUID, PlayerData> getPlayers() {
		return players;
	}

	public static void addPlayer(Player player) {
		players.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
	}

	public static boolean isPlaying(Player player) {
		return players.containsKey(player.getUniqueId());
	}

	public static void setNovice(Player player, Boolean set) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			bean.setNovice(set);
		}
	}

	public static boolean getNovice(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			return bean.isNovice();
		}
		return false;
	}

	public static void removePlayer(Player player) {
		players.remove(player.getUniqueId());
	}

	public static void addZombieKill(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			bean.addZombieKill();
		}
	}

	public static int getZombieKills(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			return bean.getZombieKills().intValue();
		}
		return 0;
	}

	public static void addPlayerKill(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			bean.addPlayerKill();
		}
	}

	public static int getPlayerKills(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			return bean.getPlayerKills().intValue();
		}
		return 0;
	}

	public static void addPlayerHeal(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			bean.addPlayerHeal();
		}
	}

	public static int getPlayerHeals(Player player) {
		if (isPlaying(player)) {
			PlayerData bean = (PlayerData) players.get(player.getUniqueId());
			return bean.getPlayerHeals().intValue();
		}
		return 0;
	}
}
