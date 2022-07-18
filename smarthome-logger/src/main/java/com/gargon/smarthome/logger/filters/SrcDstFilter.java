package com.gargon.smarthome.logger.filters;

import com.gargon.smarthome.utils.config.JSONConfigUtils;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import org.json.JSONObject;

public class SrcDstFilter implements Filter {

    private SmarthomeDevice src = null;
    private SmarthomeDevice dst = null;

    public SrcDstFilter(JSONObject config) {
        if (config != null) {
            if (config.has("src")) {
                src = JSONConfigUtils.getConfigDevice(config, "src");
            }
            if (config.has("dst")) {
                dst = JSONConfigUtils.getConfigDevice(config, "dst");
            }
        }

        if (src == null && dst == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(SmarthomeMessage smarthomeMessage) {
        return (src == null || src.equals(smarthomeMessage.getSrc()))
                && (dst == null || dst.equals(smarthomeMessage.getDst()));
    }

}
