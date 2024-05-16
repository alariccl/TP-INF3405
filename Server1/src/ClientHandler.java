
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	
	private static Map<String, String> userDB = new HashMap<>();
	private static final String USER_DB_PATH = "userdb.txt";
	
	public static void loadUserDatabase() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(USER_DB_PATH))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2) {
					userDB.put(parts[0], parts[1]);
				}
			}
		} catch (FileNotFoundException e){
			System.out.println("User database file not found.\n");
		}
	}
	
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
	}
	
	public static void receiveImage(Socket socket) throws IOException {
    	InputStream is = socket.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		//creation du fichier recu
		FileOutputStream fos = new FileOutputStream("received_image.jpg");
		fos.write(baos.toByteArray());
		fos.flush();
		fos.close();
    }
	
	public void applySobelFilter() throws IOException {
		BufferedImage image = ImageIO.read(new File("received_image.jpg"));
		File outputFile = new File("image_traite.jpg");
		ImageIO.write(Sobel.process(image), "jpg", outputFile);
	}
	
	public void sendImage() throws IOException {
    	File outputFile = new File("image_traite.jpg");
        FileInputStream fis = new FileInputStream(outputFile);
        byte[] imageOutputData = new byte[(int) outputFile.length()];
        fis.read(imageOutputData);
        fis.close();
        
        // envoyer l' image par le socket
        OutputStream os1 = socket.getOutputStream();
        os1.write(imageOutputData);
        os1.flush();
    }
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}
	
	public void run() { // Création de thread qui envoi un message à un client
		try {
			FileWriter userDataBase = new FileWriter(USER_DB_PATH, true);
			OutputStream os = socket.getOutputStream();
			PrintWriter pr = new PrintWriter(os);
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
	        BufferedReader bf = new BufferedReader(in);
	        
	        String username = bf.readLine();
	        System.out.println("client username: " + username);
	        String password = bf.readLine();
	        System.out.println("client password: " + password);
	        
	        // si le database est vide
	        loadUserDatabase();
			if (userDB.isEmpty()) {
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				//envoyer au client le message 
				pr.println("Client registered successfully.\n");
				pr.flush();
				// reception de l' image
				receiveImage(socket);
				// traitement de l' image
				applySobelFilter();
				// envoi de l' image traite
				sendImage();
		        socket.shutdownOutput();				
			// si le username existe
			} else if (!userDB.containsKey(username)) {
				System.out.print("User not found. Creating a new Client\n");
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				pr.println("User not found. Creating a new Client\n");
				pr.println("Client registered successfully.\n");
				pr.flush();
				// reception de l' image
				receiveImage(socket);
				// traitement de l' image
				applySobelFilter();
				// envoi de l' image traite
				sendImage();
		        socket.shutdownOutput();

			// si le username existe et que le mdp est bon
			}  else if (userDB.get(username).equals(password)) {
				System.out.print("Authentication successful.\n");
				System.out.print("Hello from server - you are client#" + clientNumber + "\n"); // envoi de message				
				//envoyer au client le message 
				pr.println("Authentication successful.\n");
				pr.println("Hello from server - you are client#" + clientNumber + "\n"); // envoi de message				
				pr.flush();
				// reception de l' image
				receiveImage(socket);
				// traitement de l' image
				applySobelFilter();
				// envoi de l' image traite
				sendImage();
		        socket.shutdownOutput();
			} else {
				System.out.print("Error authentication failed. Incorrect password.\n");
				pr.println("Error authentication failed. Incorrect password.\n");
				pr.flush();
			}
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e + "\n");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?\n");
			}
				System.out.println("Connection with client# " + clientNumber+ " closed\n");
			}
	}
}
