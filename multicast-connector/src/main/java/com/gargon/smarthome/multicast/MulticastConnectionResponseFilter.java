package com.gargon.smarthome.multicast;

import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.multicast.socket.MulticastSocketResponseFilter;

/**
 * Класс реализует абстрактный фильтр данных, получаемых из мультикаст группы.
 * Производится первоначальная проверка данных на соответствие формату MulticastDataMessage
 */
public abstract class MulticastConnectionResponseFilter implements MulticastSocketResponseFilter {

    /**
     * Абстрактный метод фильтрации данных формата MulticastDataMessage
     * по дополнительным параметрам
     *
     * @param multicastMessageReceived полученное сообщение формата MulticastDataMessage
     * @return TRUE если сообщение прошло фильтр
     */
    public abstract boolean filter(MulticastDataMessage multicastMessageReceived);

    /**
     * Реализует фильтр интерфейса MulticastSocketResponseFilter,
     * в котором производится предпроверка на соответствие данных формату MulticastDataMessage
     *
     * @param dataReceived фильтруемый пакет данных
     * @return признак соответствия данных формату MulticastDataMessage
     */
    @Override
    public boolean filter(byte[] dataReceived) {
        if (dataReceived != null) {
            MulticastDataMessage s = new MulticastDataMessage(dataReceived);
            if (s.isValid()) {
                return filter(s);
            }
        }
        return false;
    }
}
