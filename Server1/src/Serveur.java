import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.util.Scanner; // for the IP and ports verification

public class Serveur {
	private static ServerSocket Listener;
	
	public static void main(String[] args) throws Exception {
		//Compteur incremente a chaque connexion d'un client au serveur
		int clientNumber = 0;
		
		//Adress and port of server
		String serverAddress = "127.0.0.1";
		int serverPort = 5003;
		
		//Creation de la connexion (serverSocket object) pour communiquer avec les clients
		Listener = new ServerSocket();
		Listener.setReuseAddress(true); //binds to an address an port in the TIME_WAIT state (?)
		InetAddress serverIP = InetAddress.getByName(serverAddress); //binds serverSocket to IP and port
		
		//Association de l'adresse et du port a la connexion 
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		
		try {
			// À chaque fois qu'un nouveau client se, connecte, on exécute la fonction
			// run() de l'objet ClientHandler
			while (true) {
				// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
				// Lors d'une nouvelle connection : on incémente le compteur clientNumber
				new ClientHandler(Listener.accept(), clientNumber++).start();
				}
			
			//finally only is ran if theres an exception thrown.
			//This means that before any crashes happen we close the server first before anything
		}finally {
				//Fermeture de la connexion
				Listener.close();
				}
		}
	}