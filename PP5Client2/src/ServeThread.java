import java.io.*;
import java.net.*;
import java.util.*;
import java.io.File;

public class ServeThread implements Runnable {
	Socket socket;
	InputStream recvStream;
	OutputStream sendStream;
	ObjectInputStream is;
	ObjectOutputStream os;
	AppMessage request;
	ClientUtilities cu;
	DataInputStream input;
	DataOutputStream output;

	public ServeThread(Socket clientSocket, ClientUtilities c) {
		this.socket = clientSocket;
		this.cu = c;
	}
	
	public void run() {
		//System.out.println("Service thread running for client at " + socket.getInetAddress() + " on port " + socket.getPort());
		String fileRequest = null;
		try{
			InputStream recvStream = socket.getInputStream();
			is = new ObjectInputStream(recvStream);
			AppMessage request = (AppMessage)is.readObject();
			//System.out.println("A peer wants: " + request.getFileRequested());
			fileRequest = request.getFileRequested();
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			File file = new File(fileRequest);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			OutputStream out = socket.getOutputStream();
			
			byte[] contents;
			long fileLength = file.length();
			long current = 0;
			
			long start = System.nanoTime();
			while(current!=fileLength) {
				int size = 1000;
				if(fileLength - current >= size) {
					current += size;
				}
				else {
					size = (int)(fileLength - current);
					current = fileLength;
				}
				contents = new byte[size];
				bis.read(contents,0,size);
				out.write(contents);
				//System.out.println("sending file..." + (current*100)/fileLength+"% complete!");
			}
			out.flush();
			this.socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
