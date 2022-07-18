package com.gargon.smarthome.model.enums;

import com.gargon.smarthome.model.commands.Command;
import com.gargon.smarthome.model.commands.ControlCommand;
import com.gargon.smarthome.model.commands.InfoCommand;
import com.gargon.smarthome.model.commands.ServiceCommand;
import com.gargon.smarthome.model.commands.control.*;
import com.gargon.smarthome.model.commands.info.*;
import com.gargon.smarthome.model.commands.service.DebugCommand;
import com.gargon.smarthome.model.commands.service.DiscoveryCommand;
import com.gargon.smarthome.model.commands.service.PingCommand;

/**
 * Команды и их коды
 */
public enum SmarthomeCommand {

    /* Ответ устройств на поиск, в качестве параметра - название устройства (текст) */
    DISCOVERY_RESPONSE(0x01, DiscoveryResponseCommand.class),

    /* Поиск других устройств, параметров нет */
    DISCOVERY(0x00, DiscoveryCommand.class, DISCOVERY_RESPONSE),

    /* Работа с загрузчиком. Данные - субкоманда.
     <-0 - загрузчик запущен
     ->1 - перейти в режим обновления прошивки
     <-2 - подтверждение перехода, плюс два байта - размер страницы
     ->3 запись прошивки, 4 байта - адрес, всё остальное - данные (равные размеру страницы)
     <-4 блок прошивки записан
     ->5 выход из режима прошивки */
    BOOT_CONTROL(0x02, BootControlCommand.class),

    /* Перезагружает устройство в загрузчик. */
    REBOOT(0x03, RebootCommand.class),

    /* Посылается устройством после инициализации библиотеки, сообщает об успешной загрузке устройства.
     Параметр - содержимое MCU регистра, говорящее о причине перезагрузки. */
    BOOT_COMPLETED(0x04, BootCompletedCommand.class),

    /*Сообщение о текущем времени*/
    TIME_INFO(0x09, TimeInfoCommand.class),

    /*Запрос текущего времени*/
    TIME(0x08, DefaultControlCommand.class, TIME_INFO),

    /* Ответ на пинг, в данных то, что было прислано в предыдущей команде */
    PING_REPLY(0xFF, PingReplyCommand.class),

    /* Пинг, на эту команду устройство должно ответить следующей командой, возвратив весь буфер */
    PING(0xFE, PingCommand.class, PING_REPLY),

    /* Сообщение о номере текущего канала*/
    CHANNEL_INFO(0x11, ChannelInfoCommand.class),

    /* Команда переключения канала*/
    CHANNEL(0x10, ChannelCommand.class, CHANNEL_INFO),

    /* Сообщение о состоянии громкости устройства*/
    VOLUME_INFO(0x16, VolumeInfoCommand.class),

    /* Команда управления громкостью устройства*/
    VOLUME(0x15, VolumeCommand.class, VOLUME_INFO),

    /* Команда отключения звука устройства*/
    MUTE(0x17, MuteCommand.class, VOLUME_INFO),

    /* Сообщение о состоянии эквалайзера*/
    EQUALIZER_INFO(0x19, EqualizerInfoCommand.class),

    /* Команда управления эквалайзером*/
    EQUALIZER(0x18, EqualizerCommand.class, EQUALIZER_INFO),

    /* Сообщение о состоянии FM-приемника*/
    FM_INFO(0x1D, FMInfoCommand.class),

    /* Команда управления FM-приемником*/
    FM(0x1C, FMCommand.class, FM_INFO),

    /* Команда запроса состояния (вкл/выкл) устройства*/
    POWER_INFO(0x1F, PowerInfoCommand.class),

    /* Команда управления включением/выключением устройства*/
    POWER(0x1E, PowerCommand.class, POWER_INFO),

    /* Сообщение о состоянии всех выключателей устройства в виде битовой маски*/
    SWITCH_INFO(0x21, SwitchInfoCommand.class),

    /* Команда управления выключателями/реле*/
    SWITCH(0x20, SwitchCommand.class, SWITCH_INFO),

    /* Сообщает о состоянии нефиксируемой кнопки*/
    BUTTON_INFO(0x23, ButtonInfoCommand.class),

    /* Команда запроса состояния нефиксируемых кнопок*/
    BUTTON(0x22, DefaultControlCommand.class, BUTTON_INFO),

    /* Сообщает код набранный с помощью диска телефона*/
    ROTARY_DIAL_NUMBER_INFO(0x24, RotaryDialNumberInfoCommand.class),

    /* Сообщение о температуре*/
    TEMPERATURE_INFO(0x26, TemperatureInfoCommand.class),

    /* Команда запроса текущей температуры*/
    TEMPERATURE(0x25, DefaultControlCommand.class, TEMPERATURE_INFO),

    /* Сообщает об уровне влажности*/
    HUMIDITY_INFO(0x28, HumidityInfoCommand.class),

    /* Команда запроса текущей влажности*/
    HUMIDITY(0x27, DefaultControlCommand.class, HUMIDITY_INFO),

    /* Сообщает об уровне атмосферного давления*/
    PRESSURE_INFO(0x2A, PressureInfoCommand.class),

    /* Команда запроса текущего атмосферного давления*/
    PRESSURE(0x29, DefaultControlCommand.class, PRESSURE_INFO),

    /* Сообщает метеоданные*/
    METEO_INFO(0x2F, MeteoInfoCommand.class),

    /* Команда запроса метеоданных*/
    METEO(0x2E, DefaultControlCommand.class, METEO_INFO),

    /* Сообщает о найденном 1-wire устройстве*/
    ONEWIRE_INFO(0x31, OneWireInfoCommand.class),

    /* Команда поиска 1-wire устройств*/
    ONEWIRE_SEARCH(0x30, DefaultControlCommand.class, ONEWIRE_INFO),

    /* Сообщает об уровне напряжения*/
    VOLTAGE_INFO(0x33, VoltageInfoCommand.class),

    /* Команда запроса напряжения*/
    VOLTAGE(0x32, DefaultControlCommand.class, VOLUME_INFO),

    /* Сообщает о наличии движения в помещении*/
    MOTION_INFO(0x41, MotionInfoCommand.class),

    /* Сообщает об уровне  освещенности*/
    LIGHT_LEVEL_INFO(0x46, LightLevelInfoCommand.class),

    /* Команда запроса уровня освещенности*/
    LIGHT_LEVEL(0x45, DefaultControlCommand.class, LIGHT_LEVEL_INFO),

    /* Сообщает о текущем состоянии вентилятора */
    FAN_INFO(0x51, FanInfoCommand.class),

    /* Команда управления вентилятором */
    FAN(0x50, FanCommand.class, FAN_INFO),

    /* Сообщает о текущем состоянии процесса зарядки */
    CHARGE_INFO(0x53, ChargeInfoCommand.class),

    /* Команда управления подзарядкой устройств */
    CHARGE(0x52, ChargeCommand.class, CHARGE_INFO),

    /* Сообщает о текущем состоянии дверей */
    DOORS_INFO(0x56, DoorsInfoCommand.class),

    /* Команда запроса состояния дверей*/
    DOORS(0x55, DefaultControlCommand.class, DOORS_INFO),

    /* Сообщает о текущем состоянии диммера */
    DIMMER_INFO(0x58, DimmerInfoCommand.class),

    /* Команда упрвления диммером*/
    DIMMER(0x57, DimmerCommand.class, DIMMER_INFO),

    /* Сообщает о состоянии теплого пола */
    HEATFLOOR_INFO(0x61, HeatfloorInfoCommand.class),

    /* Команда управления теплым полом */
    HEATFLOOR(0x60, HeatfloorCommand.class, HEATFLOOR_INFO),

    /* Сообщает о состоянии сервопривода */
    SERVO_INFO(0x67, ServoInfoCommand.class),

    /* Команда управления сервоприводом */
    SERVO(0x66, ServoCommand.class, SERVO_INFO),

    /* Сообщает о состоянии произвольного устройства */
    DEVICE_STATE_INFO(0x71, DeviceStateInfoCommand.class),

    /* Команда управления состоянием произвольного устройства */
    DEVICE_STATE(0x70, DeviceStateCommand.class, DEVICE_STATE_INFO),

    /* Сообщает о нажатии кнопки на ПДУ */
    RC_BUTTON_PRESSED(0x75, RCButtonPressedCommand.class),

    /* Эмулирует нажтие кнопки на ПДУ */
    //RC_BUTTON_SEND(0x76),

    /* Команда блокирования*/
    ANDROID(0xA0, AndroidCommand.class),

    /* Подать звуковой сигнал*/
    BEEP(0x90, BeepCommand.class),

    /* Команда отладки*/
    DEBUG(0x99, DebugCommand.class);


    private int code;
    private Class<? extends Command> commandClass;

    private SmarthomeCommand responseCommand;


    SmarthomeCommand(int code, Class<? extends Command> commandClass, SmarthomeCommand responseCommand) {
        this.code = code;
        this.commandClass = commandClass;
        this.responseCommand = responseCommand;
    }

    SmarthomeCommand(int code, Class<? extends Command> commandClass) {
        this(code, commandClass, null);
    }

    public boolean isControlCommand() {
        return commandClass.isAssignableFrom(ControlCommand.class);
    }

    public boolean isInfoCommand() {
        return commandClass.isAssignableFrom(InfoCommand.class);
    }

    public boolean isServiceCommand() {
        return commandClass.isAssignableFrom(ServiceCommand.class);
    }

    public int getCode() {
        return code;
    }

    public SmarthomeCommand getResponseCommand() {
        return responseCommand;
    }

    public static SmarthomeCommand getByCode(int code) {
        for (SmarthomeCommand c : values()) {
            if (code == c.code) {
                return c;
            }
        }
        return null;
    }
}
