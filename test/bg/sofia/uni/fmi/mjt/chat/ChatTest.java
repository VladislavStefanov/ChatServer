package bg.sofia.uni.fmi.mjt.chat;

import static org.junit.Assert.*;

import org.junit.Test;

public class ChatTest {
	
	private static final String NICKNAME_ALREADY_TAKEN = "Nickname already taken.";
	private static final String MUST_BE_VALID_SEND = "Must be valid send";
	private static final String NICKNAME_NOT_FOUND = "Nickname not found.";
	private static final String WRONG_COMMAND = "Such command does not exist.";
	private static final String MUST_LIST_USERS = "Must list users";
	private static final String NICKNAMES_DELIMITER = ", ";
	private static final String MUST_BE_SAME_MESSAGE = "Must be same message";
	private static final String MUST_BE_MULTIPLE_LINES = "Must be multiple lines";
	private static final String MUST_HAVE_SERVER_AS_SENDER = "Must have server as sender";
	private static final String MUST_HAVE_RIGHT_MESSAGE_BODY = "Must have right message body";
	private static final String MUST_HAVE_SENDER = "Must have sender";
	private static final String SERVER_NICKNAME = "Server";
	private static final String NEW_LINE = "\n";
	private static final String DEFAULT_NICKNAME_3 = "vladikata";
	private static final String DEFAULT_NICKNAME_2 = "vladcata";
	private static final String DEFAULT_MESSAGE_BODY = "message";
	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_NICKNAME = "vladi";

	@Test
	public void testCreateUser() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		assertEquals(EMPTY_STRING, chat.getMessagesForUser(DEFAULT_NICKNAME));
	}

	@Test
	public void testGetMessagesForUser() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_2);
		chat.sendMessage(DEFAULT_NICKNAME_2, DEFAULT_NICKNAME, DEFAULT_MESSAGE_BODY);
		chat.sendMessage(DEFAULT_NICKNAME, DEFAULT_NICKNAME_3, DEFAULT_MESSAGE_BODY);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME);
		assertTrue(MUST_BE_MULTIPLE_LINES, messages.contains(NEW_LINE));
		assertTrue(MUST_HAVE_SERVER_AS_SENDER, messages.contains(SERVER_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(DEFAULT_MESSAGE_BODY));
		assertTrue(MUST_HAVE_SENDER, messages.contains(DEFAULT_NICKNAME_2));
	}

	@Test
	public void testChangeNickname() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_3);
		chat.changeNickname(DEFAULT_NICKNAME, DEFAULT_NICKNAME_2);
		chat.sendMessage(DEFAULT_NICKNAME_2, DEFAULT_NICKNAME_2, DEFAULT_MESSAGE_BODY);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME_2);
		assertTrue(MUST_BE_VALID_SEND, !messages.contains(SERVER_NICKNAME));
		
		chat.changeNickname(DEFAULT_NICKNAME_2, DEFAULT_NICKNAME_3);
		messages = chat.getMessagesForUser(DEFAULT_NICKNAME_2);
		assertTrue(MUST_HAVE_SERVER_AS_SENDER, messages.contains(SERVER_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(NICKNAME_ALREADY_TAKEN));
		
		chat.changeNickname(DEFAULT_NICKNAME_2, SERVER_NICKNAME);
		messages = chat.getMessagesForUser(DEFAULT_NICKNAME_2);
		assertTrue(MUST_HAVE_SERVER_AS_SENDER, messages.contains(SERVER_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(NICKNAME_ALREADY_TAKEN));
	}

	@Test
	public void testSendMessage() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_2);
		chat.sendMessage(DEFAULT_NICKNAME, DEFAULT_NICKNAME_2, DEFAULT_MESSAGE_BODY);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME_2);
		assertTrue(MUST_HAVE_SENDER, messages.contains(DEFAULT_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(DEFAULT_MESSAGE_BODY));
	}

	@Test
	public void testSendAllMessage() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_2);
		chat.sendAllMessage(DEFAULT_NICKNAME, DEFAULT_MESSAGE_BODY);
		String messages1 = chat.getMessagesForUser(DEFAULT_NICKNAME);
		String messages2 = chat.getMessagesForUser(DEFAULT_NICKNAME_2);
		assertEquals(MUST_BE_SAME_MESSAGE, messages1, messages2);
		assertTrue(MUST_HAVE_SENDER, messages1.contains(DEFAULT_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages1.contains(DEFAULT_MESSAGE_BODY));
	}

	@Test
	public void testSendWrongCommandMessage() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.sendWrongCommandMessage(DEFAULT_NICKNAME);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME);
		assertTrue(MUST_HAVE_SERVER_AS_SENDER, messages.contains(SERVER_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(WRONG_COMMAND));
	}

	@Test
	public void testListUsers() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_2);
		chat.listUsers(DEFAULT_NICKNAME);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME);
		assertTrue(MUST_LIST_USERS, messages.contains(DEFAULT_NICKNAME + NICKNAMES_DELIMITER + DEFAULT_NICKNAME_2));
	}

	@Test
	public void testRemoveUser() {
		Chat chat = new Chat();
		chat.createUser(DEFAULT_NICKNAME);
		chat.createUser(DEFAULT_NICKNAME_2);
		chat.removeUser(DEFAULT_NICKNAME_2);
		chat.sendMessage(DEFAULT_NICKNAME, DEFAULT_NICKNAME_2, DEFAULT_MESSAGE_BODY);
		String messages = chat.getMessagesForUser(DEFAULT_NICKNAME);
		assertTrue(MUST_HAVE_SERVER_AS_SENDER, messages.contains(SERVER_NICKNAME));
		assertTrue(MUST_HAVE_RIGHT_MESSAGE_BODY, messages.contains(NICKNAME_NOT_FOUND));
	}

}
