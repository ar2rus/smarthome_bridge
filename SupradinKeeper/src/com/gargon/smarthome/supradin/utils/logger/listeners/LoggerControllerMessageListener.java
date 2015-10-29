package com.gargon.smarthome.supradin.utils.logger.listeners;

import com.gargon.smarthome.supradin.utils.logger.RealTimeSupradinDataMessage;
import java.util.List;

public interface LoggerControllerMessageListener {

    /**
     * Колбэк метод - отдающий новые сообщения для записи в БД
     *
     * @param messages список сообщений для записи в БД
     */
    public void messages(List<RealTimeSupradinDataMessage> messages);

}
