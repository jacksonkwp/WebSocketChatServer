package chat;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
        JSONObject jsonMessage;
        try {
            jsonMessage = new JSONObject(s);
        } catch (JSONException e) {
            throw new DecodeException(s, "Invalid JSON format", e);
        }
        
        Message message = new Message();

        if (jsonMessage.has("message")) {
            try {
				message.setMessage(jsonMessage.getString("message"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if (jsonMessage.has("online")) {
            try {
				message.setOnline(jsonMessage.getBoolean("online"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return message;
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
