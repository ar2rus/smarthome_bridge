package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;

import java.util.Map;

public interface Reader {

    Map<String, Object> perform(SmarthomeMessage smarthomeMessage);

}
