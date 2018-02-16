import java.util.*;
import java.io.Serializable;
public class PeerInfo implements Serializable {
	String peerIP;
	int peerPort;
	int peerID;
	String peerName;
	boolean online;
	ArrayList<String> peerFiles; 
	double rating;
	double ratingCount;
	public PeerInfo(String IP, int port, int ID) {
		this.peerIP = IP;
		this.peerPort = port;
		this.peerID = ID;
		this.peerName = null;
		this.online = true;
		this.ratingCount = 0;
		this.rating = 2;
		this.peerFiles = new ArrayList();
	}
}
