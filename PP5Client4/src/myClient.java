import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Random;
import java.util.ArrayList;

//MYCLIENT1 FOR P2P file sharing thingy
public class myClient {
	public static void main(String[] args) throws IOException{
		//Socket socket = new Socket("127.0.0.1",5555);
		//System.out.println("socket created");
		//socket.close();
		int myPort = 8888;
		ClientUtilities cu = new ClientUtilities(myPort);
		cu.setName();
		cu.clientIsHere();
		clientReadThread CRT = new clientReadThread(cu);
		PeerServeThread PST = new PeerServeThread(cu);
		Thread T = new Thread(CRT);
		Thread TT = new Thread(PST);
		T.start();
		TT.start();
		try {
		T.join();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Quitting...");
		System.exit(0);
	}
}
