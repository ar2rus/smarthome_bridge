package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.utils.HexDataUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultReader implements Reader {

    @Override
    public Map<String, Object> perform(final SmarthomeMessage smarthomeMessage) {
        return new HashMap<String, Object>() {{
            put(String.valueOf(smarthomeMessage.getSrc()), HexDataUtils.bytesToHex(smarthomeMessage.getData()));
        }};
    }

}
