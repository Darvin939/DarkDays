package darvin939.DarkDays.Commands;

public class InvalidUsage extends Exception {

	private static final long serialVersionUID = -3329843858161455841L;

	public InvalidUsage(String message) {
        super("<rose>Invalid Command Usage: " + message);
    }
}
