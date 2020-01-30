package bg.sofia.uni.fmi.mjt.chat;

import static bg.sofia.uni.fmi.mjt.chat.ChatClient.DEFAULT_BUFFER_CAPACITY;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClientWriter implements Runnable {
	private static final String READING_EXCEPTION_HAS_OCCURRED = "A problem when reading from server has occured.";

	private SocketChannel socketChannel;
	private ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_CAPACITY);

	public ChatClientWriter(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		while (true) {
			try {
				buffer.clear();
				socketChannel.read(buffer);
				buffer.flip();
				String serverMessage = new String(buffer.array(), 0, buffer.limit());
				if (ChatClient.DISCONNECT_COMMAND.equalsIgnoreCase(serverMessage)) {
					break;
				}
				System.out.println(serverMessage);
			} catch (IOException e) {
				System.out.println(READING_EXCEPTION_HAS_OCCURRED);
			}
		}

	}

}
