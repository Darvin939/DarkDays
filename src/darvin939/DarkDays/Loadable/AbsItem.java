package darvin939.DarkDays.Loadable;

public abstract interface AbsItem {
	
	public abstract String getName();
	
	public abstract String getMessage();
	
	public abstract void setItem(String item);

	public abstract String getItem();
	
	public abstract void setDepend(String depend);
	
	public abstract String getDepend();

	public abstract void setMessage(String key, String msg);
}
