package com.gargon.smarthome.logger;

import com.gargon.smarthome.utils.config.JSONConfigUtils;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.logger.events.AlwaysLogEvent;
import com.gargon.smarthome.logger.filters.NotStateRequestFilter;
import com.gargon.smarthome.logger.readers.DefaultReader;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.logger.listeners.LoggerEventListener;
import com.gargon.smarthome.logger.events.LogEvent;
import com.gargon.smarthome.logger.events.LogEventFactory;
import com.gargon.smarthome.logger.events.PeriodicLogEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public final class LoggerService {

    private static SmarthomeConnection smarthomeConnection;
    private ScheduledExecutorService periodicalEventsScheduledExecutor;

    Map<SmarthomeCommand, List<LogEvent>> events = new HashMap<>();

    private final List<LoggerEventListener> loggerEventListeners = new CopyOnWriteArrayList<>();

    private static final Logger LOG = Logger.getLogger(LoggerService.class.getName());

    public LoggerService(JSONObject commandsСonfig, JSONObject controlСommandsСonfig) throws Exception {
        if (commandsСonfig == null && controlСommandsСonfig == null) {
            LOG.severe("No one commands section defined. Application will be closed");
            return;
        }

        if (controlСommandsСonfig != null) {
            JSONArray exclude = controlСommandsСonfig.optJSONArray("exclude");
            Set<SmarthomeCommand> excludeControlCommandSet = new HashSet<>();
            if (exclude != null) {
                for (int i = 0; i < exclude.length(); i++) {
                    String excludeControlCommandName = exclude.optString(i, null);
                    SmarthomeCommand excludeControlCommand = JSONConfigUtils.getConfigCommand(excludeControlCommandName);
                    if (excludeControlCommand != null) {
                        excludeControlCommandSet.add(excludeControlCommand);
                    } else {
                        LOG.warning("Excluded control command \"" + excludeControlCommandName + "\" is not defined. It skipped.");
                    }
                }
            }
            for (SmarthomeCommand command : SmarthomeCommand.values()) {
                if (command.isControlCommand() && !excludeControlCommandSet.contains(command)) {
                    events.put(command, Collections.singletonList(new AlwaysLogEvent(
                            Collections.singletonList(new NotStateRequestFilter()),
                            new DefaultReader())));
                }
            }
            LOG.info("Loaded " + events.size() + " events for control commands");
        }

        List<PeriodicLogEvent> periodicEvents = new ArrayList<>();
        for (String key : commandsСonfig.keySet()) {
            try {
                SmarthomeCommand command = JSONConfigUtils.getConfigCommand(key);
                if (command == null) {
                    throw new IllegalArgumentException("Unknown command: " + key);
                }

                if (events.containsKey(command)) {
                    LOG.warning("Config already defined for command " + key + ". Overriding with commands section config");
                    events.remove(command);
                }

                //значение может быть как массивом элементов так и отдельным элементом
                JSONArray commandArray = commandsСonfig.optJSONArray(key);
                if (commandArray == null) {
                    commandArray = new JSONArray();
                    commandArray.put(commandsСonfig.optJSONObject(key));
                }

                for (int i = 0; i < commandArray.length(); i++) {
                    LogEvent c = LogEventFactory.createLogEvent(commandArray.optJSONObject(i));
                    if (c != null) {
                        List list = events.get(command);
                        if (list == null) {
                            events.put(command, list = new ArrayList<>());
                        }
                        list.add(c);

                        if (c instanceof PeriodicLogEvent) {
                            periodicEvents.add((PeriodicLogEvent) c);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while loading config for command: " + key, e);
            }
        }
        LOG.info("Total log events: " + events.size());

        try {
            smarthomeConnection = new SupradinConnection();
            smarthomeConnection.bindListener(new SmarthomeDataListener() {
                @Override
                public void messageReceived(SmarthomeConnection smarthomeConnection, SmarthomeMessage smarthomeMessage) {
                    final TimeBoundSmarthomeMessage m = new TimeBoundSmarthomeMessage(smarthomeMessage, System.currentTimeMillis());

                    if (events.containsKey(smarthomeMessage.getCommand())) {
                        List<LogEvent> c_list = events.get(smarthomeMessage.getCommand());
                        for (LogEvent c : c_list) {
                            if (c.approve(m)) {
                                List<TimeBoundSmarthomeMessage> messages = Collections.singletonList(m);
                                for (LoggerEventListener listener : loggerEventListeners) {
                                    listener.newEvents(messages);
                                }
                            }
                        }
                    }
                }
            });
            smarthomeConnection.open();

            //run periodical commands
            periodicalEventsScheduledExecutor = Executors.newScheduledThreadPool(periodicEvents.size());
            for (final PeriodicLogEvent periodicalEvent : periodicEvents) {
                periodicalEventsScheduledExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //стартуем триггер, если надо
                            periodicalEvent.trigger(smarthomeConnection);
                            //ждем пока триггер соберет ответы
                            Thread.sleep(periodicalEvent.getTriggerTimeout());

                            //теперь мы сделали все, что могли да и время полностью вышло -> отдаем в базу
                            List<TimeBoundSmarthomeMessage> messages = periodicalEvent.timeout();

                            for (LoggerEventListener listener : loggerEventListeners) {
                                listener.newEvents(messages);
                            }
                        } catch (InterruptedException ex) {
                            LOG.log(Level.SEVERE, "Interruption in periodical event", ex);
                        }
                    }
                }, periodicalEvent.getPeriod() - periodicalEvent.getTriggerTimeout(), periodicalEvent.getPeriod(), TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "init service error", e);
            shutdown();
        }
    }

    public void addMessageListener(LoggerEventListener listener) {
        loggerEventListeners.add(listener);
    }

    public void shutdown() {
        if (smarthomeConnection != null) {
            smarthomeConnection.close();
        }
        if (periodicalEventsScheduledExecutor != null) {
            periodicalEventsScheduledExecutor.shutdown();
        }
    }

}
