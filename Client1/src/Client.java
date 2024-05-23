import java.io.DataInputStream;
import java.net.Socket;

// NOTE: clientHandler is part of the server program
//While client handles the connection after client connects with server

// The client here connects to the server, receives messages and then closes the connection
public class Client{
	private static Socket socket; // reminder that a socket is creating an end point of a 2 way communication
	
	public static void main(String[] args) throws Exception{
		//Addresse et port du serveur
		String serverAddress = "127.0.0.1";
		int port = 5003;
		
		//Creation d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lance sur [%s:%d]",  serverAddress, port);
		
		//Creation d'un canal entrant pour recevoir les messages envoyes, par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		//Attente de la reception d'un message envoye par le server sur le canal
		//client waits for the server to send a message and then stores it in helloMessageFromServer
		//the second line just prints it to the console
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		//fermeture de la connexion avec le serveur
		socket.close();
	}
}