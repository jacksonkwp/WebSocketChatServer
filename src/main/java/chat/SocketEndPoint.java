package chat;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.json.*;

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
    		throws IOException, EncodeException, JSONException {
    	
    	//create a new user instance and store it
    	this.session = session;
        User newUser = new User();
        newUser.setEndPoint(this);
        newUser.setName(name);
        newUser.setOnline(true);
        users.add(newUser);
        
        //notify users of new user
        JSONObject msg = new JSONObject();
        msg.put("message", newUser.getName() + " joined the chat.");
        broadcast(msg.toString());
    }

    @OnMessage
    public void onMessage(Session session, String stringMsg) throws
    		IOException, EncodeException, JSONException {
        
    	JSONObject msg = new JSONObject(stringMsg);
    	
    	//broadcast the message if there is one
    	if (msg.has("message")) {
	    	
    		//look up sender for their name
	    	String sender = "unknown";
	        for (User user : users) {
	        	if (user.getEndPoint().session.getId().equals(session.getId())) {
	        		sender = user.getName();
	        		break;
	        	}
	        }
	        
	        //forward the message to the other users with sender info
	        JSONObject forwardMsg = new JSONObject();
	        forwardMsg.put("message", sender + ": " + msg.getString("message"));
	        broadcast(forwardMsg.toString());
	        
    	}
    	
    	//update the status if its passed
    	if (msg.has("online")) {
    		
    		//look up sender and update their status
	        for (User user : users) {
	        	if (user.getEndPoint().session.getId().equals(session.getId())) {
	        		user.setOnline(msg.getBoolean("online"));
					JSONObject updateMsg = new JSONObject();
					updateMsg.put("message", String.format("%s has updated to %s.", user.getName(),(user.isOnline() ? "online":"do not disturb")));
					broadcast(updateMsg.toString());
	        		break;
	        	}
	        }
    	}
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException, JSONException {
    	
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
        JSONObject msg = new JSONObject();
        msg.put("message", leavingUser + " left the chat.");
        broadcast(msg.toString());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("OnError: An error occured");
    }
    
    private static void broadcast(String msg) 
    		throws IOException, EncodeException {
    	
    	//send msg to each user that is online
    	users.forEach(user -> {
    		synchronized (user) {
    			try {
    				if (user.isOnline()) {
	    				user.getEndPoint().session
	    					.getBasicRemote()
	    					.sendText(msg);
    				}
    			} catch (IOException e) {
    				System.out.println("Brodcast: An error occured");
    			}
    		}
    	});
	}
}