package com.gargon.smarthome.logger.events;

import com.gargon.smarthome.logger.TimeBoundSmarthomeMessage;
import com.gargon.smarthome.logger.filters.Filter;
import com.gargon.smarthome.logger.readers.Reader;
import org.json.JSONObject;

import java.util.List;

public class AlwaysLogEvent extends LogEvent {

    public AlwaysLogEvent(JSONObject config) {
        super(config);
    }

    public AlwaysLogEvent(List<Filter> filters, Reader reader){
        super(filters, reader);
    }

    @Override
    public boolean approve(TimeBoundSmarthomeMessage message) {
        return filter(message);
    }

}
