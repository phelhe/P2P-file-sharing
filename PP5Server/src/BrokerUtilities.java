import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;

public class BrokerUtilities {
	ServerSocket serverSocket;
	ArrayList<PeerInfo> peers;
	int userCount;
	InputStream recvStream;
	OutputStream sendStream;
	ObjectInputStream is;
	ObjectOutputStream os;
	public BrokerUtilities(ServerSocket s) {
		this.serverSocket = s;
		peers = new ArrayList();
		userCount = 0;
	}
	
	public void addPeer(Socket clientSocket, String peerName, int peerPort, String currentIP) throws IOException {
		userCount++;
		System.out.println("User count: " + userCount);
		System.out.println("Adding client at: ");
		System.out.println(clientSocket.getPort());
		System.out.println(clientSocket.getInetAddress().toString());
		System.out.println("Dangling port: " + peerPort);
		boolean alreadyRegistered = false;
		for(PeerInfo peer: peers) {
			if(peer.peerName.equals(peerName)) {
				System.out.println("This user is already registered!");
				peer.peerIP = currentIP; //test this???
				alreadyRegistered = true;
				peer.online = true;
			}
		}
		
		this.sendStream = clientSocket.getOutputStream();
		PeerInfo newPeer = new PeerInfo(clientSocket.getInetAddress().toString(),clientSocket.getPort(),userCount);
		newPeer.peerName = peerName;
		newPeer.peerPort = peerPort;
		try {
			this.os = new ObjectOutputStream(sendStream);
			os.writeObject(newPeer);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(!alreadyRegistered) {
			System.out.println("This is a new user");
			System.out.println("Adding peers to peer list");
			peers.add(newPeer);
		}
		
		System.out.println("PEERS LIST:");
		for(PeerInfo peer: peers) {
			System.out.println((peer.peerName) + " at " + peer.peerIP + " on port " + peer.peerPort + ": " + "online? " + peer.online);
		}
		//need to tell client their user ID
	}
	
	public void removePeer(Socket clientSocket, String peerName) {
		System.out.println("Removing client with ID: " + peerName + " at");
		System.out.println(clientSocket.getPort());
		System.out.println(clientSocket.getInetAddress().toString());
		int i = 0;
		//System.out.println("PEERS LIST: ");
		for(PeerInfo peer: peers) {
			if(peer.peerName.equals(peerName)) {
				//System.out.println("match found!");
				//peers.remove(peer);
				System.out.println(peers.toString());
				peer.online = false;
			}
			//System.out.println("Peer " + peer.peerID + ":");
			//System.out.println(peer.peerIP);
			//System.out.println(peer.peerPort);
			i++;
		}
		System.out.println("PEERS LIST:");
		for(PeerInfo peer: peers) {
			System.out.println((peer.peerName) + " at " + peer.peerIP + " on port " + peer.peerPort + ": " + "online? " + peer.online);
		}
	}
	
	public void addFiles(AppMessage message) {
		String tempName = message.getName();
		//System.out.println(tempName);
		ArrayList<String> tempArray = message.getFilesToRegister();
		for(PeerInfo peer: peers) {
			//System.out.println(peer.peerName);
			if(peer.peerName.equals(tempName)) {
				//System.out.println("match found!");
				for(String file: tempArray) {
					peer.peerFiles.add(file);
				}
				System.out.println("Added files for peer " + peer.peerName);
				System.out.println("New file list:");
				for(String file: peer.peerFiles) {
					System.out.println(file);
				}
			}
		}
	}
	
	public void unregisterFile(AppMessage message) {
		System.out.println("Unregistering: " + message.getFileToUnregister() + "for user " + message.getName());
		String name = message.getName();
		for(PeerInfo peer: peers) {
			if(peer.peerName.equals(name)) {
				System.out.println("Peer found!");
				Iterator<String> it = peer.peerFiles.iterator();
				while(it.hasNext()) {
					String str = it.next();
					if(message.getFileToUnregister().equals(str)) {
						System.out.println("file found!");
						it.remove();
					}
				}
			}
		}
	}
	
	public AppMessage findPeer(AppMessage request) {
		String requestedFile = request.getFileRequested();
		AppMessage message = new AppMessage("unavailable");
		ArrayList<PeerInfo> candidates = new ArrayList();
		for(PeerInfo peer: peers) {
			if(peer.online) {
				for(String file: peer.peerFiles) {
					if(file.equals(requestedFile)) {
						candidates.add(peer);
					}
				}
			}
		}
		PeerInfo best = null;
		if(candidates.size()>0) {
			System.out.println("CANDIDATES:");
			best = candidates.remove(0);
			System.out.println(best.peerName + ": " + best.rating);
			for(PeerInfo peer: candidates) {
				System.out.println(peer.peerName + ": " + peer.rating);
				if(peer.rating > best.rating) {
					best = peer;
				}
			}
			System.out.println("The best candidate was: " + best.peerName + " with a rating of " + best.rating);
		}
		if(best!=null) { //was a peer found??
			message.setPort(best.peerPort);
			message.setIP(best.peerIP);
			message.setMessage("available");
			message.setName(best.peerName);
			message.setFileRequested(requestedFile);
		}
		/*for(PeerInfo peer: peers) {
			if(peer.online) {
				for(String file: peer.peerFiles) {
					if(file.equals(requestedFile)) {
						System.out.println("File found! User " + peer.peerName + " has the file.");
						System.out.println("His info is " + peer.peerIP + " and " + peer.peerPort);
						message.setPort(peer.peerPort);
						message.setIP(peer.peerIP);
						message.setMessage("available");
						message.setName(peer.peerName);
						message.setFileRequested(requestedFile);
					}
				}
			}
		}*/
		return message;
	}
	
	public void respondToRequest(Socket clientSocket, AppMessage request) {
		AppMessage response = this.findPeer(request);
		try {
		this.sendStream = clientSocket.getOutputStream();
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			this.os = new ObjectOutputStream(sendStream);
			os.writeObject(response);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ratePeer(AppMessage message) {
		for(PeerInfo peer: peers) {
			if(peer.peerName.equals(message.getName())) {
				peer.ratingCount++;
				double count = peer.ratingCount;
				double rating = (peer.rating*(count-1)) + message.getRating();
				rating = rating/count;
				peer.rating = rating;
			}
		}
		for(PeerInfo peer: peers) {
			System.out.println(peer.peerName + ": " + peer.rating);
		}
	}
}
