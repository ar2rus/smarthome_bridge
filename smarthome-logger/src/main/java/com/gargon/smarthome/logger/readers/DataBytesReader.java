package com.gargon.smarthome.logger.readers;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.utils.HexDataUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataBytesReader implements Reader {

    private int idByte0 = -1;
    private int idByte1 = -1;
    private int valueByte0 = -1;
    private int valueByte1 = -1;

    public DataBytesReader(int idByte0, int idByte1, int valueByte0, int valueByte1) {
        this.idByte0 = idByte0;
        this.idByte1 = idByte1;
        this.valueByte0 = valueByte0;
        this.valueByte1 = valueByte1;

        if ((idByte0 < 0 && valueByte0 < 0)
                || (idByte0 > 0 && idByte1 <= idByte0)
                || (valueByte0 > 0 && valueByte1 <= valueByte0)) {
            throw new IllegalArgumentException();
        }
    }

    public DataBytesReader(JSONObject config) {
        if (config != null) {
            idByte0 = config.optInt("idByte0", -1);
            idByte1 = config.optInt("idByte1", -1);
            valueByte0 = config.optInt("valueByte0", -1);
            valueByte1 = config.optInt("valueByte1", -1);
        }

        if ((idByte0 < 0 && valueByte0 < 0)
                || (idByte0 > 0 && idByte1 <= idByte0)
                || (valueByte0 > 0 && valueByte1 <= valueByte0)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Map<String, Object> perform(SmarthomeMessage smarthomeMessage) {
        if (smarthomeMessage.getData().length > idByte1 && smarthomeMessage.getData().length > valueByte1) {

            String id = null;
            if (idByte1 > 0) {
                id = HexDataUtils.bytesToHex(Arrays.copyOfRange(smarthomeMessage.getData(), idByte0, idByte1 + 1));
            }

            Object value = null;
            if (valueByte1 > 0) {
                value = HexDataUtils.bytesToHex(Arrays.copyOfRange(smarthomeMessage.getData(), valueByte0, valueByte1 + 1));
            }

            Map<String, Object> r = new HashMap<>();
            r.put(id, value);

            return r;
        }
        return null;
    }

}
