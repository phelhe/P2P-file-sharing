import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;

public class PeerServeThread implements Runnable {
	ClientUtilities cu;
	ServerSocket socket;
	InputStream recvStream;
	OutputStream sendStream;
	ObjectInputStream is;
	ObjectOutputStream os;
	AppMessage request;
	boolean listening;
	int port;
	public PeerServeThread(ClientUtilities c) throws IOException {
		cu = c;
		this.port = cu.myPort;
	}
	
	public void run() {
		try {
			this.socket = new ServerSocket(cu.myPort);
		}catch(IOException e) {
			System.err.println("Could not listen on port: " + cu.myPort);
			System.exit(-1);
		}
		//System.out.println("PeerServeThread is up and running");
		this.listening = true;
		try {
			while(cu.listening) {
				Socket peerSocket = socket.accept();
				//System.out.println("peer accepted at " + peerSocket.getInetAddress());
				ServeThread serve = new ServeThread(peerSocket, cu);
				Thread T = new Thread(serve);
				//System.out.println("starting thread...");
				T.start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Shutting down your service to peers...");
	}
}
