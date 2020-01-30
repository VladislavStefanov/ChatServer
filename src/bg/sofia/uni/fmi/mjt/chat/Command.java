package bg.sofia.uni.fmi.mjt.chat;

import java.util.Arrays;

public enum Command {
	NICK("nick"), SEND("send"), SEND_ALL("send-all"), LIST_USERS("list-users"), DISCONNECT("disconnect");

	private String commandName;

	private Command(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandName() {
		return commandName;
	}

	public static Command from(String commandName) {
		return Arrays.stream(values()).filter(command -> commandName.equalsIgnoreCase(command.getCommandName()))
				.findFirst().orElse(null);
	}
}
