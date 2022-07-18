package com.gargon.smarthome.logger.events;

import org.json.JSONObject;

public class LogEventFactory {

    public enum LogEventEnum{
        ALWAYS("always"),
        PERIODIC("period"),
        ONCHANGE("change");

        private String alias;

        LogEventEnum(String alias){
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }

        public static LogEventEnum getByAlias(String alias){
            for (LogEventEnum event : values()){
                if (event.getAlias().equals(alias)){
                    return event;
                }
            }
            return null;
        }
    }

    public static LogEvent createLogEvent(JSONObject config) {
        if (config != null) {
            String logEventName = config.optString("type", LogEventEnum.ALWAYS.toString());

            LogEventEnum logEvent;
            try {
                logEvent = LogEventEnum.valueOf(logEventName);
            } catch (Exception e) {
                logEvent = LogEventEnum.getByAlias(logEventName);
            }

            if (logEvent != null) {
                switch (logEvent) {
                    case ALWAYS:
                        return new AlwaysLogEvent(config);
                    case ONCHANGE:
                        return new OnChangeValueLogEvent(config);
                    case PERIODIC:
                        return new PeriodicLogEvent(config);
                }
            } else {
                throw new IllegalArgumentException("Unknown logEvent: " + logEventName);
            }
        }
        return null;
    }

}
