package com.gargon.smarthome.controller.server;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.utils.HexDataUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
        String response = null;
        int responseCode;

        try {
            SmarthomeDevice dst = SmarthomeDevice.valueOf(params.get(PARAM_DST));
            SmarthomeCommand cmd = SmarthomeCommand.valueOf(params.get(PARAM_CMD));
            String hexData = params.get(PARAM_DAT);

            //send command 
            try {
                if (!callback.send(dst, cmd, HexDataUtils.hexToByteArray(hexData))) {
                    throw new RuntimeException("Send command error");
                }
                response = "Ok";
                responseCode = 200;
            } catch (Exception e) {
                throw new RuntimeException("Command executing error", e);
            }

        } catch (RuntimeException e) {
            response = "Error while sending: \"" + e.getMessage() + "\"";
            responseCode = 500;
        } catch (Exception e) {
            response = "Wrong request format!"
                    + "\n\n\nusage: http://HOST:PORT" + URI + "?" + PARAM_DST + "={dst_id}&" + PARAM_CMD + "={command_id}[&" + PARAM_DAT + "={hex data}]"
                    + "\n\ndst_id = {";
            response += String.join(", ", new ArrayList(Arrays.asList(SmarthomeDevice.values())));
            response += "}";
            response += "\n\ncommand_id = {";
            response += String.join(", ", new ArrayList(Arrays.asList(SmarthomeCommand.values())));
            response += "}";

            responseCode = 400;
        }

        sendTextResponse(t, responseCode, response);
    }

}
