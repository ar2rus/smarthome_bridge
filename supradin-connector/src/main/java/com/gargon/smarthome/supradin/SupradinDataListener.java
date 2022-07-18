package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**
 * Интерфейс описывает слушателя данных модуля Supradin
 */
public interface SupradinDataListener {

    /**
     * Получение новых данных
     *
     * @param connection      текущее соединение, например для немедленной отправки ответа
     * @param receivedMessage полученное сообщение
     */
    public void dataReceived(SupradinConnection connection, SupradinDataMessage receivedMessage);

}
