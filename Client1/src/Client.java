import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;  // Importation de Scanner

// Application client
public class Client {
	private static Socket socket;
	
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
	
	public static void main(String[] args) throws Exception {
			// Adresse et port du serveur
			Scanner scanner = new Scanner(System.in);
			String serverAddress; // = "127.0.0.1";
			while (true) {
				System.out.print("Enter IP address: ");
				serverAddress = scanner.nextLine();
				if (isValidIPAddress(serverAddress)) {
					break;
				} else {
					System.out.print("Invalid IP address. Please enter a valid IP address\n");
				}
			}
			
			int serverPort;
			while (true) {
				System.out.print("Enter port number: ");
				serverPort = scanner.nextInt();
				if (isValidPort(serverPort)) {
					break;
				} else {
					System.out.print("Invalid port. Please enter a valid Port between 5000 and 5050\n");
				}
			}
			
			String username;
			while (true) {
				System.out.print("Enter username: ");
				username = scanner.next();
				if (!username.isEmpty()) {
					break;
				}
			}
			
			String password;
			while (true) {
				System.out.print("Enter password: ");
				password = scanner.next();
				if (!password.isEmpty()) {
					break;
				}
			}
		
			// Création d'une nouvelle connexion aves le serveur
			socket = new Socket(serverAddress, serverPort);
			System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, serverPort);
			
			PrintWriter pr = new PrintWriter(socket.getOutputStream());
			pr.println(username);
			pr.flush();
			
			pr.println(password);
			pr.flush();
		
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
			BufferedReader bf = new BufferedReader(in);			
			
	        String messageFromServer = bf.readLine();
	        System.out.println("server : " + messageFromServer);
	        
	        String IMAGE_PATH = "lassonde.jpg";
	        //TODO : ajouter le traitement de l'image
	        
	        // lis l' image dans un tableau d'octets
	        File imageFile = new File(IMAGE_PATH);
	        FileInputStream fis = new FileInputStream(imageFile);
	        byte[] imageData = new byte[(int) imageFile.length()];
	        fis.read(imageData);
	        fis.close();
	        
	        // envoyer l' image par le socket
	        OutputStream os = socket.getOutputStream();
	        os.write(imageData);
	        os.flush();
	        
	        //recevoir image traite
//	        DataInputStream dis = new DataInputStream(socket.getInputStream());
//	        int length = dis.readInt(); // Read the length of the incoming image data
//	        byte[] processedImageData = new byte[length];
//	        dis.readFully(processedImageData);
//
//	        FileOutputStream fos = new FileOutputStream("image_traite.jpg");
//	        fos.write(processedImageData);
//	        fos.flush();
//	        fos.close();

			// fermeture de La connexion avec le serveur
			socket.close();
	}
}