package client;
import java.io.BufferedReader;
import java.io.File;
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
public class ClientTUI {

	private FileClient client;
	private BufferedReader consoleIn;
	private PrintWriter consoleOut;

	private List<String> validCommands = new ArrayList<>();

	public ClientTUI(FileClient client) {
		this.client = client;
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
		consoleOut = new PrintWriter(System.out, true);
		validCommands.addAll(Arrays.asList(ProtocolMessages.VALID_COMMANDS));
	}

	public void start(int maxNameLength) throws IOException {
		boolean askingForInput = true;
		String userInput;
		printCommandMenu();
		
		while (askingForInput) {
			userInput = askStringAnswer("What is your command? \n");
			try {
				handleUserInput(userInput, maxNameLength);
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
		showMessage("For [file] fill in the name of the file you want to use, including the extension");
		showMessage(String.format("%-20s %s", "u file", "upload a [file] to the server"));
		showMessage(String.format("%-20s %s", "d file", "download a [file] from the server"));
		showMessage(String.format("%-20s %s", "r file", "remove a [file] from the server"));
		showMessage(String.format("%-20s %s", "l", "get a list of all [files] on the server"));
		showMessage(String.format("%-20s %s", "p", "print the command menu"));
		showMessage(String.format("%-20s %s", "q", "quit the program \n"));
	}

	private void handleUserInput(String input, int maxNameLength) throws ExitProgram, IOException {
		String[] parts = input.split("\\s+");
		String command = parts[0];
		String fileName = (parts.length > 1) ? parts[1] : "";
		
		if (!validCommands.contains(command)) {
			showMessage("That is not a valid command. Please try again \n");
			printCommandMenu();
		} else if (fileName.length() > maxNameLength) {
			showMessage("That fileName is too long. The fileName may at most be " + maxNameLength + " characters long \n");
		} else if (command.equals(ProtocolMessages.PRINT)) {		
			printCommandMenu();
		} else {
			client.handleRequest(command, fileName);
		}
	}
}
