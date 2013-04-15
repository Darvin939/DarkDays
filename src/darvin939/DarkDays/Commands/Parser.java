package darvin939.DarkDays.Commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Parser {
	private LinkedHashMap<String, String[]> commands;
	private LinkedHashMap<String, Handler> handlers;
	private LinkedHashMap<String, String> help;
	private LinkedHashMap<String, String> permissions;
	private ArrayList<String> command_list;

	public Parser() {
		this.commands = new LinkedHashMap<String, String[]>();
		this.handlers = new LinkedHashMap<String, Handler>();
		this.help = new LinkedHashMap<String, String>();
		this.permissions = new LinkedHashMap<String, String>();
		this.command_list = new ArrayList<String>();
	}

	public String[] getCommands() {
		return command_list.toArray(new String[] {});
	}

	public String getCommandsString() {
		String split = "";
		Iterator<String> iter = command_list.iterator();
		while (iter.hasNext()) {

			split = split.isEmpty() ? iter.next() : split + ", " + iter.next();
			
			//if (split.isEmpty())
			//	split = iter.next();
			//else
			//	split = split + ", " + iter.next();
		}
		return split;
	}

	public void add(String Command, Handler Handler) {
		// String base = Command.split(" ")[0];
		this.commands.put(Command, Command.split(" "));

		// if (!this.handlers.containsKey(base.substring(1, base.length())))
		// this.handlers.put(base.substring(1, base.length()), Handler);

		this.handlers.put(Command, Handler);
		this.command_list.add(Command.split(" ")[1].toLowerCase());
	}

	public void setHelp(String command, String help) {
		this.help.put(command, help);
	}

	public boolean hasHelp(String command) {
		return this.help.containsKey(command);
	}

	public LinkedHashMap<String, String> getHelp() {
		return this.help;
	}

	public String getHelp(String command) {
		return this.help.get(command);
	}

	public void setPermission(String Command, String level) {
		this.permissions.put(Command, level);
	}

	public Handler getHandler(String Command) {
		return this.handlers.get(Command.toLowerCase());
	}

	public boolean hasPermission(String Command) {
		return this.permissions.containsKey(Command);
	}

	public String getPermission(String Command) {
		return this.permissions.get(Command);
	}
}
