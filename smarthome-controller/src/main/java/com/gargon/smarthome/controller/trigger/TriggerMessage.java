package com.gargon.smarthome.controller.trigger;

import java.util.ArrayList;
import java.util.List;

import com.gargon.smarthome.utils.config.JSONConfigUtils;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import org.json.JSONObject;

public class TriggerMessage {

    private SmarthomeDevice src;
    private SmarthomeDevice dst;
    private SmarthomeCommand cmd;
    private String data;

    private static final String KEY_SRC = "src";
    private static final String KEY_DST = "dst";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_DATA = "data";

    public static final TriggerMessage parseJson(JSONObject config) {
        TriggerMessage tt = new TriggerMessage();
        tt.src = JSONConfigUtils.getConfigDevice(config, KEY_SRC);
        tt.dst = JSONConfigUtils.getConfigDevice(config, KEY_DST);
        tt.cmd = JSONConfigUtils.getConfigCommand(config, KEY_CMD);
        tt.data = config.optString(KEY_DATA, null); //use empty String for empty data, or null -> for no matter data
        return tt;
    }

    public SmarthomeDevice getSrc() {
        return src;
    }

    public SmarthomeDevice getDst() {
        return dst;
    }

    public SmarthomeCommand getCmd() {
        return cmd;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        List<String> s = new ArrayList();
        if (src != null) {
            s.add("src=" + src);
        }
        if (dst != null) {
            s.add("dst=" + dst);
        }
        if (cmd != null) {
            s.add("cmd=" + cmd);
        }
        if (data != null) {
            s.add("data=" + data);
        }
        return String.join("; ", s);
    }
    
    

}
