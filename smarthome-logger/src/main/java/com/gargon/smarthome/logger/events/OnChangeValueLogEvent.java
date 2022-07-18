package com.gargon.smarthome.logger.events;

import com.gargon.smarthome.logger.TimeBoundSmarthomeMessage;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class OnChangeValueLogEvent extends LogEvent {

    private final Map<String, Object> lastEvents = new HashMap();

    public OnChangeValueLogEvent(JSONObject config) {
        super(config);
    }

    @Override
    public boolean approve(TimeBoundSmarthomeMessage message) {
        boolean approve = false;
        if (filter(message)) {
            Map<String, Object> v = read(message);
            for (Map.Entry<String, Object> entry : v.entrySet()) {
                String key = message.getSrc() + ";" + entry.getKey();
                if (lastEvents.containsKey(key)) {
                    if (!lastEvents.get(key).equals(entry.getValue())) {
                        approve = true;
                    }
                } else {
                    approve = true;
                }
                lastEvents.put(key, entry.getValue());
            }
        }
        return approve;
    }

}
