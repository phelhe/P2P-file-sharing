import java.io.*;
import java.net.*;
import java.util.*;

public class BrokerThread implements Runnable {
	Socket socket;
	InputStream recvStream;
	OutputStream sendStream;
	ObjectInputStream is;
	ObjectOutputStream os;
	AppMessage request;
	BrokerUtilities bu;
	int userCount;
	
	public BrokerThread(Socket clientSocket, BrokerUtilities b) throws IOException{
		this.socket = clientSocket;
		this.recvStream = clientSocket.getInputStream();
		//this.sendStream = clientSocket.getOutputStream();
		this.bu = b;
	}
	
	public void run() {
		System.out.println("thread running for client at " + socket.getInetAddress());
		try {
			//this.os = new ObjectOutputStream(sendStream);
			this.is = new ObjectInputStream(recvStream);
		}catch(IOException e) {
			e.printStackTrace();
		}
		try {
			request = (AppMessage)is.readObject();
			System.out.println("request coming in... client says:");
			System.out.println(request.getMessage());
			if(request.getMessage().equals("register")) {
				System.out.println("User wants to register: ");
				System.out.println(request.getFilesToRegister().toString());
				bu.addFiles(request);
			}
			else if(request.getMessage().equals("arriving")) {
				System.out.println("a user is arriving");
				System.out.println("user's name is: " + request.getName());
				bu.addPeer(socket, request.getName(), request.getPort(), socket.getInetAddress().toString());
			}
			else if(request.getMessage().equals("leaving")) {
				System.out.println("a user is leaving");
				bu.removePeer(socket,request.getName());
			}
			else if(request.getMessage().equals("unregister")) {
				bu.unregisterFile(request);
			}
			else if(request.getMessage().equals("request")) {
				System.out.println("User wants a file: ");
				System.out.println(request.getFileRequested());
				bu.respondToRequest(socket, request);
				//System.out.println("bu.findPeer returned " + toSend.getMessage());
			}
			else if(request.getMessage().equals("rating")) {
				System.out.println("A peer has rated peer " + request.getName() + " a score of " + request.getRating());
				System.out.println("That is a rating of " + request.getRating()/(double)5);
				bu.ratePeer(request);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
		socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------------");
	}
}
