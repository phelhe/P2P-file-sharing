import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class ClientUtilities {
	boolean registering;
	Socket socket;
	OutputStream sendStream;
	InputStream recvStream;
	ObjectInputStream is;
	ObjectOutputStream os;
	String ServerIP;
	int ServerPort;
	int myId;
	String myName;
	int myPort;
	boolean listening;
	public ClientUtilities(int p) {
		this.registering = false;
		this.ServerIP = "127.0.0.1";
		this.ServerPort = 5555;
		this.myPort = p;
		this.listening = true;
	}
	
	/*
	 * public void clientIsHere()
	 * open socket to server
	 * send message saying available
	 * close the socket
	 */
	public void clientIsHere() throws IOException{
		socket = new Socket(ServerIP, ServerPort);
		System.out.println("Logging in...");
		AppMessage message = new AppMessage("arriving");
		message.peerName = this.myName;
		message.setPort(this.myPort);
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		os.flush();
		recvStream = socket.getInputStream();
		is = new ObjectInputStream(recvStream);
		PeerInfo myInfo = null;
		try {
		//System.out.println("reading object?");
		myInfo = (PeerInfo)is.readObject();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Logged in.");
		System.out.println("COMMANDS: ");
		System.out.println("    Type 'register' to register files with the server");
		System.out.println("    Type 'unregister' to unregister a file from the server");
		System.out.println("    Type 'search' to request a file");
		System.out.println("    Type 'exit' to log out and quit");
		myId = myInfo.peerID;
		//System.out.println("My id is: " + myId);
		socket.close();
	}
	
	
	/*
	 * public void clientIsLeaving()
	 * open socket to server
	 * send message saying you are leaving
	 * close the socket
	 */
	public void clientIsLeaving() throws IOException{
		socket = new Socket(ServerIP, ServerPort);
		System.out.println("Logging out...");
		AppMessage message = new AppMessage("leaving");
		message.setID(myId);
		message.setName(this.myName);
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		socket.close();
		this.listening = false;
	}
	
	public void registerFiles(ArrayList filesToRegister) throws IOException{
		AppMessage message = new AppMessage("register");
		message.setFilesToRegister(filesToRegister);
		message.setID(myId);
		message.setName(this.myName);
		System.out.println("Registering: ");
		System.out.println(filesToRegister.toString());
		socket = new Socket(ServerIP, ServerPort);
		System.out.println("Sending information to the server...");
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		socket.close();
	}
	
	public void unregisterFile(String fileName) throws IOException {
		AppMessage message = new AppMessage("unregister");
		message.setFileToUnregister(fileName);
		message.setName(myName);
		System.out.println("Unregistering: "  + fileName);
		//System.out.println(fileName);
		socket = new Socket(ServerIP, ServerPort);
		System.out.println("Telling the server the file you want to unregister");
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		socket.close();
	}
	
	public void requestFile(String fileName) throws IOException {
		AppMessage message = new AppMessage("request");
		message.setFileRequested(fileName);
		System.out.println("Requesting: " + fileName);
		//System.out.println(fileName);
		socket = new Socket(ServerIP, ServerPort);
		System.out.println("Asking the server for a peer...");
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		os.flush();
		recvStream = socket.getInputStream();
		is = new ObjectInputStream(recvStream);
		AppMessage response = null;
		try {
			response = (AppMessage)is.readObject();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		socket.close();
		
		System.out.println("Response received: ");
		if(response.getMessage().equals("available")) {
			System.out.println("Peer found with IP " + response.getIP() + " at port " + response.getPort());
			this.fetchFileFromPeer(response);
		}
		else {
			System.out.println("No peer found with that file...");
		}
	}
	
	public void fetchFileFromPeer(AppMessage response) throws IOException{
		//System.out.println("Going to get the file...");
		socket = new Socket(response.getIP().substring(1), response.getPort());
		System.out.println("Connecting to peer...");
		AppMessage message = new AppMessage("i am here");
		message.peerName = this.myName;
		message.setFileRequested(response.getFileRequested());
		message.setPort(this.myPort);
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(message);
		os.flush();
		
		//now save the file???
		byte[] contents = new byte[10000];
		FileOutputStream fos = new FileOutputStream(response.getFileRequested());
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		InputStream in = socket.getInputStream();
		
		int bytesRead = 0;
		System.out.println("Reading file...");
		while((bytesRead = in.read(contents))!=-1) {
			bos.write(contents,0,bytesRead);
		}
		
		bos.flush();
		bos.close();
		in.close();
				
		//
		socket.close();
		System.out.println("File downloaded!");
		this.ratePeer(response);
	}
	
	public void ratePeer(AppMessage response) throws IOException {
		System.out.println("How would you rate this peer on a scale of 1 to 5?");
		double rating = 5;
		try{
			rating = Integer.parseInt(this.readFromKeyboard());
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("You rated them: " + rating + "/5.0");
		socket = new Socket(this.ServerIP,this.ServerPort);
		AppMessage rate = new AppMessage("rating");
		rate.setName(response.getName());
		rate.setRating(rating);
		sendStream = socket.getOutputStream();
		os = new ObjectOutputStream(sendStream);
		os.writeObject(rate);
		os.flush();
		socket.close();
	}
	
	public void setName() {
		try {
		System.out.println("Enter a username: ");
		String name = this.readFromKeyboard();
		this.myName = name;
		//System.out.println("Your name is: " + this.myName);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clientIsRegistering() {
		this.registering = true;
	}
	
	public void clientDoneRegistering() {
		this.registering = false;
	}
	
	public boolean clientRegisteringStatus() {
		return this.registering;
	}
	
	
	public String readFromKeyboard() throws Exception{
		BufferedReader stdin;
		String message;
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Type something: ");
		message = stdin.readLine();
		return message;
	}
}
