package chat;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@ServerEndpoint(value = "/chat/{name}", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
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
		getAllUsers();
        
        //notify users of new user
        //JSONObject msg = new JSONObject();
		Message joinMsg = new Message();
        joinMsg.setMessage(newUser.getName() + " joined the chat.");
        broadcast(joinMsg);

    }

    @OnMessage
    public void onMessage(Session session, Message msg) throws IOException, EncodeException {
        //broadcast the message if there is one
        if (msg.getMessage() != null) {
            //look up sender for their name
            String sender = "unknown";
            for (User user : users) {
                if (user.getEndPoint().session.getId().equals(session.getId())) {
                    sender = user.getName();
                    break;
                }
            }

            //forward the message to the other users with sender info
            Message forwardMsg = new Message();
            forwardMsg.setMessage(sender + ": " + msg.getMessage());
            broadcast(forwardMsg);
        }

        //update the status if its passed
        if (msg.hasOnlineStatus()) {
            //look up sender and update their status
            for (User user : users) {
                if (user.getEndPoint().session.getId().equals(session.getId())) {
                    user.setOnline(msg.isOnline());
                    Message updateMsg = new Message();
                    updateMsg.setMessage(String.format("%s has updated to %s.", user.getName(), (user.isOnline() ? "online" : "do not disturb")));
                    broadcast(updateMsg);
                    getAllUsers();
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
        //JSONObject msg = new JSONObject();
        Message leaveMsg = new Message();
        leaveMsg.setMessage(leavingUser + " left the chat.");
        broadcast(leaveMsg);
		getAllUsers();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("OnError: An error occured");
    }

    private static void getAllUsers() {
        ObjectMapper mapper = new ObjectMapper();
        users.forEach(user -> {
            synchronized (user) {
                try {
                    String json = mapper.writeValueAsString(users);
                    System.out.println("user " + json);
                    Message msg = new Message();
                    msg.setUsers(json);
                    user.getEndPoint().session
                            .getBasicRemote()
                            .sendObject(msg);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    
	private static void broadcast(Message msg) throws IOException, EncodeException {
    	
    	//send msg to each user that is online
    	users.forEach(user -> {
    		synchronized (user) {
    			try {
    				if (user.isOnline()) {
	    				try {
							user.getEndPoint().session
								.getBasicRemote()
								.sendObject(msg);
						} catch (EncodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    			} catch (IOException e) {
    				System.out.println("Brodcast: An error occured");
    			}
    		}
    	});
	}
}