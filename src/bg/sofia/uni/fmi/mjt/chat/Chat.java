package bg.sofia.uni.fmi.mjt.chat;

import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.NICKNAME_ALREADY_EXISTS;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.NICKNAME_DOES_NOT_EXIST;
import static bg.sofia.uni.fmi.mjt.chat.ErrorMessageBody.WRONG_COMMAND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
	private static final String NICKNAMES_DELIMITER = ", ";
	private static final char NEW_LINE = '\n';
	private static final String SERVER_NICKNAME = "Server";

	Map<String, List<Message>> messagesToBeSentByNickname = new HashMap<>();

	public void createUser(String nickname) {
		messagesToBeSentByNickname.put(nickname, new ArrayList<>());
	}

	public String getMessagesForUser(String nickname) {
		StringBuilder messagesTextBuilder = new StringBuilder();
		for (Message message : messagesToBeSentByNickname.get(nickname)) {
			messagesTextBuilder.append(message.toString()).append(NEW_LINE);
		}
		messagesToBeSentByNickname.get(nickname).clear();
		return messagesTextBuilder.toString();
	}

	public boolean changeNickname(String oldNickname, String newNickname) {
		if (!messagesToBeSentByNickname.containsKey(newNickname) && !newNickname.equalsIgnoreCase(SERVER_NICKNAME)) {
			messagesToBeSentByNickname.put(newNickname, messagesToBeSentByNickname.remove(oldNickname));
			return true;
		}

		messagesToBeSentByNickname.get(oldNickname)
				.add(new Message(SERVER_NICKNAME, NICKNAME_ALREADY_EXISTS.getMessageBody()));
		return false;
	}

	public void sendMessage(String senderNickname, String recieverNickname, String messageBody) {
		if (messagesToBeSentByNickname.containsKey(recieverNickname)) {
			messagesToBeSentByNickname.get(recieverNickname).add(new Message(senderNickname, messageBody));
		} else {
			messagesToBeSentByNickname.get(senderNickname)
					.add(new Message(SERVER_NICKNAME, NICKNAME_DOES_NOT_EXIST.getMessageBody()));
		}
	}

	public void sendAllMessage(String senderNickname, String messageBody) {
		Message message = new Message(senderNickname, messageBody); 
		for (List<Message> messagesToBeSent : messagesToBeSentByNickname.values()) {
			messagesToBeSent.add(message);
		}
	}

	public void sendWrongCommandMessage(String nickname) {
		messagesToBeSentByNickname.get(nickname).add(new Message(SERVER_NICKNAME, WRONG_COMMAND.getMessageBody()));
	}

	public void listUsers(String recieverNickname) {
		StringBuilder nicknamesBuilder = new StringBuilder();
		for (String nickname : messagesToBeSentByNickname.keySet()) {
			nicknamesBuilder.append(nickname).append(NICKNAMES_DELIMITER);
		}
		int lastIndexOfNicknamesDelimiter = nicknamesBuilder.lastIndexOf(NICKNAMES_DELIMITER);
		nicknamesBuilder.delete(lastIndexOfNicknamesDelimiter,
				lastIndexOfNicknamesDelimiter + NICKNAMES_DELIMITER.length());

		messagesToBeSentByNickname.get(recieverNickname).add(new Message(nicknamesBuilder.toString()));
	}

	public void removeUser(String nickname) {
		messagesToBeSentByNickname.remove(nickname);
	}
}
