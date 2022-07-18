package com.gargon.smarthome.utils.http;

import com.gargon.smarthome.enums.Address;
import com.gargon.smarthome.enums.Command;
import com.gargon.smarthome.enums.Priority;
import com.gargon.smarthome.utils.HexDataUtils;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class SendHTTPHandler extends SupradinHTTPHandler {
    
    public static final String URI = "/send";
    
    protected final SendHTTPCallback callback;
    
    public SendHTTPHandler(SendHTTPCallback callback) {
        super(URI);
        this.callback = callback;
    }
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = parseParams(t);

        //try to parse params
        String response = "Error while sending";
        int responseCode = 500;
        
        try {
            int dst = Integer.parseInt(params.get(PARAM_DST));
            int pri = Integer.parseInt(params.get(PARAM_PRI));
            int cmd = Integer.parseInt(params.get(PARAM_CMD));
            String hexData = "";
            if (params.containsKey(PARAM_DAT)
                    && !params.get(PARAM_DAT).isEmpty()) {
                hexData = params.get(PARAM_DAT);
            }

            //send command 
            try {
                if (callback.send(dst, pri, cmd, HexDataUtils.hexToByteArray(hexData))) {
                    response = "Ok";
                    responseCode = 200;
                }
            } catch (Exception e) {
            }
            
        } catch (Exception e) {
            response = "Wrong request format!"
                    + "\n\n\nusage: http://HOST:PORT" + URI + "?" + PARAM_DST + "={dst_id}&" + PARAM_PRI + "={priority}&" + PARAM_CMD + "={command_id}[&" + PARAM_DAT + "={hex data}]"
                    + "\n\ndst_id = {";
            for (Address address : Address.values()) {
                response += "<option value=\"" + address.getValue() + "\">" + address.toString() + "</option>";
            }
            
            response += "}";
            response += "\n\npriority = {";
            for (Priority priority : Priority.values()) {
                response += "<option value=\"" + priority.getValue() + "\">" + priority.toString() + "</option>";
            }
            response += "}";
            response += "\n\ncommand_id = {";
            for (Command command : Command.values()) {
                response += "<option value=\"" + command.getValue() + "\">" + command.toString() + "</option>";
            }
            response += "}";
            
            responseCode = 400;
        }
        
        sendTextResponse(t, responseCode, response);
    }

}
