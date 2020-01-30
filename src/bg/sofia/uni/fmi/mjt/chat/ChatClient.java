package bg.sofia.uni.fmi.mjt.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ChatClient {
	private static final String WRITING_EXCEPTION_HAS_OCCURRED = "A problem when writing to server has occurred.";
	private static final String DISCONNECTED_FROM_SERVER = "Disconnected from server.";
	private static final String UNABALE_TO_INITIALIZE_CLIENT = "Unabale to initialize client.";
	private static final int HOST_PORT = 9745;
	private static final String HOST_NAME = "localhost";
	
	public static final String DISCONNECT_COMMAND = "disconnect";
	public static final int DEFAULT_BUFFER_CAPACITY = 1000;

	public void start() {
		try (Scanner scanner = new Scanner(System.in); SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(HOST_NAME, HOST_PORT));
			new Thread(new ChatClientWriter(socketChannel)).start();
			
			while (true) {
				if (scanner.hasNext()) {
					String input = scanner.nextLine();
					StringTokenizer tokenizer = new StringTokenizer(input);
					writeToServer(socketChannel, input);
					if (tokenizer.nextToken().equalsIgnoreCase(DISCONNECT_COMMAND)) {
						System.out.println(DISCONNECTED_FROM_SERVER);
						socketChannel.close();
						break;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(UNABALE_TO_INITIALIZE_CLIENT);
		}
	}

	private void writeToServer(SocketChannel socketChannel, String input) {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(input.getBytes());
			socketChannel.write(buffer);
		} catch (IOException e) {
			System.out.println(WRITING_EXCEPTION_HAS_OCCURRED);
		}
	}

	public static void main(String[] args) {
		new ChatClient().start();
	}
}
