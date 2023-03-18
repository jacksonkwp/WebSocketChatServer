package chat;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public String encode(Message msg) throws EncodeException {
	    JSONObject json = new JSONObject();
	    if (msg.getMessage() != null) {
	        try {
				json.put("message", msg.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    if (msg.hasOnlineStatus()) {
	        try {
				json.put("online", msg.isOnline());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    if (msg.getUsers() != null) {
	        try {
				json.put("users", msg.getUsers());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return json.toString();
	}


    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
