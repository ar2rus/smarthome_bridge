package com.gargon.smarthome.multicast.socket;

public interface MulticastSocketResponseFilter {

    /**
     * Фильтрация данных
     *
     * @param dataReceived пакет фильтруемых данных
     * @return true если данные прошли фильтрацию
     */
    boolean filter(byte[] dataReceived);

}
