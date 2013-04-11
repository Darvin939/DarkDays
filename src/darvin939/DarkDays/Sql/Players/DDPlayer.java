package darvin939.DarkDays.Sql.Players;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import darvin939.DarkDays.Players.Memory.PlayerLoadData;

public abstract class DDPlayer {
	protected final String name;
	protected Player player = null;
	private final int hashCode;

	protected DDPlayer(Player player) {
		this.name = player.getName();
		this.player = player;
		int result = 7;
		result = 41 * result + (name == null ? 0 : name.hashCode());

		hashCode = result;
	}

	protected DDPlayer(String name) {
		this.name = name;
		this.player = Bukkit.getServer().getPlayer(name);
		int result = 7;
		result = 41 * result + (this.name == null ? 0 : this.name.hashCode());

		hashCode = result;
	}

	public abstract Location getLoc();

	public abstract PlayerLoadData getData();

	public abstract String getEffects();

	public abstract void addPlayer();

	public abstract void removePlayer();

	public abstract void addData(PlayerLoadData data);
	
	public abstract void removeData();

	public abstract void addEffects(String data);

	public abstract void removeEffects();

	public String getName() {	
		return name;
	}

	public int hashCode() {
		return hashCode;
	}

}
