package com.gargon.smarthome.logger.filters;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import org.json.JSONObject;


public class DataByteFilter implements Filter {

    private int dataIndex = -1;

    private Integer eq = null;
    private Integer ne = null;

    protected DataByteFilter(int dataIndex, boolean isEqual, int value) {
        this.dataIndex = dataIndex;
        if (isEqual) {
            eq = value;
        } else {
            ne = value;
        }

        if (this.dataIndex < 0) {
            throw new IllegalArgumentException();
        }
    }
    
    public DataByteFilter(JSONObject config) {
        if (config != null) {
            dataIndex = config.optInt("byte", -1);
            if (config.has("eq")) {
                try {
                    eq = config.getInt("eq");
                } catch (Exception e) {
                    eq = null;
                }
            } else if (config.has("ne")) {
                try {
                    ne = config.getInt("ne");
                } catch (Exception e) {
                    ne = null;
                }
            }
        }

        if ((eq == null && ne == null) || dataIndex < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(SmarthomeMessage smarthomeMessage) {
        if (smarthomeMessage.getData().length > dataIndex) {
            if (eq != null) {
                return smarthomeMessage.getData()[dataIndex] == eq;
            } else if (ne != null) {
                return smarthomeMessage.getData()[dataIndex] != ne;
            }
        }
        return true;
    }

}
