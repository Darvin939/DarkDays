package darvin939.DarkDays.Players.Memory;

import java.util.UUID;

public class PlayerStatus {
	private UUID playerId;
	private Integer zombieKills = 0;
	private Integer playerKills = 0;
	private Integer playerHeals = 0;

	public PlayerStatus(UUID playerId) {
		this.playerId = playerId;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public Integer getZombieKills() {
		return zombieKills;
	}

	public void setZombieKills(Integer zombieKills) {
		this.zombieKills = zombieKills;
	}

	public void addZombieKill() {
		this.zombieKills = Integer.valueOf(zombieKills.intValue() + 1);
	}

	public Integer getPlayerKills() {
		return playerKills;
	}

	public void setPlayerKills(Integer playerKills) {
		this.playerKills = playerKills;
	}

	public void addPlayerKill() {
		this.playerKills = Integer.valueOf(playerKills.intValue() + 1);
	}

	public Integer getPlayerHeals() {
		return playerHeals;
	}

	public void setPlayerHeals(Integer playerHeals) {
		this.playerHeals = playerHeals;
	}

	public void addPlayerHeal() {
		this.playerHeals = Integer.valueOf(playerHeals.intValue() + 1);
	}
}