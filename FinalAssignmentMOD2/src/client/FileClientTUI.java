package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.ExitProgram;
import transmission.ProtocolMessages;

/**
 * Client TUI for user input and user messages
 * 
 * @author tessa.gerritse
 */
public class FileClientTUI {

	private FileClient client;
	private BufferedReader consoleIn;
	private PrintWriter consoleOut;
	
	private List<String> validCommands = new ArrayList<>();

	public FileClientTUI(FileClient client) {
		this.client = client;
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
		consoleOut = new PrintWriter(System.out, true);
		validCommands.addAll(Arrays.asList(ProtocolMessages.VALID_COMMANDS));
	}

	public void start() throws IOException {
		boolean askingForInput = true;
		String userInput;
		printCommandMenu();
		while (askingForInput) {
			userInput = askStringAnswer("What is your command?");
			try {
				handleUserInput(userInput);
			} catch (ExitProgram e) {
				askingForInput = false;
				consoleIn.close();
				consoleOut.close();
			}
		}
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

	private void printCommandMenu() {
		showMessage("When asked to enter a command, please use one of the following commands");
		showMessage("For [file] fille in the name of the file you want use, including the extension \n");
		showMessage(String.format("%-20s %s", "u file", "upload a [file] to the server"));
		showMessage(String.format("%-20s %s", "d file", "download a [file] from the server"));
		showMessage(String.format("%-20s %s", "r file", "remove a [file] from the server"));
		showMessage(String.format("%-20s %s", "l", "get a list of all [files] on the server"));
		showMessage(String.format("%-20s %s", "q", "quit the program"));
		showMessage(""); //empty line
	}

	private void handleUserInput(String input) throws ExitProgram, IOException {
		String[] parts = input.split("\\s+");
		String command = parts[0];
		String fileName = (parts.length > 1) ? parts[1] : "";
		
		boolean validCommand = false;
		while (!validCommand) {
			if (validCommands.contains(command)) {
				validCommand = true;
				client.handleRequest(command, fileName);
			} else {
				showMessage("That is not a valid command. Please try again.");
				printCommandMenu();
			}
		}
	}
}
