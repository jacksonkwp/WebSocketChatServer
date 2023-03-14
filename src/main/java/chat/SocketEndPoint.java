package chat;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint(value = "/chat/{name}")
public class SocketEndPoint {
	
	private Session session;
    private static Set<User> users = new CopyOnWriteArraySet<User>();
	
    @OnOpen
    public void onOpen(Session session,
    		@PathParam("name") String name)
    		throws IOException, EncodeException {
    	
    	//create a new user instance and store it
    	this.session = session;
        User newUser = new User();
        newUser.setEndPoint(this);
        newUser.setName(name);
        users.add(newUser);
        
        //notify users of new user
        broadcast(newUser.getName() + " joined the chat.");
    }

    @OnMessage
    public void onMessage(Session session, String msg) throws IOException, EncodeException {
        
    	//look up sender for their name
    	String sender = "unknown";
        for (User user : users) {
        	if (user.getEndPoint().session.getId().equals(session.getId())) {
        		sender = user.getName();
        		break;
        	}
        }
        
        //forward the message to the other users
        broadcast(sender + ": " + msg);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
    	
    	//remove user
    	String leavingUser = "unknown";
        for (User user : users) {
        	if (user.getEndPoint().session.getId() == session.getId()) {
        		leavingUser = user.getName();
        		users.remove(user);
        		break;
        	}
        }
    	
        //notify other users of the leaving user
        broadcast(leavingUser + " left the chat.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("An error occured");
    }
    
    private static void broadcast(String msg) 
    		throws IOException, EncodeException {
    	 
    	users.forEach(user -> {
    		synchronized (user) {
    			try {
    				user.getEndPoint().session
    					.getBasicRemote()
    					.sendText(msg);
    			} catch (IOException e) {
    				System.out.println("An error occured");
    			}
    		}
    	});
	}
}