package com.gargon.smarthome.logger.filters;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import org.json.JSONObject;

public class DstFilter extends AbstractDeviceFilter {

    public DstFilter(JSONObject config) {
        super(config);
    }

    @Override
    public boolean filter(SmarthomeMessage smarthomeMessage) {
        return super.filter(smarthomeMessage.getDst());
    }

}
