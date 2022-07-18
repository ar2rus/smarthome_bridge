package com.gargon.smarthome.multicast.socket;

import java.net.InetAddress;

public interface MulticastSocketDataListener {

    /**
     * Получение новых данных
     *
     * @param ip   IP-адрес отправителя данных
     * @param port порт отправителя данных
     * @param data пакет данных
     */
    void dataReceived(InetAddress ip, int port, byte[] data);

    /**
     * Получение новых данных, включая свои сообщения
     *
     * @param ip   IP-адрес отправителя данных
     * @param port порт отправителя данных
     * @param data пакет данных
     */
    void dataSniffReceived(InetAddress ip, int port, byte[] data);

}
