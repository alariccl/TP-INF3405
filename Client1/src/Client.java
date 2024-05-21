import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	
	public static String askServerAddress(Scanner scanner) {
		String serverAddress;
		while (true) {
			System.out.print("Enter IP address: ");
			serverAddress = scanner.nextLine();
			if (isValidIPAddress(serverAddress)) {
				return serverAddress;
			} else {
				System.out.print("Invalid IP address. Please enter a valid IP address\n");
			}
		}
	}
	
	public static int askServerPort(Scanner scanner) {
		int serverPort;
		while (true) {
			System.out.print("Enter port number: ");
			serverPort = scanner.nextInt();
			if (isValidPort(serverPort)) {
				return serverPort;
			} else {
				System.out.print("Invalid port. Please enter a valid Port between 5000 and 5050\n");
			}
		}
	}
	
	public static String askUsername(Scanner scanner) {
		String username;
		while (true) {
			System.out.print("Enter username: ");
			username = scanner.next();
			if (!username.isEmpty()) {
				return username;
			}
		}
	}
	
	public static String askPassword(Scanner scanner) {
		String password;
		while (true) {
			System.out.print("Enter password: ");
			password = scanner.next();
			if (!password.isEmpty()) {
				return password;
			}
		}
	}
	
	public static void sendUsernamePassword(Socket socket, String username, String password) throws IOException {
		PrintWriter pr = new PrintWriter(socket.getOutputStream());
		pr.println(username);
		pr.flush();
		pr.println(password);
		pr.flush();
		}
	
	public static String askImageName(Scanner scanner) {
		String imageName;
    	while (true) {
    		System.out.print("Enter image name: ");
    		imageName = scanner.next();
    		if (!imageName.isEmpty()) {
    			return imageName;
    		}
    	}
	}
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		String serverAddress = askServerAddress(scanner);
		int serverPort = askServerPort(scanner);
		String username = askUsername(scanner);
		String password = askPassword(scanner);

		// Création d'une nouvelle connexion aves le serveur
		socket = new Socket(serverAddress, serverPort);
		System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, serverPort);
		
		// envoyer username et password au serveur
		sendUsernamePassword(socket, username, password);
		
		// prepare a recevoir l'image
		InputStream is = socket.getInputStream();
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));			

        String messageFromServer = bf.readLine();
        System.out.println("Server : " + messageFromServer);
        
       // TODO : si le client n' est pas connecter fermer le socket
        
        String imageName = askImageName(scanner);
    	scanner.close();
        
        // TODO : ajouter un try catch si image n'existe pas
        try {
        	//lis l' image dans un tableau d'octets
        	File imageFile = new File(imageName);
        	FileInputStream fis = new FileInputStream(imageFile);
        	byte[] imageData = new byte[(int) imageFile.length()];
        	fis.read(imageData);
        	fis.close();
        	
        	// envoyer l' image par le socket
        	OutputStream os = socket.getOutputStream();
        	os.write(imageData);
        	os.flush();
        	socket.shutdownOutput();
        	
        	// recevoir image traite
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	byte[] buffer = new byte[1024];
        	int bytesRead;
        	while ((bytesRead = is.read(buffer)) != -1) {
        		baos.write(buffer, 0, bytesRead);
        	}
        	byte[] processedImageData = baos.toByteArray();
        	
        	FileOutputStream fos = new FileOutputStream("processed_" + imageName);
        	fos.write(processedImageData);
        	fos.close();
        	System.out.println("Processed image received and saved as processed_" + imageName);
        } catch (NullPointerException e) {
        	System.out.println("Image not found.");
        } catch (FileNotFoundException f) {
        	System.out.println("Image not found.");
        }
        finally {
        	// fermeture de La connexion avec le serveur
		socket.close();
        }
	}
}