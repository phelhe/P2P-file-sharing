import java.io.Serializable;
import java.util.ArrayList;

//SERVER APPMESSAGE
public class AppMessage implements Serializable {
	private String message;
	ArrayList filesToRegister;
	String fileToUnregister;
	String fileRequested;
	int ID;
	String peerName;
	int peerPort;
	String peerIP;
	double rating;
	
	public AppMessage(String m) {
		this.message = m;
	}
	
	public void setRating(double r) {
		this.rating = r;
	}
	
	public double getRating() {
		return this.rating;
	}
	
	public void setIP(String i) {
		this.peerIP = i;
	}
	
	public String getIP() {
		return this.peerIP;
	}
	
	public void setPort(int p) {
		this.peerPort = p;
	}
	
	public int getPort() {
		return this.peerPort;
	}
	
	public void setName(String n) {
		this.peerName = n;
	}
	
	public String getName() {
		return this.peerName;
	}
	
	public void setID(int i) {
		this.ID = i;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public void setMessage(String m) {
		this.message = m;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setFileRequested(String f) {
		this.fileRequested = f;
	}
	
	public String getFileRequested() {
		return this.fileRequested;
	}
	
	public void setFilesToRegister(ArrayList f) {
		this.filesToRegister = f;
	}
	
	public ArrayList getFilesToRegister() {
		return this.filesToRegister;
	}
	
	public void setFileToUnregister(String f) {
		this.fileToUnregister = f;
	}
	
	public String getFileToUnregister() {
		return this.fileToUnregister;
	}
}
