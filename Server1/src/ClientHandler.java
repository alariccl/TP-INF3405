import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//pour traiter la demande de chaque client sur un socket particulier
public class ClientHandler extends Thread { 
	//means clientHandler is a subclass of thread
	//note that a thread is a small program that can run at the same time as anothe
	
	private Socket socket;
	private int clientNumber;
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}
	
	// Création de thread qui envoi un message à un client et handles errors
	public void run() { //run() is called when start() is used for thread
		try {
			// création de canal d’envoi
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("Hello from server - you are client#" + clientNumber);
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e);
		} finally {
			try {
				socket.close();
			}catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with client#" + clientNumber+ "closed");
		}
	}
}