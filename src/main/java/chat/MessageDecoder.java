package chat;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public Message decode(String string) throws DecodeException {
	    JSONObject json;
	    try {
	        json = new JSONObject(string);
	    } catch (JSONException e) {
	        throw new DecodeException(string, "Failed to decode JSON.", e);
	    }
	    
	    Message msg = new Message();
	    
	    if (json.has("message")) {
	        try {
	            msg.setMessage(json.getString("message"));
	        } catch (JSONException e) {
	            throw new DecodeException(string, "Failed to decode message.", e);
	        }
	    }
	    
	    if (json.has("online")) {
	        try {
	            msg.setOnline(json.getBoolean("online"));
	            msg.setHasOnlineStatus(true);
	        } catch (JSONException e) {
	            throw new DecodeException(string, "Failed to decode online status.", e);
	        }
	    }
	    
	    return msg;
	}



    @Override
    public boolean willDecode(String s) {
        try {
            JSONObject jsonMessage = new JSONObject(s);
            return jsonMessage.has("message") || jsonMessage.has("online");
        } catch (JSONException e) {
            return false;
        }
    }


    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
