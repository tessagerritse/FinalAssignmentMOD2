package main.java.fileClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.ExitProgram;
import shared.Protocol;

/**
 * Takes care of the user-input and provides feedback to the user if his input cannot be handled.
 * 
 * @author tessa.gerritse
 *
 */
public class FileClientTUI {
	
	private FileClient fileClient;
	private BufferedReader consoleIn;
	private PrintWriter consoleOut;

	private List<String> validCommands = new ArrayList<>();
	private List<String> fileNecessary = new ArrayList<>();

	public FileClientTUI(FileClient fileClient) {
		this.fileClient = fileClient;
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
		consoleOut = new PrintWriter(System.out, true);
		validCommands.addAll(Arrays.asList(Protocol.VALID_COMMANDS));
		fileNecessary.addAll(Arrays.asList(Protocol.FILE_NECESSARY));
	}
	public void start() throws IOException {
		boolean askingForInput = true;
		printCommandMenu();
		
		while (askingForInput) {
			try {
				handleUserInput();
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
		showMessage("When asked to give your command, please use one of the following commands");
		showMessage("For [file] fill in the name of the file you want to use, including the extension \n");
		showMessage(String.format("%-20s %s", "u file", "upload a [file] to the server"));
		showMessage(String.format("%-20s %s", "d file", "download a [file] from the server"));
		showMessage(String.format("%-20s %s", "r file", "remove a [file] from the server"));
		showMessage(String.format("%-20s %s", "l", "get a list of all [files] on the server"));
		showMessage(String.format("%-20s %s", "p", "print the command menu"));
		showMessage(String.format("%-20s %s", "q", "quit the program \n"));
	}

	private void handleUserInput() throws ExitProgram, IOException {
		String userInput = askStringAnswer("What is your command? \n");
		
		String[] parts = userInput.split("\\s+");
		String command = parts[0];
		String fileName = (parts.length > 1) ? fileName = parts[1] : "";
		
		if (!validCommands.contains(command)) {
			showMessage("That is not a valid command. Please try again \n");
			printCommandMenu();
		} else if (fileNecessary.contains(command) && fileName.isEmpty()) {
			showMessage("The file name is missing. Please try again. \n");
		} else if (fileName.length() > Protocol.NAME_PACKET_SIZE) {
			showMessage("That fileName is too long. The fileName may at most be " + Protocol.NAME_PACKET_SIZE 
					+ " characters long \n");
		} else if (command.equals(Protocol.PRINT)) {		
			printCommandMenu();
		} else {
			fileClient.handleRequest(command, fileName);
		}
	}
}
