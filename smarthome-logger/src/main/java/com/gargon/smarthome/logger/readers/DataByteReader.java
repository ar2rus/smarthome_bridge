package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DataByteReader implements Reader {

    private int idByte = -1;
    private int valueByte = -1;

    public DataByteReader(JSONObject config) {
        if (config != null) {
            idByte = config.optInt("idByte", -1);
            valueByte = config.optInt("valueByte", -1);
        }

        if (idByte < 0 && valueByte < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {

        if (smarthomeMessage.getData().length > idByte && smarthomeMessage.getData().length > valueByte) {

            String id = null;
            if (idByte >= 0) {
                id = String.format("%02x", smarthomeMessage.getData()[idByte] & 0xff);
            }

            Object value = null;
            if (valueByte >= 0) {
                value = smarthomeMessage.getData()[valueByte];
            }

            Map<String, Object> r = new HashMap<>();
            r.put(id, value);

            return r;
        }
        return null;
    }

}
