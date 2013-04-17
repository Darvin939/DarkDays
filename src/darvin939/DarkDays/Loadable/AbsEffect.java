package darvin939.DarkDays.Loadable;

public abstract interface AbsEffect {

	public abstract int getPercent();

	public abstract int getDelay();
	
	public abstract void setTime(int power);
	
	public abstract int getTime();

	public abstract void setDelay(int delay);

	public abstract void setPercent(int percent);

	public abstract String getName();
}
