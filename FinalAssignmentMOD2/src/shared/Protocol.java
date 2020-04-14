package shared;

public class Protocol {
	
	public static final int META_PORT = 8888;
	public static final int UPLOAD_PORT = 8008;
	public static final int DOWNLOAD_PORT = 8080;
	public static final int REMOVE_PORT = 8800;
	public static final int LIST_PORT = 8808;
		
	public static final int FILE_PACKET_SIZE = 26000;
	public static final int NAME_PACKET_SIZE = 25;
	public static final int COMMAND_PACKET_SIZE = 1;
	public static final int FEEDBACK_PACKET_SIZE = 100;

	public static final String UPLOAD = "u";
	public static final String DOWNLOAD = "d";
	public static final String REMOVE = "r";
	public static final String LIST = "l";
	public static final String PRINT = "p";
	public static final String QUIT = "q";
	
	public static final String[] VALID_COMMANDS = {UPLOAD, DOWNLOAD, REMOVE, LIST, PRINT, QUIT};	
	
	public static final String[] FILE_NECESSARY = {UPLOAD, DOWNLOAD, REMOVE};
}


