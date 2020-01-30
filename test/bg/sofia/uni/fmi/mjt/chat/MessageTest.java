package bg.sofia.uni.fmi.mjt.chat;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageTest {

	private static final String ENDS_WITH = " ends with ";
	private static final String DELIMITER = " : ";
	private static final String DEFAULT_BODY = "alabala";
	private static final String DEFAULT_NICKNAME = "vladi";

	@Test
	public void testMessageWithNickname() {
		Message message = new Message(DEFAULT_NICKNAME, DEFAULT_BODY);
		String suffix = DEFAULT_NICKNAME + DELIMITER + DEFAULT_BODY;
		assertTrue(message.toString() + ENDS_WITH + suffix, message.toString().endsWith(suffix));
		
	}

	@Test
	public void testMessageWithoutNickname() {
		Message message = new Message(DEFAULT_BODY);
		assertTrue(message.toString() + ENDS_WITH + DEFAULT_BODY, message.toString().endsWith(DEFAULT_BODY));
	}

}
