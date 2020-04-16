package main.java.exceptions;

/**
 * This exception is thrown when the user wants to quit the program.
 * 
 * @author tessa.gerritse
 *
 */
public class ExitProgram extends Exception {

	private static final long serialVersionUID = 4487322440921926620L;

	public ExitProgram(String msg) {
		super(msg);
	}
}
