package com.mochaleaf.cordova.plugin;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mochaleaf.cordova.plugin.WebSocket.CallbackType;

/**
 * This class echoes a string called from JavaScript.
 */
public class WebSocketPlugin extends CordovaPlugin {
  WebSocket socket;
	HashMap <CallbackType, CallbackContext> contexts;
	
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    	if (action.equals("createSocket")) {
            String url = args.getString(0); 
            try {
				this.createSocket(url, callbackContext);
			} catch (URISyntaxException e) {
				callbackContext.error("Invalid URL passed in to websocket.");
				return false;
			}
            return true;
        } else if (action.equals("send")) {
            String message = args.getString(0); 
        	this.send(message, callbackContext);
        	return true;
        } else if (action.equals("close")) {
        	this.close(callbackContext);
        	return true;
        } else if (action.equals("registerCallback")) {
            String type = args.getString(0); 
            this.registerCallback(type, callbackContext);
        	return true;
        } 
        
        return false;
    }

    // Triggered from our WebSocket subclass to send a particular callback
    public void triggerCallback(CallbackType callbackType, String... data) {
		CallbackContext context = this.contexts.get(callbackType);

    	if (context != null) {
    		if (data.length == 1) {
        		context.success(data[0]);    			
    		} else {
    			context.success();
    		}
    	}
    }
    
    // Creates a websocket to use
    private void createSocket(String url, CallbackContext callbackContext) throws URISyntaxException {
    	this.socket = new WebSocket(url, this);
    	this.contexts = new HashMap <CallbackType, CallbackContext>();
    	callbackContext.success();
    }
    
    // Opens our socket if it exists
    private void send(String message, CallbackContext callbackContext) {
    	if (null != socket) {
    		this.socket.send(message);
    		callbackContext.success();
    	} else {
    		callbackContext.error("We do not have a socket open.");
    	}
    }
    
    // Closes our socket
    private void close(CallbackContext callbackContext) {
    	this.socket = null;
    	this.contexts = null;

    	callbackContext.success();
    }
    
    // Registers a callback to use
    private void registerCallback(String type, CallbackContext callbackContext) {
    	if (type.equals("onerror")) {
    		this.contexts.put(CallbackType.ONERROR, callbackContext);
    	} else if (type.equals("onopen")) {
    		this.contexts.put(CallbackType.ONOPEN, callbackContext);
    	} else if (type.equals("onclose")) {
    		this.contexts.put(CallbackType.ONCLOSE, callbackContext);
    	} else if (type.equals("onmessage")) {
    		this.contexts.put(CallbackType.ONMESSAGE,callbackContext);
    	}
    }
}
