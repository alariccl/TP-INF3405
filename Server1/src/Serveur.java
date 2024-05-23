
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
	
	private static final int PORT_MIN = 5000;
	private static final int PORT_MAX = 5050;  
	private static final int IP_MIN = 0;
	private static final int IP_MAX = 255;
	
	private static ServerSocket Listener; 
	
	private static boolean isValidIPAddress(String ipAddress) {
		String[] parts = ipAddress.split("\\.");
		if (parts.length != 4) {
			return false;
		}
		for (String part : parts) {
			try {
				int intPart = Integer.parseInt(part);
				if (intPart < IP_MIN || intPart > IP_MAX) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isValidPort(int serverPort) {
		if (serverPort < PORT_MIN || serverPort > PORT_MAX) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			String serverAddress;
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
			
			Listener = new ServerSocket();
			Listener.setReuseAddress(true);
			InetAddress serverIP = InetAddress.getByName(serverAddress);
			Listener.bind(new InetSocketAddress(serverIP, serverPort));
			System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
			int clientNumber = 0;
			while (true) {
				System.out.print("Waiting for clients...\n");
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
		} finally {
			if (Listener != null && !Listener.isClosed()) {
				System.out.format("The server is closed\n"); 
				Listener.close();				
			}
		} 
	} 
}
