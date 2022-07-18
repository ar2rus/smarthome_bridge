package com.gargon.smarthome.logger.events;

import com.gargon.smarthome.utils.config.JSONConfigUtils;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.logger.TimeBoundSmarthomeMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gargon.smarthome.utils.HexDataUtils;
import org.json.JSONObject;


public class PeriodicLogEvent extends LogEvent {

    private final Map<String, TimeBoundSmarthomeMessage> lastEvents = new HashMap();

    private final int period;                         //required
    private final int uniqueCount;
    private final boolean triggerRequired;

    private final TriggerForPeriodicEvent trigger;             //required

    public PeriodicLogEvent(JSONObject config) {
        super(config);

        try {
            period = config.getInt("period");
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong format of 'period' attribute in command of period type");
        }

        try {
            trigger = new TriggerForPeriodicEvent(config.optJSONObject("trigger"));
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong format of 'trigger' attribute in command of period type", e);
        }

        triggerRequired = config.optBoolean("triggerRequired", false);
        uniqueCount = config.optInt("uniqueCount", -1);

        if (!triggerRequired && uniqueCount < 0) {
            throw new IllegalArgumentException("wrong combination of triggerRequired and uniqueCount attributes in command of period type");
        }
    }

    public long getPeriod() {
        return period * 1000;
    }

    public int getTriggerTimeout() {
        return trigger.getTimeout();
    }

    /**
     * @param message
     * @return всегда возвращает false. Сообщения отдаются в отдельном методе по
     * истечению периода. В данном методе только отбираются наиболее актуальные
     * (поступившие последними)
     */
    @Override
    public boolean approve(TimeBoundSmarthomeMessage message) {
        if (filter(message)) {
            Map<String, Object> v = read(message);
            try {
                if (v != null) {
                    for (Map.Entry<String, Object> entry : v.entrySet()) {
                        String key = message.getSrc() + ";" + entry.getKey();
                        lastEvents.put(key, message);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(PeriodicLogEvent.class.getName()).log(Level.SEVERE, "reader format error", e);
            }
        }
        return false;
    }

    public void trigger(SmarthomeConnection connection) {
        if (triggerRequired || lastEvents.size() < uniqueCount) {
            connection.send(SmarthomeDevice.BROADCAST,
                    trigger.getCommand(), trigger.getData());

        }
    }

    public List<TimeBoundSmarthomeMessage> timeout() {
        List<TimeBoundSmarthomeMessage> messages = new ArrayList();
        for (Map.Entry<String, TimeBoundSmarthomeMessage> entry : lastEvents.entrySet()) {
            if (messages.indexOf(entry.getValue()) < 0) {
                messages.add(entry.getValue());
            }
        }

        lastEvents.clear(); //сообщения отдали -> всю историю почистили

        return messages;
    }

    class TriggerForPeriodicEvent {

        private final SmarthomeCommand command;
        private final int timeout;      //время ожидания ответов на запрос тригера (мс)
        private final byte[] data;

        public TriggerForPeriodicEvent(JSONObject config) {
            command = JSONConfigUtils.getConfigCommand(config, "command");
            if (command == null) {
                throw new IllegalArgumentException("unknown command: " + config.toString());
            }
            timeout = config.optInt("timeout", 1000);
            if (config.has("data")) {
                data = HexDataUtils.hexToByteArray(config.getString("data"));
            } else {
                data = null;
            }
        }

        public SmarthomeCommand getCommand() {
            return command;
        }

        public int getTimeout() {
            return timeout;
        }

        public byte[] getData() {
            return data;
        }
    }
}


