package com.gargon.smarthome.controller.trigger;

import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.utils.HexDataUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TriggerController {

    private List<TriggerTask> tasks = new ArrayList();

    private static final Logger LOG = Logger.getLogger(TriggerController.class.getName());

    public TriggerController(JSONArray config) {
        if (config != null) {
            for (int i = 0; i < config.length(); i++) {
                JSONObject obj = config.getJSONObject(i);
                TriggerTask tt = TriggerTask.parseJson(obj);
                if (tt.getCommand().hasCommand()) {
                    tasks.add(tt);
                } else {
                    LOG.log(Level.WARNING, "Empty task for trigger command: \"{0}\"", obj.toString());
                }
            }
        }
        LOG.log(Level.INFO, "TriggerController activated with {0} tasks", tasks.size());
    }

    public void trigger(SmarthomeConnection connection, SmarthomeMessage message) {
        for (final TriggerTask tt : tasks) {
            List<String> match_groups;
            if ((match_groups = tt.match(message)) != null) {
                final String scriptCommand = tt.getCommand().getScriptCommand();
                if (scriptCommand != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<String> command = new ArrayList();
                                command.add(scriptCommand);
                                command.addAll(match_groups);
                                ProcessBuilder pb = new ProcessBuilder(command);
                                pb.start();
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, "Error while executing task: \"{0}\": {1}",
                                        new Object[]{scriptCommand, ex.getMessage()});
                            }
                        }
                    }).start();
                } else {
                    TriggerMessage m = tt.getCommand().getMessageCommand();
                    try {
                        String data = m.getData();
                        //FF/2/22AA -> replace group_22, replace group_2
                        for (int i = match_groups.size() - 1; i >= 0; i--) {
                            data = data.replaceAll("/" + i, match_groups.get(i));
                        }

                        byte[] byte_array = HexDataUtils.hexToByteArray(data);

                        connection.sendData(m.getDst(), m.getCmd(), byte_array);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Error while sending command \"{0}\": {1}",
                                new Object[]{m, e.getMessage()});
                    }

                }
                LOG.log(Level.INFO, "Triggered task: \"{0}\" for message: \"{1}\"",
                        new Object[]{tt.getCommand().toString(), message.toString()});
            }
        }
    }
}
