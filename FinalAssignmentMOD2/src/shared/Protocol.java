package shared;

public class Protocol {
	
	public static final int META_PORT = 8888;
	public static final int UPLOAD_PORT = 8008;
	public static final int DOWNLOAD_PORT = 8080;
	public static final int REMOVE_PORT = 8800;
	public static final int LIST_PORT = 8808;
	
	public static final int CLIENT_META_PORT = 9999;
	public static final int CLIENT_UPLOAD_PORT = 9009;
	public static final int CLIENT_DOWNLOAD_PORT = 9090;
	public static final int CLIENT_REMOVE_PORT = 9900;
	public static final int CLIENT_LIST_PORT = 9909;
	
	public static final int HEADER = 2; //bytes	
	//Location of HEADER bytes
	public static final int INFO = 0;
	public static final int SEQNUM = 1;
	//Possible values of INFO
	public static final int NOT_EOF = 0; //int
	public static final int EOF = 1; //int
	public static final int NOT_EOF_NAME = 2; //int
	public static final int EOF_NAME = 3; //int
	//Maximal SEQNUM (sequence number)
	public static final int MAX_SEQNUM = (int) (Math.pow(2, 8) - 1); //ints
	
	public static final int PACKET_SIZE = (int) (Math.pow(2, 16) - 50); //bytes, note: -50, just to be sure
	public static final int DATA_SIZE = PACKET_SIZE - HEADER; //bytes
	
	public static final int COMMAND_PACKET_SIZE = 1;
	public static final int NAME_PACKET_SIZE = 32;
	public static final int FEEDBACK_PACKET_SIZE = 128;

	//Possible user commands
	public static final String UPLOAD = "u";
	public static final String DOWNLOAD = "d";
	public static final String REMOVE = "r";
	public static final String LIST = "l";
	public static final String PRINT = "p";
	public static final String QUIT = "q";
	
	public static final String[] VALID_COMMANDS = {UPLOAD, DOWNLOAD, REMOVE, LIST, PRINT, QUIT};	
	public static final String[] FILE_NECESSARY = {UPLOAD, DOWNLOAD, REMOVE};
}


