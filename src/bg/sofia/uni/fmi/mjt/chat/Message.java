package bg.sofia.uni.fmi.mjt.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Message {
	private static final String MESSAGE_FORMAT_MARKER = "?";
	private static final String DATE_FORMAT = "[?]";
	private static final String MESSAGE_DELIMITER = " ";
	private static final String NICKNAME_MESSAGE_BODY_DELIMITER = ":";

	private final LocalDateTime timeCreated;
	private final String senderNickname;
	private final String messageBody;

	public Message(String senderNickname, String messageBody) {
		this.senderNickname = senderNickname;
		this.messageBody = messageBody;
		this.timeCreated = LocalDateTime.now();
	}

	public Message(String messageBody) {
		this.senderNickname = null;
		this.messageBody = messageBody;
		this.timeCreated = LocalDateTime.now();
	}

	public String toString() {
		StringBuilder messageTextBuilder = new StringBuilder(DATE_FORMAT);
		String timeCreatedText = timeCreated.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

		int indexToBeReplaced = messageTextBuilder.indexOf(MESSAGE_FORMAT_MARKER);

		messageTextBuilder.replace(indexToBeReplaced, indexToBeReplaced + 1, timeCreatedText).append(MESSAGE_DELIMITER);
		if (senderNickname != null) {
			messageTextBuilder.append(senderNickname).append(MESSAGE_DELIMITER).append(NICKNAME_MESSAGE_BODY_DELIMITER)
					.append(MESSAGE_DELIMITER);
		}
		messageTextBuilder.append(messageBody);

		return messageTextBuilder.toString();
	}
}
