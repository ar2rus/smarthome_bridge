package com.gargon.smarthome.controller;

import com.gargon.smarthome.controller.server.*;
import com.gargon.smarthome.controller.trigger.TriggerController;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.multicast.MulticastConnection;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.protocol.SmarthomeMessageFilter;
import com.gargon.smarthome.utils.config.JSONConfigReader;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmarthomeController {

    private static MulticastConnection multicastConnection;

    private static TriggerController triggerController = null;

    private static HttpServer httpServer = null;

    private static final Logger LOG = Logger.getLogger(SmarthomeController.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int http_port = -1;
        String triggerControllerConfig = null;

        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "-http":
                    try {
                        http_port = Integer.parseInt(args[++i]);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Incorrect HTTP port specified (\"{0}\"). Skip HTTP server initialization", args[1]);
                    }
                    break;
                case "-c":
                    try {
                        triggerControllerConfig = args[++i];
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "TriggerController config required. Skip triggerController initialization", args[1]);
                    }
                    break;
            }

            i++;
        }

        try {
            multicastConnection = new MulticastConnection();

            if (triggerControllerConfig != null) {
                try {
                    JSONObject config = JSONConfigReader.read(triggerControllerConfig);
                    if (config != null) {
                        triggerController = new TriggerController(config.optJSONArray("tasks"));
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error while initializing TriggerController: " + e.getMessage(), e);
                }
            }

            multicastConnection.bindListener(new SmarthomeDataListener() {
                @Override
                public void messageReceived(SmarthomeConnection smarthomeConnection, SmarthomeMessage smarthomeMessage) {
                    if (triggerController != null) {
                        triggerController.trigger(smarthomeConnection, smarthomeMessage);
                    }
                }
            });

            LOG.log(Level.INFO, "Open multicast connection");
            multicastConnection.open();

            if (http_port > 0) {
                LOG.log(Level.INFO, "Starting HTTP server ({0} port)", http_port);
                //http server
                httpServer = HttpServer.create(new InetSocketAddress(http_port), 0);
                httpServer.createContext(MainHTTPHandler.URI, new MainHTTPHandler());
                httpServer.createContext(SendHTTPHandler.URI, new SendHTTPHandler(new SendHTTPCallback() {
                    @Override
                    public boolean send(SmarthomeDevice dst, SmarthomeCommand command, byte[] data) {
                        if (data == null) {
                            data = new byte[]{};
                        }
                        return multicastConnection.sendData(dst, command, data);
                    }
                }));
                httpServer.createContext(AskHTTPHandler.URI, new AskHTTPHandler(new AskHTTPCallback() {
                    @Override
                    public byte[] ask(SmarthomeDevice dst, SmarthomeCommand command, byte[] data,
                                      final SmarthomeDevice rsrc, final SmarthomeCommand rcmd, int rtimeout) {
                        if (data == null) {
                            data = new byte[]{};
                        }
                        SmarthomeMessage m = multicastConnection.sendDataAndWaitResponse(dst, command, data,
                                new SmarthomeMessageFilter() {
                                    @Override
                                    public boolean filter(SmarthomeMessage smarthomeMessage) {
                                        return smarthomeMessage.getSrc() == rsrc && smarthomeMessage.getCommand() == rcmd;
                                    }
                                }, rtimeout);
                        if (m != null) {
                            return m.getData();
                        }
                        return null;
                    }
                }));

                httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
                httpServer.start();
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (httpServer != null) {
                        httpServer.stop(1);
                    }

                    if (multicastConnection != null) {
                        multicastConnection.close();
                    }

                    LOG.log(Level.INFO, "Application closed");
                }
            });

        } catch (Exception e) {

            if (multicastConnection != null) {
                multicastConnection.close();
            }

            LOG.log(Level.SEVERE, "initialization error", e);
        }
    }

}
