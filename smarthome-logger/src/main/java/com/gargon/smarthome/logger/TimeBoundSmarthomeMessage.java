package com.gargon.smarthome.logger;

import com.gargon.smarthome.protocol.SmarthomeMessage;

public class TimeBoundSmarthomeMessage extends SmarthomeMessage {

    /**
     * Класс переопределяет обычный SmarthomeMessage, добавляя в него дату и
     * время получения реального сообщения. Подобное решение вызвано лишь тем,
     * что перед непосредственной "укладкой" сообщений в БД возможна ситуация,
     * когда они первоначально "отлеживаются" в буфере (для PeriodCommand) и
     * лишь по истечению периода самая актуальная команда "кладется" в БД.
     * Понятно, что в избежании двусмысленных ситуаций при анализе данных из БД,
     * логично класть сообщения в БД со временем соответсвующем времени их
     * получения(наступления)
     *
     */
    
    private final long time;
    
    public TimeBoundSmarthomeMessage(SmarthomeMessage message, long time) {
        super(message.getSrc(), message.getDst(), message.getCommand(), message.getData());
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time + " " + super.toString();
    }

}
