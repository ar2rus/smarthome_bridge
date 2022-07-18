package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.socket.SupradinSocketResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**
 * Класс реализует абстрактный фильтр данных, получаемых с модуля Supradin.
 * Производится первоначальная проверка данных на соответствие формату SupradinDataMessage
 */
public abstract class SupradinConnectionResponseFilter implements SupradinSocketResponseFilter {

    /**
     * Абстрактный метод фильтрации данных формата SupradinDataMessage
     * по дополнительным параметрам
     *
     * @param receivedMessage полученное сообщение формата SupradinDataMessage
     * @return TRUE если сообщение пропущено фильтром
     */
    public abstract boolean filter(SupradinDataMessage receivedMessage);

    /**
     * Реализует фильтр интерфейса SupradinSocketResponseFilter,
     * в котором производится предпроверка на соответствие данных формату SupradinDataMessage
     *
     * @param dataReceived фильтруемый пакет данных
     * @return признак соответствия данных формату SupradinDataMessage
     */
    @Override
    public boolean filter(byte[] dataReceived) {
        if (dataReceived != null) {
            SupradinDataMessage s = new SupradinDataMessage(dataReceived);
            if (s.isValid()) {
                return filter(s);
            }
        }
        return false;
    }
}
