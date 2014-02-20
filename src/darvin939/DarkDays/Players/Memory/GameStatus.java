package darvin939.DarkDays.Players.Memory;

import darvin939.DarkDays.Configuration.PlayerConfig;

public class GameStatus {
	private Integer hunger;
	private Boolean death;
	private Boolean novice;
	private Boolean spawned;

	public GameStatus(Integer hunger, Boolean death, Boolean novice, Boolean spawned) {
		this.hunger = hunger;
		this.death = death;
		this.novice = novice;
		this.spawned = spawned;
	}

	public Integer getHunger() {
		return hunger;
	}

	public Boolean isDeath() {
		return death;
	}

	public Boolean isNovice() {
		return novice;
	}

	public Boolean isSpawned() {
		return spawned;
	}

	public void setHunger(Integer value) {
		this.hunger = value;
	}

	public void setDeath(Boolean value) {
		this.death = value;
	}

	public void setNovice(Boolean value) {
		this.novice = value;
	}

	public void setSpawned(Boolean value) {
		this.spawned = value;
	}

	public Object get(String data) {
		switch (data) {
		case PlayerConfig.DEATH:
			return death;
		case PlayerConfig.SPAWNED:
			return spawned;
		case PlayerConfig.NOVICE:
			return novice;
		case PlayerConfig.HUNGER:
			return hunger;
		}
		return null;
	}

	public void set(String data, Object value) {
		switch (data) {
		case PlayerConfig.DEATH: {
			this.death = (Boolean) value;
			break;
		}
		case PlayerConfig.SPAWNED: {
			this.spawned = (Boolean) value;
			break;
		}
		case PlayerConfig.NOVICE: {
			this.novice = (Boolean) value;
			break;
		}
		case PlayerConfig.HUNGER: {
			this.hunger = (Integer) value;
			break;
		}
		}
	}
}
