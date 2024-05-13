
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;  // Importation de Scanner

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Serveur {
	private static ServerSocket Listener; 
	
	private static boolean isValidIPAddress(String ipAddress) {
		String[] parts = ipAddress.split("\\.");
		if (parts.length != 4) {
			return false;
		}
		for (String part : parts) {
			try {
				int intPart = Integer.parseInt(part);
				if (intPart < 0 || intPart > 255) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isValidPort(int serverPort) {
		if (serverPort < 5000 || serverPort > 5050) {
			return false;
		}
		return true;
	}
	
//	private static Map<String, String> userDB = new HashMap<>();
	private static final String USER_DB_PATH = "userdb.txt";

//	public static void loadUserDatabase() throws IOException {
//		try (BufferedReader reader = new BufferedReader(new FileReader(USER_DB_PATH))) {
//			String line;
//			while ((line = reader.readLine()) != null) {
//				String[] parts = line.split(",");
//				if (parts.length == 2) {
//					userDB.put(parts[0], parts[1]);
//				}
//			}
//		} catch (FileNotFoundException e){
//			System.out.println("User database file not found.");
//		}
//	}
	
	// Application Serveur
	public static void main(String[] args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			String serverAddress;
			while (true) {
				System.out.print("Enter IP address: ");
				serverAddress = scanner.nextLine();
				if (isValidIPAddress(serverAddress)) {
					break;
				} else {
					System.out.print("Invalid IP address. Please enter a valid IP address");
				}
			}
			int serverPort;
			while (true) {
				System.out.print("Enter port number: ");
				serverPort = scanner.nextInt();
				if (isValidPort(serverPort)) {
					break;
				} else {
					System.out.print("Invalid port. Please enter a valid Port between 5000 and 5050");
				}
			}
			// Adresse et port du serveur
			// Création de la connexien pour communiquer ave les, clients
			Listener = new ServerSocket();
			Listener.setReuseAddress(true);
			InetAddress serverIP = InetAddress.getByName(serverAddress);
			// Association de l'adresse et du port à la connexion
			Listener.bind(new InetSocketAddress(serverIP, serverPort));
			System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
			
			// Compteur incrémenté à chaque connexion d'un client au serveur
			int clientNumber = 0;
			// À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
			// run();  // de l'objet ClientHandler
//			loadUserDatabase();
			while (true) {
				// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
				// Une nouvetle connection : on incémente le compteur clientNumber 
				System.out.print("Waiting for clients...");
				new ClientHandler(Listener.accept(), clientNumber++).start(); 	// .accept() permet d'accpeter un client dans un server
																				// .start() permet de commencer le run() qui est implemente dans la classe derivee de Thread
			}
		} finally {
			 // Fermeture de la connexion
			if (Listener != null && !Listener.isClosed()) {
				System.out.format("The server is closed"); 
				Listener.close();				
			}
		} 
	} 
}
