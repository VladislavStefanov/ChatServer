package bg.sofia.uni.fmi.mjt.chat;

import static bg.sofia.uni.fmi.mjt.chat.Command.LIST_USERS;
import static bg.sofia.uni.fmi.mjt.chat.Command.NICK;
import static bg.sofia.uni.fmi.mjt.chat.Command.SEND;
import static bg.sofia.uni.fmi.mjt.chat.Command.SEND_ALL;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.READING_EXCEPTION_HAS_OCCURRED;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.SELECTOR_UNABLE_TO_SELECT_KEYS;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.UNABLE_TO_INITIALIZE_APPLICATION;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.WRITING_EXCEPTION_HAS_OCCURRED;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class ChatServer {
	private static final int HOST_PORT = 9745;
	private static final String HOST_NAME = "localhost";
	private static final int MESSAGE_TOKENS_SEND_ALL_COUNT = 2;
	private static final int MESSAGE_TOKENS_COUNT = 3;
	private static final String MESSAGE_ELEMENTS_DELIMITER = " ";
	private static final int DEFAULT_BUFFER_CAPACITY = 1000;

	private Map<SocketChannel, String> nicknamesByChannels = new HashMap<>();
	private Chat chat = new Chat();

	public void start() {
		try (Scanner scanner = new Scanner(System.in);
				Selector selector = Selector.open();
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();) {
			serverSocketChannel.bind(new InetSocketAddress(HOST_NAME, HOST_PORT));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				try {
					int readyChannels = selector.select();
					if (readyChannels == 0) {
						continue;
					}

					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();

						if (key.isAcceptable()) {
							registerChannel(selector, serverSocketChannel);
						} else if (key.isReadable()) {
							SocketChannel socketChannel = (SocketChannel) key.channel();
							readFromClient(socketChannel);
							for (SocketChannel channel : nicknamesByChannels.keySet()) {
								writeToClient(channel);
							}
						}

						updateChannels();

						keyIterator.remove();
					}
				} catch (IOException e) {
					System.out.println(SELECTOR_UNABLE_TO_SELECT_KEYS.getMessageBody());
					e.printStackTrace();
				}
			}

		} catch (ClosedChannelException e) {
			System.out.println(UNABLE_TO_INITIALIZE_APPLICATION.getMessageBody());
			System.out.println(ErrorMessageBody.CHANNEL_IS_CLOSED.getMessageBody());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(UNABLE_TO_INITIALIZE_APPLICATION.getMessageBody());
			e.printStackTrace();
		}

	}

	private void registerChannel(Selector selector, ServerSocketChannel serverSocketChannel) {
		try {
			SocketChannel clientSocketChannel = serverSocketChannel.accept();
			clientSocketChannel.configureBlocking(false);
			clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

			String nickname = UUID.randomUUID().toString();
			nicknamesByChannels.put(clientSocketChannel, nickname);
			chat.createUser(nickname);
		} catch (ClosedChannelException e) {
			System.out.println(ErrorMessageBody.CHANNEL_IS_CLOSED.getMessageBody());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(ErrorMessageBody.IO_EXCEPTION_HAS_OCCURRED.getMessageBody());
			e.printStackTrace();
		}
	}

	private void writeToClient(SocketChannel socketChannel) {
		try {
			String messageText = chat.getMessagesForUser(nicknamesByChannels.get(socketChannel));
			ByteBuffer buffer = ByteBuffer.wrap(messageText.getBytes());
			socketChannel.write(buffer);
		} catch (IOException e) {
			System.out.println(WRITING_EXCEPTION_HAS_OCCURRED.getMessageBody());
			e.printStackTrace();
		}
	}

	private void readFromClient(SocketChannel socketChannel) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_CAPACITY);
			StringBuilder clientMessageBuilder = new StringBuilder();
			while (socketChannel.read(buffer) > 0) {
				buffer.flip();
				clientMessageBuilder.append(new String(buffer.array(), 0, buffer.limit()));
				buffer.clear();
			}
			String clientMessage = clientMessageBuilder.toString();

			String nickname = nicknamesByChannels.get(socketChannel);

			int tokenIndex = 0;
			String[] tokens = clientMessage.split(MESSAGE_ELEMENTS_DELIMITER, MESSAGE_TOKENS_COUNT);
			Command command = Command.from(tokens[tokenIndex++]);
			if (command == null) {
				chat.sendWrongCommandMessage(nickname);
			} else if (NICK.equals(command)) {
				if (tokens.length < MESSAGE_TOKENS_COUNT - 1) {
					chat.sendWrongCommandMessage(nickname);
				} else {
					String newNickname = tokens[tokenIndex++];
					if (chat.changeNickname(nickname, newNickname)) {
						nicknamesByChannels.put(socketChannel, newNickname);
					}
				}
			} else if (SEND.equals(command)) {
				if (tokens.length < MESSAGE_TOKENS_COUNT) {
					chat.sendWrongCommandMessage(nickname);
				} else {
					String recieverNickname = tokens[tokenIndex++];
					String messageBody = tokens[tokenIndex++];
					chat.sendMessage(nickname, recieverNickname, messageBody);
				}
			} else if (SEND_ALL.equals(command)) {
				if (tokens.length < MESSAGE_TOKENS_SEND_ALL_COUNT) {
					chat.sendWrongCommandMessage(nickname);
				} else {
					tokens = clientMessage.split(MESSAGE_ELEMENTS_DELIMITER, MESSAGE_TOKENS_SEND_ALL_COUNT);
					String messageBody = tokens[tokenIndex++];
					chat.sendAllMessage(nickname, messageBody);
				}
			} else if (LIST_USERS.equals(command)) {
				chat.listUsers(nickname);
			} else if (Command.DISCONNECT.equals(command)) {
				chat.removeUser(nicknamesByChannels.remove(socketChannel));
				ByteBuffer writeBuffer = ByteBuffer.wrap(Command.DISCONNECT.getCommandName().getBytes());
				socketChannel.write(writeBuffer);
				socketChannel.close();
			}
		} catch (IOException e) {
			System.out.println(READING_EXCEPTION_HAS_OCCURRED.getMessageBody());
			e.printStackTrace();
		}
	}

	private void updateChannels() {
		for (SocketChannel channel : nicknamesByChannels.keySet()) {
			if (!channel.isConnected()) {
				chat.removeUser(nicknamesByChannels.remove(channel));
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer().start();
	}
}
