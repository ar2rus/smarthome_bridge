package com.gargon.smarthome.controller.server;


import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;

public interface AskHTTPCallback {

    byte[] ask(SmarthomeDevice dst, SmarthomeCommand command, byte[] data,
               SmarthomeDevice rsrc, SmarthomeCommand rcmd, int rtimeout);

}
