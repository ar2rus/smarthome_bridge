package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.SmarthomeMessageDecoder;
import com.gargon.smarthome.protocol.SmarthomeMessage;

import java.util.HashMap;
import java.util.Map;

public class DimmerReader implements Reader {

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {

        Map<Integer, Integer> v = SmarthomeMessageDecoder.decodeDimmerInfo(smarthomeMessage.getData());
        if (v != null) {
            Map<String, Object> r = new HashMap();
            for (Map.Entry<Integer, Integer> e : v.entrySet()) {
                r.put(e.getKey().toString(), e.getValue());
            }
            return r;
        }
        return null;
    }

}
