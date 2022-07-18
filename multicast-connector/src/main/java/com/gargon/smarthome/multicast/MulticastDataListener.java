package com.gargon.smarthome.multicast;

import com.gargon.smarthome.multicast.messages.MulticastDataMessage;

import java.net.InetAddress;

/**
 * Интерфейс описывает слушателя данных мультикаст группы
 *
 */
public interface MulticastDataListener {

    /**
     * Получение новых данных
     *
     * @param connection текущее соединение, например для немедленной отправки ответа
     * @param ip         IP-адрес отправителя
     * @param receivedMessage    пакет данных
     */
    void dataReceived(MulticastConnection connection, InetAddress ip, MulticastDataMessage receivedMessage);

    /**
     * Получение новых данных, включая свои сообщения
     *
     * @param connection      текущее соединение, например для немедленной отправки ответа
     * @param ip              IP-адрес отправителя
     * @param receivedMessage пакет данных
     */
    void dataSniffReceived(MulticastConnection connection, InetAddress ip, MulticastDataMessage receivedMessage);

}
