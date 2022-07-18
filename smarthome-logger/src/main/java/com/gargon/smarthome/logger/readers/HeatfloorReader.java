package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.SmarthomeMessageDecoder;

import java.util.Map;

public class HeatfloorReader implements Reader {

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {
        Map<String, Integer> v = SmarthomeMessageDecoder.decodeHeatfloorInfo(smarthomeMessage.getData());
        if (v != null) {
            return (Map) v;
        }
        return null;
    }

}
