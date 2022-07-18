package com.gargon.smarthome.controller.trigger;

import com.gargon.smarthome.protocol.SmarthomeMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargon.smarthome.utils.HexDataUtils;
import org.json.JSONObject;

public class TriggerTask {

    private TriggerMessage message;
    private TriggerCommand command;

    public static final TriggerTask parseJson(JSONObject config) {
        TriggerTask tt = new TriggerTask();
        tt.message = TriggerMessage.parseJson(config);
        tt.command = TriggerCommand.parseJson(config);

        return tt;
    }

    public TriggerMessage getMessage() {
        return message;
    }

    public TriggerCommand getCommand() {
        return command;
    }

    /**
     * Проверяет соответствие {@code test_data} сообщения
     * шаблону регулярного выражения {@code pattern_data}
     *
     * @param pattern_data
     * @param test_data
     * @return null, если соответствия нет; список найденных групп,
     * если соответствие есть
     */
    private List<String> check_regex_data(String pattern_data, byte[] test_data) {
        if (pattern_data == null) {
            return Collections.emptyList();
        }

        String test_hex = HexDataUtils.bytesToHex(test_data);
        if (test_hex == null) {
            return null;
        }

        Pattern p = Pattern.compile(pattern_data);
        Matcher m = p.matcher(test_hex);

        if (m.matches()) {
            m.reset();
            List<String> groups = new ArrayList<>();
            if (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    groups.add(m.group(i));
                }
            }
            return groups;
        } else {
            return null;
        }
    }

    public List<String> match(SmarthomeMessage m) {
        if (m != null) {
            if ((message.getSrc() == null || message.getSrc() == m.getSrc())
                    && (message.getDst() == null || message.getDst() == m.getDst())
                    && (message.getCmd() == null || message.getCmd() == m.getCommand())) {
                return check_regex_data(message.getData(), m.getData());
            }
        }
        return null;
    }
}
