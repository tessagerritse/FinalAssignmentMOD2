package exceptions;

public class ExitProgram extends Exception {

	private static final long serialVersionUID = 4487322440921926620L;

	public ExitProgram(String msg) {
		super(msg);
	}
}
