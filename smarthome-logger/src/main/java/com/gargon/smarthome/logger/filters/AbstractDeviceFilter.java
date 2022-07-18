package com.gargon.smarthome.logger.filters;

import com.gargon.smarthome.utils.config.JSONConfigUtils;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import org.json.JSONObject;

public abstract class AbstractDeviceFilter implements Filter {

    private SmarthomeDevice eq = null;
    private SmarthomeDevice ne = null;

    public AbstractDeviceFilter(JSONObject config) {
        if (config != null) {
            if (config.has("eq")) {
                eq = JSONConfigUtils.getConfigDevice(config, "eq");
            } else if (config.has("ne")) {
                ne = JSONConfigUtils.getConfigDevice(config, "ne");
            }
        }

        if (eq == null && ne == null) {
            throw new IllegalArgumentException();
        }
    }

    public boolean filter(SmarthomeDevice device) {
        if (eq != null) {
            return device == eq;
        } else if (ne != null) {
            return device != ne;
        }
        return false;
    }

}
