package chat;

import lombok.Data;

@Data
public class User {
	String name = "unknown";
	SocketEndPoint endPoint;
	boolean dnd;
}
