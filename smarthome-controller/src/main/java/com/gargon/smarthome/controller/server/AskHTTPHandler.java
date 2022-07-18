package com.gargon.smarthome.controller.server;

import com.gargon.smarthome.SmarthomeMessageDecoder;
import com.gargon.smarthome.controller.server.utils.CommonUtils;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.utils.HexDataUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AskHTTPHandler extends SupradinHTTPHandler {

    public static final String URI = "/ask";

    protected final AskHTTPCallback callback;

    public AskHTTPHandler(AskHTTPCallback callback) {
        super(URI);
        this.callback = callback;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = parseParams(t);

        String response = null;
        int responseCode;

        try {
            SmarthomeDevice dst = SmarthomeDevice.valueOf(params.get(PARAM_DST));
            SmarthomeCommand cmd = SmarthomeCommand.valueOf(params.get(PARAM_CMD));
            String hexData = params.get(PARAM_DAT);
            SmarthomeDevice rsrc = SmarthomeDevice.valueOf(params.get(PARAM_RESPONSE_SRC));
            SmarthomeCommand rcmd = SmarthomeCommand.valueOf(params.get(PARAM_RESPONSE_CMD));
            ResponseFormatType rft = ResponseFormatType.valueOf(CommonUtils.getString(params.get(PARAM_RESPONSE_FORMAT), ResponseFormatType.TXT.name()));
            int rtm = Integer.parseInt(CommonUtils.getString(params.get(PARAM_RESPONSE_TIMEOUT), "3000")); //3 sec

            //send command 
            try {
                byte[] rdata = callback.ask(dst, cmd, HexDataUtils.hexToByteArray(hexData), rsrc, rcmd, rtm);
                if (rdata == null) {
                    throw new RuntimeException("Ask command error");
                }
                switch (rft) {
                    case HEX:
                        response = HexDataUtils.bytesToHex(rdata);
                        break;
                    case TXT:
                        response = SmarthomeMessageDecoder.toString(rcmd, rdata);
                        break;
                }
                responseCode = 200;
            } catch (Exception e) {
                throw new RuntimeException("Command executing error", e);
            }

        } catch (Exception e) {
//            response = e.getMessage()
//                    + "\n\n\nusage: http://HOST:PORT" + URI + "?" + PARAM_DST + "={dst_id}&"
//                    + PARAM_CMD + "={command_id}[&" + PARAM_DAT + "={hex data}]&" + PARAM_RESPONSE_SRC + "={rsrc_id}&" + PARAM_RESPONSE_CMD + "={rcommand_id}[&"
//                    + PARAM_RESPONSE_FORMAT + "={rformat_id}][&" + PARAM_RESPONSE_TIMEOUT + "={timeout_in_ms}]"
//                    + "\n\ndst_id, rsrc_id = {";
//            response += String.join(", ", new ArrayList(Arrays.asList(SmarthomeDevice.values())));
//            response += "}";
//            response += "\n\ncommand_id, rcommand_id = {";
//            response += String.join(", ", new ArrayList(Arrays.asList(SmarthomeCommand.values())));
//            response += "}";
//            response += "\n\nrformat_id = {";
//            response += String.join(", ", new ArrayList(Arrays.asList(ResponseFormatType.values())));
//            response += "}";
            response = "error";
            responseCode = 500;
        }

        sendTextResponse(t, responseCode, response);
    }

}
