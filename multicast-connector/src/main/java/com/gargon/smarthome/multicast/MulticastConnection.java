package com.gargon.smarthome.multicast;


import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.multicast.socket.MulticastSocket;
import com.gargon.smarthome.multicast.socket.MulticastSocketDataListener;
import com.gargon.smarthome.multicast.socket.MulticastSocketResponseFilter;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.protocol.SmarthomeMessageFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс реализует API для взаимодействия с мульткаст-группой.
 */
public class MulticastConnection implements SmarthomeConnection {

    public static final String MULTICAST_IP = "234.5.6.7";
    public static final int MULTICAST_DATA_PORT = 12345;

    private String groupIp = MULTICAST_IP;
    private int dataPort = MULTICAST_DATA_PORT;

    private volatile boolean connected;

    private MulticastSocket socketWrapper;
    private MulticastSocketDataListener socketListener;

    private final Set<MulticastDataListener> dataListeners = new CopyOnWriteArraySet<>();
    private final Map<SmarthomeDataListener, MulticastDataListener> smarthomeDataListenerMap = Collections.synchronizedMap(new HashMap());

    /**
     * Создает объект класса с предустановленными параметрами подключения:
     * IP адрес мультикаст группы - "234.5.6.7".
     * Номер порта данных - 12345.
     */
    public MulticastConnection() {
    }

    /**
     * Создает объект класса с предустановленными параметрами подключения:
     * Номер порта данных - 12345.
     *
     * @param groupIp
     */
    public MulticastConnection(String groupIp) {
        this.groupIp = groupIp;
    }

    /**
     * Создает объект класса
     *
     * @param groupIp  IP адрес мультикаст группы
     * @param dataPort номер порта данных
     */
    public MulticastConnection(String groupIp, int dataPort) {
        this(groupIp);
        this.dataPort = dataPort;
    }

    /**
     * Проверяет статус подключения к мультикаст группе
     * Определяется только логикой вызова команд
     *
     * @return признак подключения к мультикаст группе
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Устанавливает соединение с мультикаст группой.
     *
     * @return признак успешного открытия соединения
     */
    public boolean open() {
        try {
            socketWrapper = new MulticastSocket(groupIp, dataPort);

            final MulticastConnection connection = this;
            socketWrapper.addDatagramDataListener(socketListener = new MulticastSocketDataListener() {

                @Override
                public void dataReceived(InetAddress ip, int port, byte[] data) {
                    if (port == dataPort) {
                        MulticastDataMessage message = new MulticastDataMessage(data);
                        if (message.isValid()) {
                            for (MulticastDataListener listener : dataListeners) {
                                listener.dataReceived(connection, ip, message);
                            }
                        }
                    }
                }

                @Override
                public void dataSniffReceived(InetAddress ip, int port, byte[] data) {
                    if (port == dataPort) {
                        MulticastDataMessage message = new MulticastDataMessage(data);
                        if (message.isValid()) {
                            for (MulticastDataListener listener : dataListeners) {
                                listener.dataSniffReceived(connection, ip, message);
                            }
                        }
                    }
                }
            });

            connected = true;
            return true;
        } catch (SocketException | UnknownHostException ex) {
            socketWrapper = null;
        } catch (IOException ex) {
            Logger.getLogger(MulticastConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Производит разрыв текущего активного подключения и закрывет соединение (сокет)
     */
    public boolean close() {
        if (socketWrapper != null) {
            connected = false;
            if (socketWrapper != null && socketListener != null) {
                socketWrapper.removeDatagramDataListener(socketListener);
            }
            socketWrapper.close();
            return true;
        }
        return false;
    }

    /**
     * Отправляет пакет данных на порт данных в мультикаст группу для ранее установленого активного подключения.
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

    /**
     * Отправляет пакет данных на порт данных мультикаст группы для ранее установленго активного подключения и
     * ожидает responseCount ответов сервера
     * соответствующих фильтру responseFilter в течение responseTimeout миллисекунд.
     *
     * @param responseCount   количество ожидаемых ответов сервера:
     *                        0 - только отправка сообщения;
     *                        -1 - для ожидания всех сообщений в течение responseTimeout;
     * @param responseTimeout время ожидания всех ответов в миллисекундах
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
                    r.add(convertToSmarthomeMessage(new MulticastDataMessage(response)));
                }
                return r;
            }
        }
        return null;
    }

    /**
     * Отправляет пакет данных на порт данных мультикаст группы для ранее установленго активного подключения и
     * ожидает ответа соответствующего фильтру responseFilter в течение responseTimeout миллисекунд.
     *
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
                return convertToSmarthomeMessage(new MulticastDataMessage(response));
            }
        }
        return null;
    }

    /**
     * Добавляет слушателя входящих сообщений данных мультикаст группы
     *
     * @param listener объект слушателя входящих сообщений мультикаст группы
     */
    public boolean addDataListener(MulticastDataListener listener) {
        return dataListeners.add(listener);
    }

    /**
     * Удаляет слушателя входящих сообщений данных мультикаст группы
     *
     * @param listener объект слушателя входящих сообщений мультикаст модуля
     */
    public boolean removeDataListener(MulticastDataListener listener) {
        return dataListeners.remove(listener);
    }

    @Override
    public boolean bindListener(final SmarthomeDataListener listener) {
        if (listener != null) {
            MulticastDataListener multicastDataListener;
            if (addDataListener(multicastDataListener = new MulticastDataListener() {

                @Override
                public void dataReceived(MulticastConnection connection, InetAddress ip, MulticastDataMessage receivedMessage) {
                    //listener.dataReceived(connection, convertToSmarthomeMessage(receivedMessage));
                }

                @Override
                public void dataSniffReceived(MulticastConnection connection, InetAddress ip, MulticastDataMessage receivedMessage) {
                    listener.dataReceived(connection, convertToSmarthomeMessage(receivedMessage));
                }
            })) {
                smarthomeDataListenerMap.put(listener, multicastDataListener);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unbindDataListener(SmarthomeDataListener smarthomeDataListener) {
        MulticastDataListener multicastDataListener = smarthomeDataListenerMap.get(smarthomeDataListener);
        return multicastDataListener != null && removeDataListener(multicastDataListener);
    }

    /**
     * IP адрес мультикаст группы
     *
     * @return текущий IP адрес мультикаст-группы
     */
    public String getGroupIp() {
        return groupIp;
    }


    /**
     * Номер порта данных соединения
     *
     * @return номер порта данных соединения
     */
    public int getDataPort() {
        return dataPort;
    }

    private MulticastSocketResponseFilter convertFilter(final SmarthomeMessageFilter f) {
        return new MulticastSocketResponseFilter() {
            @Override
            public boolean filter(byte[] dataReceived) {
                return f.filter(convertToSmarthomeMessage(new MulticastDataMessage(dataReceived)));
            }
        };
    }

    private static MulticastDataMessage createMessage(SmarthomeDevice device, SmarthomeCommand command, byte[] data) {
        return new MulticastDataMessage(device.getAddress(), 0xEE, command.getCode(), data);
    }

    private static SmarthomeMessage convertToSmarthomeMessage(MulticastDataMessage m) {
        return new SmarthomeMessage(m.getSrc(), m.getDst(), m.getCommand(), m.getData());
    }

}
