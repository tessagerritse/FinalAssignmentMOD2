package exceptions;

/**
 * This exception is thrown when the user wants to quit the program.
 * 
 * @author tessa.gerritse
 *
 */
public class ExitProgram extends Exception {

	private static final long serialVersionUID = -1946224119760311922L;

	public ExitProgram(String msg) {
		super(msg);
	}
}
