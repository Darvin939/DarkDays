package darvin939.DarkDays.Sql.Players;

import darvin939.DarkDays.Configuration.PC;

public class SQLPlayerData {
	Integer hunger;
	Boolean death;
	Boolean novice;
	Boolean spawned;

	public SQLPlayerData(Integer hunger, Boolean death, Boolean novice, Boolean spawned) {
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
		case PC.DEATH:
			return death;
		case PC.SPAWNED:
			return spawned;
		case PC.NOVICE:
			return novice;
		case PC.HUNGER:
			return hunger;
		}
		return null;
	}

	public void set(String data, Object value) {
		switch (data) {
		case PC.DEATH: {
			this.death = (Boolean) value;
			break;
		}
		case PC.SPAWNED: {
			this.spawned = (Boolean) value;
			break;
		}
		case PC.NOVICE: {
			this.novice = (Boolean) value;
			break;
		}
		case PC.HUNGER: {
			this.hunger = (Integer) value;
			break;
		}
		}
	}
}
