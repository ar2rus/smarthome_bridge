package com.gargon.smarthome.controller.server;


import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;

public interface SendHTTPCallback {

    boolean send(SmarthomeDevice dst, SmarthomeCommand command, byte[] data);

}
