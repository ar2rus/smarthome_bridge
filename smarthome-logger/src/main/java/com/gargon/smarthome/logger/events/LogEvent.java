package com.gargon.smarthome.logger.events;

import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.logger.TimeBoundSmarthomeMessage;
import com.gargon.smarthome.logger.filters.Filter;
import com.gargon.smarthome.logger.filters.FilterFactory;
import com.gargon.smarthome.logger.readers.Reader;
import com.gargon.smarthome.logger.readers.ReaderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


public abstract class LogEvent {

    private final List<Filter> filters = new ArrayList();
    private final Reader reader;

    public LogEvent(JSONObject config) {
        JSONArray filtersJSON = config.optJSONArray("filters");
        if (filtersJSON != null) {
            for (int i = 0; i < filtersJSON.length(); i++) {
                Filter f = FilterFactory.createFilter(filtersJSON.optJSONObject(i));
                if (f != null) {
                    filters.add(f);
                }
            }
        }
        reader = ReaderFactory.createReader(config.optJSONObject("reader"));
    }

    protected LogEvent(List<Filter> filters, Reader reader){
        if (filters != null){
            this.filters.addAll(filters);
        }
        this.reader = reader;
    }

    protected boolean filter(SmarthomeMessage message) {
        for (Filter f : filters) {
            if (!f.filter(message)) {
                return false;
            }
        }
        return true;
    }

    protected Map<String, Object> read(SmarthomeMessage smarthomeMessage) {
        return reader.perform(smarthomeMessage);
    }

    public abstract boolean approve(TimeBoundSmarthomeMessage message);

}
