package com.gargon.smarthome.supradin;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.protocol.SmarthomeMessageFilter;
import com.gargon.smarthome.supradin.messages.SupradinControlMessage;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.supradin.socket.SupradinSocket;
import com.gargon.smarthome.supradin.socket.SupradinSocketResponseFilter;
import com.gargon.smarthome.supradin.socket.SupradinSocketDataListener;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Класс реализует API для взаимодействия с модулем Supradin.
 * Порядок вызова методов для взаимодействия с модулем:
 * 1)open;
 * 2)connect, addDataListener;
 * 3)sendData / sendDataAndWaitResponse / sendDataAndWaitResponses;
 * 4)close.
 */
public class SupradinConnection implements SmarthomeConnection {

    /**
     * IP адрес Supradin модуля по умолчанию
     */
    public static final String SUPRADIN_IP = "192.168.1.10";

    /**
     * Номер порта управления Supradin по умолчанию
     */
    public static final int SUPRADIN_CONTROL_PORT = 1234;

    /**
     * Номер порта данных Supradin по умолчанию
     */
    public static final int SUPRADIN_DATA_PORT = 1235;

    private String host = SUPRADIN_IP;
    private int controlPort = SUPRADIN_CONTROL_PORT;
    private int dataPort = SUPRADIN_DATA_PORT;

    /**
     * Период отправки "пинг" сообщений для поддержания соединения, в секундах
     */
    private final static int SUPRADIN_CONNECTION_KEEPER_PERIOD = 10; //s

    /**
     * Время ожидания ответа на отправленное сообщение на порт управления, в миллисекундах
     */
    private final static int SUPRADIN_CONNECTION_CONTROL_RESPONSE_TIMEOUT = 2000; //ms

    private volatile boolean connected = false;
    private volatile boolean active = false;
    private char lastControlMessageId;

    private SupradinSocket socketWrapper;
    private SupradinSocketDataListener socketListener;
    private ScheduledExecutorService connectionKeeper;

    private final List<SupradinDataListener> dataListeners = new CopyOnWriteArrayList<>();
    private final Map<SmarthomeDataListener, SupradinDataListener> smarthomeDataListenerMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * Создает объект класса с предустановленными параметрами подключения:
     * IP адрес модуля Supradin - "192.168.1.10".
     * Номер порта управления модуля Supradin - 1234.
     * Номер порта данных модуля Supradin - 1235.
     */
    public SupradinConnection() {
    }

    /**
     * Создает объект класса с предустановленными параметрами подключения:
     * Номер порта управления модуля Supradin - 1234.
     * Номер порта данных модуля Supradin - 1235.
     *
     * @param host IP адрес модуля Supradin
     */
    public SupradinConnection(String host) {
        this.host = host;
    }

    /**
     * Создает объект класса
     *
     * @param host        IP адрес модуля Supradin
     * @param controlPort номер порта управления модуля Supradin
     * @param dataPort    номер порта данных модуля Supradin
     */
    public SupradinConnection(String host, int controlPort, int dataPort) {
        this(host);
        this.controlPort = controlPort;
        this.dataPort = dataPort;
    }

    /**
     * Проверяет статус подключения к модулю Supradin
     * Определяется только логикой вызова команд
     *
     * @return признак подключения к модулю Supradin
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Проверяет состояние подключения к модулю Supradin
     * Определяется на основании ответов на команду ping
     * Рекомендуется использовать для проверки соединения
     *
     * @return признак состояния подключения к модулю Supradin
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Открывает соединение с модулем Supradin.
     * Для закрытия текущего соединения следует вызывать  {@link SupradinConnection#close()}.
     *
     * @return признак успешного открытия соединения
     */
    public boolean open() {
        try {
            socketWrapper = new SupradinSocket(host);
            return connect();
        } catch (SocketException | UnknownHostException ex) {
            socketWrapper = null;
        }
        return false;
    }

    /**
     * Производит разрыв текущего активного подключения
     * и закрывет соединение (сокет). Дальнейшее повторное открытие сокета методом {@link SupradinConnection#open()} невозможно.
     * Предварительный вызов метода  {@link SupradinConnection#disconnect()} делать не нужно.
     */
    public boolean close() {
        boolean r = false;
        if (socketWrapper != null) {
            r = disconnect();
            socketWrapper.close();
        }
        return r;
    }

    /**
     * Отправляет команду на управляющий порт модуля Supradin и получает ответ на нее.
     *
     * @param command код команды
     * @return ответ сервера на отправленную команду или null если ответ получен не был
     */
    private synchronized byte[] control(byte command) {
        if (socketWrapper != null) {
            ++lastControlMessageId;
            final byte[] dataToSend = new byte[]{(byte) (lastControlMessageId >> 8 & 0xFF), (byte) lastControlMessageId, command};
            return socketWrapper.sendAndWaitResponse(controlPort,
                    dataToSend,
                    new SupradinSocketResponseFilter() {
                        @Override
                        public boolean filter(byte[] dataReceived) {
                            char id = SupradinControlMessage.getCommandId(dataToSend);
                            return id > 0 && id == SupradinControlMessage.getCommandId(dataReceived);
                        }
                    },
                    SUPRADIN_CONNECTION_CONTROL_RESPONSE_TIMEOUT);
        }
        return null;
    }

    /**
     * Отправляет команду на подключение к модулю Supradin
     * Дополнительно добавляется слушатель сообщений соединения
     * и запускается дополнительный поток периодически отправляющий {@link SupradinConnection#ping()}
     * серверу и, тем самым, поддерживающим соединение в активном состоянии
     * (в том числе открывающий соединение, если его не удалось установить командой COMMAND_CONNECT).
     *
     * @return признак, если соединение удалось установить сразу. Так или иначе,
     * даже если получен false, попытки установить соединение будут осуществляться запущенным
     * connectionKeeper'ом вызовом ping()
     */
    private boolean connect() {
        if (!connected) {
            byte[] response = control(SupradinControlMessage.CONTROL_COMMAND_CONNECT);

            final SupradinConnection connection = this;
            socketWrapper.addDatagramDataListener(socketListener = new SupradinSocketDataListener() {

                @Override
                public void dataReceived(int port, byte[] data) {
                    if (port == dataPort) {
                        SupradinDataMessage supradin = new SupradinDataMessage(data);
                        if (supradin.isValid()) {
                            for (SupradinDataListener listener : dataListeners) {
                                listener.dataReceived(connection, supradin);
                            }
                        }
                    }
                }
            });

            connectionKeeper = Executors.newSingleThreadScheduledExecutor();
            connectionKeeper.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    active = ping();
                }
            }, 0, SUPRADIN_CONNECTION_KEEPER_PERIOD, TimeUnit.SECONDS);
            //}

            connected = true;
            return SupradinControlMessage.isConnectionActive(response);
        }
        return false;
    }

    /**
     * Отправляет команду ping модулю Supradin,
     * которая служит для поддержания подключения активным или восстановления ранее разорванного подключения.
     * В самостоятельном вызове данного метода нет необходимости
     *
     * @return признак активности соединения
     */
    protected boolean ping() {
        if (connected) {
            return SupradinControlMessage.isConnectionActive(control(SupradinControlMessage.CONTROL_COMMAND_PING));
        }
        return false;
    }

    /**
     * Отправляет команду на разрыв текущего подключения с модулем Supradin.
     * Дальнейшее повторное и далее подключение возможно методом {@link SupradinConnection#connect()}.
     *
     * @return признак успешного разрыва активного подключения
     */
    private boolean disconnect() {
        if (connected) {
            if (connectionKeeper != null) {
                connectionKeeper.shutdown();
            }
            if (socketWrapper != null && socketListener != null) {
                socketWrapper.removeDatagramDataListener(socketListener);
            }
            connected = false;
            return SupradinControlMessage.isConnectionActive(control(SupradinControlMessage.CONTROL_COMMAND_DISCONNECT));
        }
        return false;
    }

    /**
     * Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения.
     *
     * @return признак успешной отправки сообщения
     */
    @Override
    public boolean sendData(SmarthomeDevice device, SmarthomeCommand command, byte[] data) {
        if (socketWrapper != null && connected) {
            return socketWrapper.send(dataPort, createMessage(device, command, data).toByteArray());
        }
        return false;
    }

    public boolean sendData(int ip, int dst, int src, int command, byte[] data) {
        if (socketWrapper != null && connected) {
            return socketWrapper.send(dataPort, new SupradinDataMessage(ip, dst, src, command, data).toByteArray());
        }
        return false;
    }

    /**
     * Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения и
     * ожидает responseCount ответов сервера
     * соответствующих фильтру responseMessageFilter в течение responseTimeout миллисекунд.
     *
     * @param responseMessageFilter фильтр входящих сообщений
     * @param responseCount         количество ожидаемых ответов сервера:
     *                              0 - только отправка сообщения;
     *                              -1 - для ожидания всех сообщений в течение responseTimeout;
     * @param responseTimeout       время ожидания всех ответов в миллисекундах
     * @return список всех полученных за время responseTimeout ответов
     */
    @Override
    public List<SmarthomeMessage> sendDataAndWaitResponses(SmarthomeDevice device, SmarthomeCommand command, byte[] data,
                                                           SmarthomeMessageFilter responseMessageFilter, int responseCount, int responseTimeout) {
        if (socketWrapper != null && connected) {
            List<byte[]> responses = socketWrapper.sendAndWaitResponses(dataPort, createMessage(device, command, data).toByteArray(),
                    convertFilter(responseMessageFilter), responseCount, responseTimeout);
            if (responses != null) {
                List<SmarthomeMessage> r = new ArrayList<>();
                for (byte[] response : responses) {
                    r.add(convertToSmarthomeMessage(new SupradinDataMessage(response)));
                }
                return r;
            }
        }
        return null;
    }

    /**
     * Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения и
     * ожидает ответа сервера соответствующего фильтру responseMessageFilter в течение responseTimeout миллисекунд.
     *
     * @param responseMessageFilter  фильтр входящих сообщений
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return ответ сервера или null если ответ не был получен
     */
    @Override
    public SmarthomeMessage sendDataAndWaitResponse(SmarthomeDevice device, SmarthomeCommand command, byte[] data,
                                                       SmarthomeMessageFilter responseMessageFilter, int responseTimeout) {
        if (socketWrapper != null && connected) {
            byte[] response = socketWrapper.sendAndWaitResponse(dataPort, createMessage(device, command, data).toByteArray(),
                    convertFilter(responseMessageFilter), responseTimeout);
            if (response != null) {
                return convertToSmarthomeMessage(new SupradinDataMessage(response));
            }
        }
        return null;
    }

    /**
     * Добавляет слушателя входящих сообщений Supradin модуля
     *
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public boolean addDataListener(SupradinDataListener listener) {
        return dataListeners.add(listener);
    }

    /**
     * Удаляет слушателя входящих сообщений Supradin модуля
     *
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public boolean removeDataListener(SupradinDataListener listener) {
        return dataListeners.remove(listener);
    }

    @Override
    public boolean bindListener(final SmarthomeDataListener listener) {
        if (listener != null) {
            SupradinDataListener supradinDataListener;
            if (addDataListener(supradinDataListener = new SupradinDataListener() {
                @Override
                public void dataReceived(SupradinConnection connection, SupradinDataMessage receivedMessage) {
                    listener.dataReceived(connection, convertToSmarthomeMessage(receivedMessage));
                }
            })) {
                smarthomeDataListenerMap.put(listener, supradinDataListener);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unbindDataListener(SmarthomeDataListener smarthomeDataListener) {
        SupradinDataListener supradinDataListener = smarthomeDataListenerMap.get(smarthomeDataListener);
        return supradinDataListener != null && removeDataListener(supradinDataListener);
    }

    /**
     * IP адрес соединения с модулем Supradin
     *
     * @return текущий IP адрес соединения с модулем Supradin
     */
    public String getHost() {
        return host;
    }

    /**
     * Номер порта управления соединения с модулем Supradin
     *
     * @return номер порта управления соединения с модулем Supradin
     */
    public int getControlPort() {
        return controlPort;
    }

    /**
     * Номер порта данных соединения с модулем Supradin
     *
     * @return номер порта данных соединения с модулем Supradin
     */
    public int getDataPort() {
        return dataPort;
    }

    private SupradinSocketResponseFilter convertFilter(final SmarthomeMessageFilter f) {
        return new SupradinSocketResponseFilter() {
            @Override
            public boolean filter(byte[] dataReceived) {
                return f.filter(convertToSmarthomeMessage(new SupradinDataMessage(dataReceived)));
            }
        };
    }

    private static SupradinDataMessage createMessage(SmarthomeDevice device, SmarthomeCommand command, byte[] data){
        return new SupradinDataMessage(device.getAddress(), 0, command.getCode(), data);
    }

    private static SmarthomeMessage convertToSmarthomeMessage(SupradinDataMessage m){
        return new SmarthomeMessage(m.getSrc(), m.getDst(), m.getCommand(), m.getData());
    }

}
