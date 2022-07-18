package com.gargon.smarthome.supradin.socket;

/**
 * Интерфейс описывает слушателя данных сокета модуля Supradin
 */
public interface SupradinSocketDataListener {

    /**
     * Получение новых данных
     *
     * @param port порт отправителя (Supradin) данных
     * @param data пакет данных
     */
    void dataReceived(int port, byte[] data);

}
