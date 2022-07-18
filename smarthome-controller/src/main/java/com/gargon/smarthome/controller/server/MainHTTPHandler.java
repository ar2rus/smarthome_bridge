package com.gargon.smarthome.controller.server;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class MainHTTPHandler extends SupradinHTTPHandler {

    public static final String URI = "/";

    public MainHTTPHandler() {
        super(URI);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "<http>\n<body>";
        response += "\n<form action=\"" + AskHTTPHandler.URI + "\" method=\"GET\">";
        response += "\n<b>ASK:</b><br/>";

        response += "\n" + PARAM_DST + "*:";
        response += "\n<select name=\"" + PARAM_DST + "\">";
        for (SmarthomeDevice device : SmarthomeDevice.values()) {
            response += "<option value=\"" + device.getAddress() + "\">" + device.toString() + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_CMD + "*:";
        response += "\n<select name=\"" + PARAM_CMD + "\">";
        for (SmarthomeCommand command : SmarthomeCommand.values()) {
            response += "<option value=\"" + command.getCode() + "\">" + command.toString() + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_DAT + ":";
        response += "\n<input name=\"" + PARAM_DAT + "\"><br/>";

        response += "\n" + PARAM_RESPONSE_SRC + "*:";
        response += "\n<select name=\"" + PARAM_RESPONSE_SRC + "\">";
        for (SmarthomeDevice device : SmarthomeDevice.values()) {
            if (device != SmarthomeDevice.BROADCAST) {
                response += "<option value=\"" + device.getAddress() + "\">" + device.toString() + "</option>";
            }
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_RESPONSE_CMD + "*:";
        response += "\n<select name=\"" + PARAM_RESPONSE_CMD + "\">";
        for (SmarthomeCommand command : SmarthomeCommand.values()) {
            response += "<option value=\"" + command.getCode() + "\">" + command.toString() + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_RESPONSE_FORMAT + "*:";
        response += "\n<select name=\"" + PARAM_RESPONSE_FORMAT + "\">";
        for (ResponseFormatType f : ResponseFormatType.values()) {
            response += "<option value=\"" + f + "\">" + f + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_RESPONSE_TIMEOUT + ":";
        response += "\n<input name=\"" + PARAM_RESPONSE_TIMEOUT + "\">ms<br/>";

        response += "<input type=\"submit\" value=\"ASK\"/>";

        response += "\n</form><br/><br/>";


        //SEND

        response += "\n<form action=\"" + SendHTTPHandler.URI + "\" method=\"GET\">";
        response += "\n<b>SEND:</b><br/>";

        response += "\n" + PARAM_DST + "*:";
        response += "\n<select name=\"" + PARAM_DST + "\">";
        for (SmarthomeDevice device : SmarthomeDevice.values()) {
            response += "<option value=\"" + device.getAddress() + "\">" + device.toString() + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_CMD + "*:";
        response += "\n<select name=\"" + PARAM_CMD + "\">";
        for (SmarthomeCommand command : SmarthomeCommand.values()) {
            response += "<option value=\"" + command.getCode() + "\">" + command.toString() + "</option>";
        }
        response += "\n</select><br/>";

        response += "\n" + PARAM_DAT + ":";
        response += "\n<input name=\"" + PARAM_DAT + "\"><br/>";

        response += "<input type=\"submit\" value=\"SEND\"/>";

        response += "\n</form><br/><br/>";

        response += "\n</body></http>";
        sendHtmlResponse(t, 200, response);
    }

}
