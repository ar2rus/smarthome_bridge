package com.gargon.smarthome.protocol;

/**
 * Слушатель входящих данных
 */
public interface SmarthomeDataListener {

    /**
     * Получение новых данных
     *
     * @param connection текущее соединение, например для немедленной отправки ответа
     * @param m          полученное сообщение
     */
    void messageReceived(SmarthomeConnection connection, SmarthomeMessage m);

    default void dataReceived(SmarthomeConnection connection, SmarthomeMessage m) {
        if (getFilter() == null || getFilter().filter(m)) {
            messageReceived(connection, m);
        }
    }

    default SmarthomeMessageFilter getFilter() {
        return null;
    }

}
