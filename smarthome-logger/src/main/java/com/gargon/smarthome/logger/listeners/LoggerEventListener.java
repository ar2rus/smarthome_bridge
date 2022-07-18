package com.gargon.smarthome.logger.listeners;

import com.gargon.smarthome.logger.TimeBoundSmarthomeMessage;

import java.util.List;

public interface LoggerEventListener {

    /**
     * Колбэк метод - отдающий новые события для записи в БД
     *
     * @param eventMessages список сообщений для записи в БД
     */
    void newEvents(List<TimeBoundSmarthomeMessage> eventMessages);

}
