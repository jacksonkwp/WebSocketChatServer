package chat;

import lombok.Data;

@Data
public class User {
	private String name = "unknown";
	private SocketEndPoint endPoint;
	private boolean online;
}
