import java.util.ArrayList;
public class clientReadThread implements Runnable {
	ClientUtilities cu;
	public clientReadThread(ClientUtilities c) {
		cu = c;
	}
	public void run() {
		//System.out.println("reading from keyboard");
		boolean reading = true;
		String fromKeyboard = null;
		String message = null;
		try {
			while(reading) {
				fromKeyboard = cu.readFromKeyboard();
				message = fromKeyboard;
				//System.out.println("Client said: " + message);
				if(message.equals("exit")) {
					//System.out.println("Client wants to leave...");
					cu.clientIsLeaving();
					reading = false;
					/* tell the server you are gone
					 * cu.leaving()
					 */
				}
				/**if the message is "register"
				 * then the client wants to register files
				 * make cu.registering = true
				 * have a while loop to do that
				 */
				if(message.equals("register")) {
					//System.out.println("client wants to register files!");
					cu.clientIsRegistering(); //make it so the client is registering
					//create arraylist to hold all the file names
					ArrayList filesList = new ArrayList();
					while(cu.clientRegisteringStatus()) {
						System.out.println("Enter a file name or enter 'done' to complete registration process.");
						fromKeyboard = cu.readFromKeyboard();
						message = fromKeyboard;
						if(message.equals("done")) {
							System.out.println("Done registering");
							cu.registerFiles(filesList);//let the utilities tell the server
							cu.clientDoneRegistering();
						}
						else {
							System.out.println("Adding " + message + " to the list of files to register.");
							filesList.add(message);
						}
					}
				}
				/*
				 * if the message is "unregister"
				 * the next thing they type will be the file name
				 * then call cu.unregister(name of file)"
				 */
				if(message.equals("unregister")) {
					System.out.println("Unregister a File");
					System.out.println("enter a file name");
					fromKeyboard = cu.readFromKeyboard();
					message = fromKeyboard;
					cu.unregisterFile(message);
				}
				/**
				 * if the message is search
				 * the next thing they type will be the file they want
				 * then call cu.search(name of file)
				 */
				if(message.equals("search")) {
					System.out.println("Request a File");
					System.out.println("Enter the file name:");
					fromKeyboard = cu.readFromKeyboard();
					message = fromKeyboard;
					cu.requestFile(message);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
