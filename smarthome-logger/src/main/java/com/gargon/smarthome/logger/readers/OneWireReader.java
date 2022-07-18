package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.SmarthomeMessageDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneWireReader implements Reader {

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {

        List<String> v = SmarthomeMessageDecoder.decodeOneWireInfo(smarthomeMessage.getData());
        if (v != null) {
            Map<String, Object> r = new HashMap();
            for (String o : v) {
                r.put(o, o);
            }
            return r;
        }
        return null;
    }

}
