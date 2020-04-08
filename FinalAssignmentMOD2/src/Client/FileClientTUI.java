package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Client TUI for user input and user messages
 * 
 * @author tessa.gerritse
 */
public class FileClientTUI {

	private FileClient client;
	private BufferedReader consoleIn;
	private PrintWriter consoleOut;
	private String userInput;
	
	public FileClientTUI(FileClient client) {
		this.client = client;
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
		consoleOut = new PrintWriter(System.out, true);
	}
	
	public void start() {
		
	}
	
	public void showMessage(String message) {
		consoleOut.println(message);
	}
	
	public String askStringAnswer(String question) {
		showMessage(question);
		String answer = null;
		try {
			answer = consoleIn.readLine();
		} catch (IOException e) {
			showMessage(e.getMessage());
		}
		return answer;
	}
	
	public void printCommandMenu() {
		showMessage(String.format("%s", "Possible commands: "));
		showMessage(String.format("%-20s %s", "u file", "upload a file to the server"));
		showMessage(String.format("%-20s %s", "d file", "download a file from the server"));
		showMessage(String.format("%-20s %s", "m file", "remove a file from the server"));
		showMessage(String.format("%-20s %s", "p file", "replace a file  on the server"));
		showMessage(String.format("%-20s %s", "l", "get a list of all files on the server"));
	}
}
