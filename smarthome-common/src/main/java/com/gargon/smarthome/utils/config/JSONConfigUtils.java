package com.gargon.smarthome.utils.config;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import org.json.JSONObject;

public class JSONConfigUtils {

    /**
     * Получает устройство по значению из конфига.
     * Первоначально пытается прочитать как строковое значение, соответствующее {@code SmarthomeDevice},
     * в случае неудачи пытается прочитать как адрес устройства.
     */
    public static SmarthomeDevice getConfigDevice(String value) {
        SmarthomeDevice device = null;
        try {
            device = SmarthomeDevice.valueOf(value);
        } catch (Exception e1) {
            try {
                device = SmarthomeDevice.getByAddress(Integer.parseInt(value));
            } catch (Exception e2) {
            }
        }
        return device;
    }

    public static SmarthomeDevice getConfigDevice(JSONObject config, String key) {
        return getConfigDevice(config.optString(key, null));
    }

    /**
     * Получает команду по значению из конфига.
     * Первоначально пытается прочитать как строковое значение, соответствующее {@code SmarthomeCommand},
     * в случае неудачи пытается прочитать как код команды.
     */
    public static SmarthomeCommand getConfigCommand(String value) {
        SmarthomeCommand command = null;
        try {
            command = SmarthomeCommand.valueOf(value);
        } catch (Exception e1) {
            try {
                command = SmarthomeCommand.getByCode(Integer.parseInt(value));
            } catch (Exception e2) {
            }
        }
        return command;
    }

    public static SmarthomeCommand getConfigCommand(JSONObject config, String key) {
        return getConfigCommand(config.optString(key, null));
    }
}
