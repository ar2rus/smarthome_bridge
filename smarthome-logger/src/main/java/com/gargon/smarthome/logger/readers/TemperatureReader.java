package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.SmarthomeMessageDecoder;

import java.util.Map;

public class TemperatureReader implements Reader {

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {
        Map<String, Float> v = SmarthomeMessageDecoder.decodeTemperatureInfo(smarthomeMessage.getData());
        if (v != null) {
            return (Map) v;
        }
        return null;
    }

}
