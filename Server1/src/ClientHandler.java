
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter;  

public class ClientHandler extends Thread { 
	private Socket socket; 
	private int clientNumber; 
	private static final String USER_DB_PATH = "userdb.txt";
	private static Map<String, String> userDB = new HashMap<>();
	
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
	
	private static synchronized void registerNewUser(FileWriter userDataBase, String username, String password) 
			throws IOException {
		userDB.put(username, password);
		userDataBase.write(username + "," + password + "\n");
		userDataBase.close();
	}
	
	private static void receiveImage(Socket socket) throws IOException {
    	InputStream is = socket.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		FileOutputStream fos = new FileOutputStream("received_image.jpg");
		fos.write(baos.toByteArray());
		fos.flush();
		fos.close();
    }
	
	private void applySobelFilter() throws IOException {
		BufferedImage image = ImageIO.read(new File("received_image.jpg"));
		File outputFile = new File("image_traite.jpg");
		ImageIO.write(Sobel.process(image), "jpg", outputFile);
	}
	
	private void sendImage() throws IOException {
    	File outputFile = new File("image_traite.jpg");
        FileInputStream fis = new FileInputStream(outputFile);
        byte[] imageOutputData = new byte[(int) outputFile.length()];
        fis.read(imageOutputData);
        fis.close();
        
        OutputStream os1 = socket.getOutputStream();
        os1.write(imageOutputData);
        os1.flush();
    }
	
	private static void printClientInformation(Socket socket, String username, String password, String processedImageNom) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy@HH:mm:ss");
		String formatDateTime = now.format(format);
		System.out.println("[" + username + " - " 
						+ socket.getLocalAddress() + ":" 
						+ socket.getLocalPort() + " - " 
						+ formatDateTime + "] : Image "
						+ processedImageNom 
						+ " received for processing");	
	}
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}
	
	public void run() {
		try {
			FileWriter userDataBase = new FileWriter(USER_DB_PATH, true);
			
			OutputStream os = socket.getOutputStream();
			PrintWriter pr = new PrintWriter(os);
			
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
	        BufferedReader bf = new BufferedReader(in);
	        
	        String username = bf.readLine();
	        String password = bf.readLine();	        
	   
	        loadUserDatabase();
	        
			if (userDB.isEmpty()) {
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				pr.println("Client registered successfully.\n");
				pr.flush();
				
				String processedImageNom = bf.readLine();
				receiveImage(socket);
				printClientInformation(socket, username, password, processedImageNom);
				applySobelFilter();
				sendImage();
		        socket.shutdownOutput();
		        
			} else if (!userDB.containsKey(username)) {
				System.out.print("User not found. Creating a new Client\n");
				registerNewUser(userDataBase, username, password);
				System.out.print("Client registered successfully.\n");
				pr.println("User not found. Creating a new Client\n");
				pr.println("Client registered successfully.\n");
				pr.flush();
				
				String processedImageNom = bf.readLine();
		        System.out.println("image :" + processedImageNom);
				receiveImage(socket);
				printClientInformation(socket, username, password, processedImageNom);
				applySobelFilter();
				sendImage();
		        socket.shutdownOutput();
		        
			} else if (userDB.get(username).equals(password)) {
				System.out.print("Authentication successful.\n");
				System.out.print("Hello from server - you are client#" + clientNumber + "\n");	
				pr.println("Authentication successful.\n");
				pr.println("Hello from server - you are client#" + clientNumber + "\n");
				pr.flush();
				
				String processedImageNom = bf.readLine();
		        System.out.println("image :" + processedImageNom);		       
				receiveImage(socket);
				printClientInformation(socket, username, password, processedImageNom);
				applySobelFilter();
				sendImage();
		        socket.shutdownOutput();
		        
			} else {
				System.out.print("Error authentication failed. Incorrect password.\n");
				pr.println("Error authentication failed. Incorrect password.");
				pr.flush();
			}
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e + "\n");
		}catch (NullPointerException e) { 
			System.out.println("Image not found.");
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
