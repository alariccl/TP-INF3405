
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	
	private static Map<String, String> userDB = new HashMap<>();
	private static final String USER_DB_PATH = "userdb.txt";

	private static synchronized void registerNewUser(FileWriter userDataBase, String username, String password) throws IOException {
		userDB.put(username, password);
		try {
			userDataBase.write(username);
			userDataBase.write(",");
			userDataBase.write(password + "\n");
			userDataBase.close();
		}
		catch (Exception e) {
			e.getStackTrace();
		}
//				BufferedWriter bw = new BufferedWriter(fw);
//				PrintWriter out = new PrintWriter(bw)) {
//			out.println(username + "," + password);
//		}
	}
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}
	public void run() { // Création de thread qui envoi un message à un client
		try {
			FileWriter userDataBase = new FileWriter(USER_DB_PATH, true);
			PrintWriter pr = new PrintWriter(socket.getOutputStream());
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
	        BufferedReader bf = new BufferedReader(in);
	        
	        String username = bf.readLine();
	        System.out.println("client username: " + username);
	        
	        String password = bf.readLine();
	        System.out.println("client username: " + password);
	        
			pr.println("TEST.\n");

	        // si le database est vide
			if (userDB.isEmpty()) {
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				pr.println("Client registered successfully.\n");

			// si le username existe
			} else if (!userDB.containsKey(username)) {
				System.out.print("User not found. Creating a new Client\n");
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				pr.println("Client registered successfully.\n");

			// si le username existe et que le mdp est bon
			}  else if (userDB.get(username).equals(password)) {
				System.out.print("Authentication successful.");
				System.out.print("Hello from server - you are client#" + clientNumber); // envoi de message				
				pr.println("Authentication successful.");
			} else {
				System.out.print("Error authentication failed. Incorrect password.");
				pr.println("Error authentication failed. Incorrect password.");
			}
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
				System.out.println("Connection with client# " + clientNumber+ " closed");
			}
	}
}
