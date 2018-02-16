import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class myBroker {
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = null;
		BrokerUtilities bu = null;
		boolean listening = true;
		
		try {
			serverSocket = new ServerSocket(5555);
			bu = new BrokerUtilities(serverSocket);
		}catch(IOException e) {
			System.err.println("Could not listen on port: 5555");
			System.exit(-1);
		}
		System.out.println("Server is up and running...");
		System.out.println();
		System.out.println();
		while(listening) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("client accepted at " + clientSocket.getInetAddress());
			BrokerThread broke = new BrokerThread(clientSocket,bu);
			System.out.println("thread created");
			Thread T = new Thread(broke);
			System.out.println("starting thread...");
			T.start();
		}
		serverSocket.close();
		System.out.println("Server socket closed... goodbye :)");
	}
}
