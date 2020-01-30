package bg.sofia.uni.fmi.mjt.chat;

public enum ErrorMessageBody {
	SELECTOR_UNABLE_TO_SELECT_KEYS("Selector unable to select keys."), 
	CHANNEL_IS_CLOSED("Channel is closed."), 
	UNABLE_TO_INITIALIZE_APPLICATION("Unable to initialize application."),
	IO_EXCEPTION_HAS_OCCURRED("IOException has occurred."),
	READING_EXCEPTION_HAS_OCCURRED("An exception when reading has occurred."),
	WRITING_EXCEPTION_HAS_OCCURRED("An exception when writing has occurred."),
	WRONG_COMMAND("Such command does not exist."), 
	NICKNAME_ALREADY_EXISTS("Nickname already taken."),
	NICKNAME_DOES_NOT_EXIST("Nickname not found.");
	
	private String messageBody;

	private ErrorMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageBody() {
		return messageBody;
	}
}
