package com.gargon.smarthome;

import com.gargon.smarthome.model.commands.control.ChannelCommand;
import com.gargon.smarthome.model.commands.control.SwitchCommand;
import com.gargon.smarthome.model.commands.info.ChannelInfoCommand;
import com.gargon.smarthome.model.commands.info.SwitchInfoCommand;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.protocol.SmarthomeMessageFilter;
import com.gargon.smarthome.model.entities.heatfloor.enums.HeatfloorMode;
import com.gargon.smarthome.model.entities.heatfloor.HeatFloorDictionary;
import com.gargon.smarthome.model.entities.heatfloor.HeatfloorProgram;

import com.gargon.smarthome.model.entities.fm.FMDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SmarthomeActionService {

    public static final int EQUALIZER_GAIN = 1;
    public static final int EQUALIZER_TREBLE = 2;
    public static final int EQUALIZER_BASS = 3;


    public static final int ROOM_AUDIOSOURCE_PC = 1;
    //public static final int ROOM_AUDIOSOURCE_PC = 2;
    public static final int ROOM_AUDIOSOURCE_BLUETOOTH = 3;
    public static final int ROOM_AUDIOSOURCE_FM = 2;

    public static final int BATHROOM_AUDIOSOURCE_PAD = 1;
    public static final int BATHROOM_AUDIOSOURCE_FM = 4;


    public static final int RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID = 1;
//    public static final int RELAY_1_FLOOR_KITCHEN_SWITCH_ID = 2;
//    public static final int RELAY_1_FLOOR_BATHROOM_SWITCH_ID = 3;
//
//    //public static final int RELAY_2_SWITCH_1_ID = 1;
//    public static final int RELAY_2_FAN_SWITCH_ID = 2;
    public static final int RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID = 3;
//
    public static final int KITCHEN_FAN_SWITCH_ID = 1;
//    public static final int KITCHEN_LIGHT_SWITCH_ID = 1;
//
//    public static final int SOCKET_DIMMER_SWITCH_ID = 1;
//
      private static final int RELAY_1_WAITING_RESPONSE_TIMEOUT = 500; //ms
      private static final int RELAY_2_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int AUDIOBOX_WAITING_RESPONSE_TIMEOUT = 500; //ms
//    private static final int AUDIOBATH_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int KITCHEN_WAITING_RESPONSE_TIMEOUT = 500; //ms
//    private static final int KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT = 500; //ms
//    private static final int SOCKET_DIMMER_WAITING_RESPONSE_TIMEOUT = 50; //ms
//
    private static final int AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT = 500; //ms
    private static final int AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT = 500; //ms
    private static final int FM_EEPROM_WAITING_RESPONSE_TIMEOUT = 500; //ms

    private static final int HEATFLOOR_COMMAND_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int HEATFLOOR_EEPROM_WAITING_RESPONSE_TIMEOUT = 500; //ms
//
//
    private static final int NUM_ATTEMPTS_COMMAND = 5;
    private static final int NUM_ATTEMPTS_STATE = 2;
//

    /**
     * Переключает свет в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleLightInCloackroom(SmarthomeConnection connection) {
        return connection.sendControlResponsible(
                SmarthomeDevice.RELAY_1, SmarthomeCommand.SWITCH, SwitchCommand.toggle(RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID),
                new Predicate<byte[]>() {
                    @Override
                    public boolean test(byte[] data) {
                        return SwitchInfoCommand.info(data) != null;
                    }
                },
                RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }


    /**
     * Включает свет в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchOnLightInCloackroom(SmarthomeConnection connection) {
        return connection.sendControlResponsible(
                SmarthomeDevice.RELAY_1, SmarthomeCommand.SWITCH, SwitchCommand.on(RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID),
                data -> SwitchInfoCommand.info(data).contains(RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID),
                RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    /**
     * Отключает свет в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchOffLightInCloackroom(SmarthomeConnection connection) {
        return connection.sendControlResponsible(
                SmarthomeDevice.RELAY_1, SmarthomeCommand.SWITCH, SwitchCommand.off(RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID),
                data -> !SwitchInfoCommand.info(data).contains(RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID),
                RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

//    /**
//     * Определяет включен ли свет в гардеробной по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true -  если свет в гардеробной включен;
//     * false - если свет в гардеробной выключен;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Boolean checkLightInCloackroomSwitchedOn(SmarthomeMessage message) {
//        if (message != null) {
//            return SmarthomeMessageFilter.filter(SmarthomeDevice.RELAY_1, SmarthomeCommand.SWITCH_INFO, 1)
//                    .filter(message);
//
//            if (message.getSrc() == SmarthomeDevice.RELAY_1
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет включен ли свет на кухне по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true -  если свет на кухне включен (также и любое состояние диммера);
//     * false - если свет на кухне выключен;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Boolean checkLightInKitchenSwitchedOn(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.KITCHEN_LIGHT
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (KITCHEN_LIGHT_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет включен ли свет в комнате по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true -  если ночник включен (также и любое состояние диммера);
//     * false - если ночник выключен;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Boolean checkLightInSocketSwitchedOn(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.SOCKET_DIMMER
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (SOCKET_DIMMER_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Проверяет включен ли свет в гардеробной.
//     * Отправляет команду на проверку состояния выключателей и
//     * в случае успешного ее выполнения производит анализ состояния
//     * выключателя света в гардеробной
//     *
//     * @param connection текущее соединение
//     * @return возвращает true -  если свет в гардеробной включен;
//     * false - если свет в гардеробной выключен;
//     * null - не удалось определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Boolean isLightInCloackroomSwitchedOn(SmarthomeConnection connection) {
//        return checkLightInCloackroomSwitchedOn(Smarthome.sendResponsible(connection,
//                SmarthomeDevice.RELAY_1,
//                Priority.COMMAND,
//                SmarthomeCommand.SWITCH,
//                new byte[]{(byte) 0xFF},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                && receivedMessage.getData().length == 1;
//                    }
//                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
//    }
//
//    /**
//     * Управляет включением теплого пола на кухне
//     *
//     * @param connection текущее соединение
//     * @param on         признак включить/выключить
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean switchHeatingFloorInKitchen(SmarthomeConnection connection, final boolean on) {
//        return Smarthome.sendResponsible(connection,
//                SmarthomeDevice.RELAY_1,
//                Priority.COMMAND,
//                SmarthomeCommand.SWITCH,
//                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_KITCHEN_SWITCH_ID},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.RELAY_1
//                                && receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                && receivedMessage.getData().length == 1
//                                && ((receivedMessage.getData()[0] >> (RELAY_1_FLOOR_KITCHEN_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
//                    }
//                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
//    }
//
//    /**
//     * Управляет включением теплого пола в ванной комнате
//     *
//     * @param connection текущее соединение
//     * @param on         признак включить/выключить
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean switchHeatingFloorInBathroom(SmarthomeConnection connection, final boolean on) {
//        return Smarthome.sendResponsible(connection,
//                SmarthomeDevice.RELAY_1,
//                Priority.COMMAND,
//                SmarthomeCommand.SWITCH,
//                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_BATHROOM_SWITCH_ID},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.RELAY_1
//                                && receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                && receivedMessage.getData().length == 1
//                                && ((receivedMessage.getData()[0] >> (RELAY_1_FLOOR_BATHROOM_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
//                    }
//                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
//    }


    /**
     * Выбирает режим управления работой вентилятора в ванной
     * (автоматический/ручной)
     *
     * @param connection текущее соединение
     * @param auto_mode
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectFanModeInBathroom(SmarthomeConnection connection, boolean auto_mode) {
        final byte mode = (byte) (auto_mode ? 1 : 0);
        return connection.sendResponsible(
                SmarthomeDevice.RELAY_2,
                SmarthomeCommand.FAN,
                new byte[]{mode},

                receivedMessage -> {
                    return receivedMessage.getSrc() == SmarthomeDevice.RELAY_2
                            && receivedMessage.getCommand() == SmarthomeCommand.FAN_INFO
                            && receivedMessage.getData().length == 9
                            && receivedMessage.getData()[0] == mode;
                },
                RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    /**
     * Переключает вентилятор в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleFanInBathroom(SmarthomeConnection connection) {
        return connection.sendResponsible(
                SmarthomeDevice.RELAY_2,
                SmarthomeCommand.FAN,
                new byte[]{(byte) 2},
                SmarthomeMessageFilter.scl(SmarthomeDevice.RELAY_2, SmarthomeCommand.FAN_INFO, 9),
                RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

//    /**
//     * Переключает вентилятор на кухне
//     *
//     * @param connection текущее соединение
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean toggleFanInKitchen(SmarthomeConnection connection) {
//        return Smarthome.sendResponsible(connection,
//                SmarthomeDevice.KITCHEN,
//                Priority.COMMAND,
//                SmarthomeCommand.SWITCH,
//                new byte[]{(byte) 2, KITCHEN_FAN_SWITCH_ID},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.KITCHEN
//                                && receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                && receivedMessage.getData().length == 1;
//                    }
//                }, KITCHEN_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
//    }

    /**
     * Управляет включением вентилятора на кухне
     *
     * @param connection текущее соединение
     * @param on         признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchFanInKitchen(SmarthomeConnection connection, final boolean on) {
        return connection.sendResponsible(
                SmarthomeDevice.KITCHEN,
                SmarthomeCommand.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), KITCHEN_FAN_SWITCH_ID},

                receivedMessage -> {
                    return receivedMessage.getSrc() == SmarthomeDevice.KITCHEN
                            && receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
                            && receivedMessage.getData().length == 1
                            && ((receivedMessage.getData()[0] >> (KITCHEN_FAN_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                },
                KITCHEN_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

//    /**
//     * Определяет включен ли вентилятор на кухне по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true - если вентилятор на кухне включен; false - если
//     * вентилятор в ванной выключен; null - если передано неверное по формату
//     * сообщение для анализа
//     */
//    public static Boolean checkFanInKitchenSwitchedOn(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.KITCHEN
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (KITCHEN_FAN_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//
//    /**
//     * Определяет включен ли вентилятор в ванной по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true - если вентилятор в ванной включен; false - если
//     * вентилятор в ванной выключен; null - если передано неверное по формату
//     * сообщение для анализа
//     */
//    public static Boolean checkFanInBathroomSwitchedOn(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.RELAY_2
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (RELAY_2_FAN_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Проверяет включен вентилятор в ванной. Отправляет команду на проверку
//     * состояния выключателей и в случае успешного ее выполнения производит
//     * анализ состояния выключателя вентилятора в ванной
//     *
//     * @param connection текущее соединение
//     * @return возвращает true - если вентилятор в ванной комнате включен; false
//     * - если вентилятор в ванной комнате выключен; null - не удалось определить
//     * (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Boolean isFanInBathroomSwitchedOn(SmarthomeConnection connection) {
//        return checkFanInBathroomSwitchedOn(
//                Smarthome.sendResponsible(connection,
//                        SmarthomeDevice.RELAY_2,
//                        Priority.COMMAND,
//                        SmarthomeCommand.SWITCH,
//                        new byte[]{(byte) 0xFF},
//                        new SupradinConnectionResponseFilter() {
//
//                            @Override
//                            public boolean filter(SupradinDataMessage receivedMessage) {
//                                return receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                        && receivedMessage.getData().length == 1;
//                            }
//                        }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
//    }
//
//
    /**
     * Переключает свет в зеркальном шкафу в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleLightInBathroom(SmarthomeConnection connection) {
        return connection.sendResponsible(
                SmarthomeDevice.RELAY_2,
                SmarthomeCommand.SWITCH,
                new byte[]{(byte) 2, RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},
                SmarthomeMessageFilter.scl(SmarthomeDevice.RELAY_2, SmarthomeCommand.SWITCH_INFO, 1),
                RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    /**
     * Управляет включением света в зеркальном шкафу в ванной
     *
     * @param connection текущее соединение
     * @param on         признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchLightInBathroom(SmarthomeConnection connection, final boolean on) {
        return connection.sendResponsible(
                SmarthomeDevice.RELAY_2,
                SmarthomeCommand.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},

                receivedMessage -> {
                    return receivedMessage.getSrc() == SmarthomeDevice.RELAY_2
                            && receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
                            && receivedMessage.getData().length == 1
                            && ((receivedMessage.getData()[0] >> (RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                },
                RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

//
//    /**
//     * Определяет включен ли свет в зеркальном шкафу в ванной комнате
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает true - если свет включен; false - если свет выключен;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Boolean checkLightInBathroomSwitchedOn(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.RELAY_2
//                    && message.getCommand() == SmarthomeCommand.SWITCH_INFO
//                    && message.getData().length == 1) {
//                return ((message.getData()[0] >> (RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID - 1)) & 1) == 1;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Проверяет включен ли свет в зеркальном шкафу в ванной комнате Отправляет
//     * команду на проверку состояния выключателей и в случае успешного ее
//     * выполнения производит анализ состояния выключателя света в зеркальном
//     * шкафу в ванной
//     *
//     * @param connection текущее соединение
//     * @return возвращает true - если свет включен; false - если свет выключен;
//     * null - не удалось определить (возникла ошибка при отправке или ответ не
//     * получен)
//     */
//    public static Boolean isLightInBathroomSwitchedOn(SmarthomeConnection connection) {
//        return checkFanInBathroomSwitchedOn(
//                Smarthome.sendResponsible(connection,
//                        SmarthomeDevice.RELAY_2,
//                        Priority.COMMAND,
//                        SmarthomeCommand.SWITCH,
//                        new byte[]{(byte) 0xFF},
//                        new SupradinConnectionResponseFilter() {
//
//                            @Override
//                            public boolean filter(SupradinDataMessage receivedMessage) {
//                                return receivedMessage.getCommand() == SmarthomeCommand.SWITCH_INFO
//                                        && receivedMessage.getData().length == 1;
//                            }
//                        }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
//    }


    /**
     * Управляет выбором источника аудиосигнла
     *
     * @param connection текущее соединение
     * @param source     идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    private static boolean selectSourceOfSound(SmarthomeConnection connection, final SmarthomeDevice device, final int source) {
        return connection.sendControlResponsible(
                device,  SmarthomeCommand.CHANNEL, ChannelCommand.selectChannel(source),
                data -> ChannelInfoCommand.info(data) == source,
                AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    /**
     * Управляет выбором источника аудиосигнла в комнате
     *
     * @param connection текущее соединение
     * @param source     идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectSourceOfSoundInRoom(SmarthomeConnection connection, final int source) {
        return selectSourceOfSound(connection, SmarthomeDevice.AUDIOBOX, source);
    }

    /**
     * Управляет выбором источника аудиосигнла в ванной комнате
     *
     * @param connection текущее соединение
     * @param source     идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectSourceOfSoundInBathroom(SmarthomeConnection connection, final int source) {
        return selectSourceOfSound(connection, SmarthomeDevice.AUDIOBATH, source);
    }

//    /**
//     * Определяет номер источника аудиосигнала в комнате по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает номер источника аудиосигнала;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Integer getSelectedSourceOfSoundInRoom(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.AUDIOBOX
//                    && message.getCommand() == SmarthomeCommand.CHANNEL_INFO
//                    && message.getData().length == 1) {
//                return (int) message.getData()[0];
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет номер источника аудиосигнала в в ванной комнате по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает номер источника аудиосигнала;
//     * null - если передано неверное по формату сообщение для анализа
//     */
//    public static Integer getSelectedSourceOfSoundInBathRoom(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.AUDIOBATH
//                    && message.getCommand() == SmarthomeCommand.CHANNEL_INFO
//                    && message.getData().length == 1) {
//                return (int) message.getData()[0];
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет номер источника аудиосигнала в комнате.
//     * Отправляет команду аудиобоксу и
//     * в случае успешного ее выполнения определяет номер активного источника аудиосигнала
//     *
//     * @param connection текущее соединение
//     * @return возвращает номер источника аудиосигнала;
//     * null - не удалось определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getSelectedSourceOfSoundInRoom(SmarthomeConnection connection) {
//        SupradinDataMessage response = Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBOX,
//                Priority.COMMAND,
//                SmarthomeCommand.CHANNEL,
//                new byte[]{(byte) 0xFF},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBOX
//                                && receivedMessage.getCommand() == SmarthomeCommand.CHANNEL_INFO
//                                && receivedMessage.getData().length == 1;
//                    }
//                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE);
//        return getSelectedSourceOfSoundInRoom(response);
//    }
//
//
//    /**
//     * Определяет номер источника аудиосигнала в ванной комнате.
//     * Отправляет команду аудиобоксу и
//     * в случае успешного ее выполнения определяет номер активного источника аудиосигнала
//     *
//     * @param connection текущее соединение
//     * @return возвращает номер источника аудиосигнала;
//     * null - не удалось определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getSelectedSourceOfSoundInBathRoom(SmarthomeConnection connection) {
//        SupradinDataMessage response = Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBATH,
//                Priority.COMMAND,
//                SmarthomeCommand.CHANNEL,
//                new byte[]{(byte) 0xFF},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBATH
//                                && receivedMessage.getCommand() == SmarthomeCommand.CHANNEL_INFO
//                                && receivedMessage.getData().length == 1;
//                    }
//                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE);
//        return getSelectedSourceOfSoundInBathRoom(response);
//    }

    /**
     * Выключает звук
     *
     * @param connection текущее соединение
     * @param device    адрес устройства в сети clunet
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean mute(SmarthomeConnection connection, final SmarthomeDevice device) {
        return connection.sendResponsible(
                device,
                SmarthomeCommand.MUTE,
                new byte[]{0x00}, //пробуем переключить (как с пульта)
                receivedMessage -> {
                    return receivedMessage.getSrc() == device
                            && receivedMessage.getCommand() == SmarthomeCommand.VOLUME_INFO
                            && receivedMessage.getData().length > 2
                            && receivedMessage.getData()[0] == 0x00;
                },
                AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }


    /**
     * Выключает звук в комнате
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean muteInRoom(SmarthomeConnection connection) {
        return mute(connection, SmarthomeDevice.AUDIOBOX);
    }

    /**
     * Выключает звук в ванной комнате
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean muteInBathroom(SmarthomeConnection connection) {
        return mute(connection, SmarthomeDevice.AUDIOBATH);
    }

//
//    /**
//     * Устанавливает уровень громкости (в процентах от максимального) звука в
//     * комнате
//     *
//     * @param connection текущее соединение
//     * @param percent    уровень громкости в процентах
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean setSoundVolumeLevelInRoom(SmarthomeConnection connection, final int percent) {
//        return Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBOX,
//                Priority.COMMAND,
//                SmarthomeCommand.VOLUME,
//                new byte[]{0x00, (byte) percent},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBOX
//                                && receivedMessage.getCommand() == SmarthomeCommand.VOLUME_INFO
//                                && receivedMessage.getData().length == 2 /*&& supradinRecieved.getData()[0] == percent*/;       //TODO: баг с передачей громкости в процентах
//                    }
//                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
//    }
//
//    /**
//     * Устанавливает уровень громкости (в процентах от максимального) звука в
//     * ванной комнате
//     *
//     * @param connection текущее соединение
//     * @param percent    уровень громкости в процентах
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean setSoundVolumeLevelInBathRoom(SmarthomeConnection connection, final int percent) {
//        return Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBATH,
//                Priority.COMMAND,
//                SmarthomeCommand.VOLUME,
//                new byte[]{0x00, (byte) percent},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBATH
//                                && receivedMessage.getCommand() == SmarthomeCommand.VOLUME_INFO
//                                && receivedMessage.getData().length == 2 /*&& supradinRecieved.getData()[0] == percent*/;       //TODO: баг с передачей громкости в процентах
//                    }
//                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
//    }

    /**
     * Изменяет уровень громкости звука
     *
     * @param connection текущее соединение
     * @param inc        признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    private static boolean changeSoundVolumeLevel(SmarthomeConnection connection, final SmarthomeDevice device, final boolean inc) {
        return connection.sendResponsible(
                device,
                SmarthomeCommand.VOLUME,
                new byte[]{(byte) (inc ? 0x02 : 0x03)},
                SmarthomeMessageFilter.scl(device, SmarthomeCommand.VOLUME_INFO, 2),
                AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }

    /**
     * Изменяет уровень громкости звука в комнате
     *
     * @param connection текущее соединение
     * @param inc        признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundVolumeLevelInRoom(SmarthomeConnection connection, final boolean inc) {
        return changeSoundVolumeLevel(connection, SmarthomeDevice.AUDIOBOX, inc);
    }

    /**
     * Изменяет уровень громкости звука в ванной комнате
     *
     * @param connection текущее соединение
     * @param inc        признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundVolumeLevelInBathroom(SmarthomeConnection connection, final boolean inc) {
        return changeSoundVolumeLevel(connection, SmarthomeDevice.AUDIOBATH, inc);
    }

//    /**
//     * Определяет уровень громкости (в процентах от максимального) звука в
//     * комнате по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает уровень громкости звука в комнате; null - если
//     * передано неверное по формату сообщение для анализа
//     */
//    public static Integer getSoundVolumeLevelInRoom(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.AUDIOBOX
//                    && message.getCommand() == SmarthomeCommand.VOLUME_INFO
//                    && message.getData().length == 2
//                    && message.getData()[0] >= 0 && message.getData()[0] <= 100) {
//                return (int) message.getData()[0];
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет уровень громкости (в процентах от максимального) звука в
//     * ванной комнате по сообщению
//     *
//     * @param message входящее сообщение для анализа
//     * @return возвращает уровень громкости звука в комнате; null - если
//     * передано неверное по формату сообщение для анализа
//     */
//    public static Integer getSoundVolumeLevelInBathRoom(SupradinDataMessage message) {
//        if (message != null) {
//            if (message.getSrc() == SmarthomeDevice.AUDIOBATH
//                    && message.getCommand() == SmarthomeCommand.VOLUME_INFO
//                    && message.getData().length == 2
//                    && message.getData()[0] >= 0 && message.getData()[0] <= 100) {
//                return (int) message.getData()[0];
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Определяет уровень громкости (в процентах от максимального) звука в
//     * комнате
//     *
//     * @param connection текущее соединение
//     * @return возвращает уровень громкости звука в комнате; null - не удалось
//     * определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getSoundVolumeLevelInRoom(SmarthomeConnection connection) {
//        return getSoundVolumeLevelInRoom(Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBOX,
//                Priority.COMMAND,
//                SmarthomeCommand.VOLUME,
//                new byte[]{(byte) 0xFF},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBOX
//                                && receivedMessage.getCommand() == SmarthomeCommand.VOLUME_INFO
//                                && receivedMessage.getData().length == 2;
//                    }
//                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
//    }
//
//
//    /**
//     * Определяет уровень громкости (в процентах от максимального) звука в
//     * ванной комнате
//     *
//     * @param connection текущее соединение
//     * @return возвращает уровень громкости звука в комнате; null - не удалось
//     * определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getSoundVolumeLevelInBathRoom(SmarthomeConnection connection) {
//        return getSoundVolumeLevelInBathRoom(Smarthome.sendResponsible(connection,
//                SmarthomeDevice.AUDIOBATH,
//                Priority.COMMAND,
//                SmarthomeCommand.VOLUME,
//                new byte[]{(byte) 0xFF},
//                new SupradinConnectionResponseFilter() {
//
//                    @Override
//                    public boolean filter(SupradinDataMessage receivedMessage) {
//                        return receivedMessage.getSrc() == SmarthomeDevice.AUDIOBATH
//                                && receivedMessage.getCommand() == SmarthomeCommand.VOLUME_INFO
//                                && receivedMessage.getData().length == 2;
//                    }
//                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
//    }
//

    /**
     * Изменяет значения экавалайзера звука в комнате
     *
     * @param connection текущее соединение
     * @param param      параметр эквалайзера для изменения (EQUALIZER_GAIN, EQUALIZER_TREBLE, EQUALIZER_BASS)
     * @param inc        признак задает увеличение или уменьшение значения параметра
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundEqualizerInRoom(SmarthomeConnection connection, final int param, final boolean inc) {
        if (param == EQUALIZER_TREBLE || param == EQUALIZER_GAIN || param == EQUALIZER_BASS) {
            return connection.sendResponsible(
                    SmarthomeDevice.AUDIOBOX,
                    SmarthomeCommand.EQUALIZER,
                    new byte[]{(byte) param, (byte) (inc ? 0x02 : 0x03)},
                    SmarthomeMessageFilter.scl(SmarthomeDevice.AUDIOBOX, SmarthomeCommand.EQUALIZER_INFO, 3),
                    AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
        }
        return false;
    }

    private static boolean selectNextFMStation(SmarthomeConnection connection, final SmarthomeDevice device, final boolean up) {
        return connection.sendResponsible(
                device,
                SmarthomeCommand.FM,
                new byte[]{(byte) (up ? 0x02 : 0x03)},
                SmarthomeMessageFilter.scl(device, SmarthomeCommand.FM_INFO, 6),
                AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    public static boolean selectNextFMStationInRoom(SmarthomeConnection connection, final boolean up) {
        return selectNextFMStation(connection, SmarthomeDevice.AUDIOBOX, up);
    }

    public static boolean selectNextFMStationInBathroom(SmarthomeConnection connection, final boolean up) {
        return selectNextFMStation(connection, SmarthomeDevice.AUDIOBATH, up);
    }

    private static boolean selectFMFrequency(SmarthomeConnection connection, final SmarthomeDevice device, float frequency) {
        int freq = (int) (frequency * 100);
        final byte byte1 = (byte) (freq & 0xFF);
        final byte byte2 = (byte) ((freq >> 8) & 0xFF);
        return connection.sendResponsible(device,
                SmarthomeCommand.FM,
                new byte[]{(byte) 0x00, byte1, byte2},
                new SmarthomeMessageFilter() {

                    @Override
                    public boolean filter(SmarthomeMessage message) {
                        return message.getSrc() == device
                                && message.getCommand() == SmarthomeCommand.FM_INFO
                                && message.getData().length == 6
                                && message.getData()[0] == 0x00
                                && message.getData()[2] == byte1
                                && message.getData()[3] == byte2;
                    }
                }, AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    public static boolean selectFMFrequencyInRoom(SmarthomeConnection connection, float frequency) {
        return selectFMFrequency(connection, SmarthomeDevice.AUDIOBOX, frequency);
    }

    public static boolean selectFMFrequencyInBathroom(SmarthomeConnection connection, float frequency) {
        return selectFMFrequency(connection, SmarthomeDevice.AUDIOBATH, frequency);
    }

//    public static Integer  checkAndroidInBathtroomCommand(SupradinDataMessage message) {
//        if (message != null) {
//            switch (message.getCommand()) {
//                case LIGHT_LEVEL_INFO:
//                    if (message.getSrc() == SmarthomeDevice.BATH_SENSORS && message.getData().length == 2) {
//                        return message.getData()[0] == 1 ? 1 : 0;
//                    }
//                    break;
//                case ANDROID:
//                    if (message.getData().length == 1) {
//                        return (int) message.getData()[0];
//                    }
//                    break;
//                case RC_BUTTON_PRESSED:
//                    if (message.getData().length == 3) {
//                        if (message.getData()[0] == 0x00 && message.getData()[1] == 0x00) {
//                            switch (message.getData()[2]) {
//                                case 0x42:
//                                    return 0x02;
//                                case 0x52:
//                                    return 0x0A;
//                            }
//                        }
//                    }
//            }
//        }
//
//        return null;
//    }

    private static void writeFMStationsToEEPROM(SmarthomeConnection connection, final SmarthomeDevice device) {
        FMDictionary fmDict = FMDictionary.getInstance();
        if (fmDict != null) {
            //стираем
            if (connection.sendResponsible(
                    device,
                    SmarthomeCommand.FM,
                    new byte[]{(byte) 0xEE, (byte) 0xEE, (byte) 0xFF},
                    receivedMessage -> {
                        return receivedMessage.getSrc() == device
                                && receivedMessage.getCommand() == SmarthomeCommand.FM_INFO
                                && receivedMessage.getData().length == 2
                                && receivedMessage.getData()[0] == (byte) 0xEE
                                && receivedMessage.getData()[1] == (byte) 0x01;
                    },
                    FM_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null) {

                //пишем
                int i = 0;
                for (Map.Entry<Float, String> entry : fmDict.getStationList().entrySet()) {

                    int freq = (int) (entry.getKey() * 100);

                    connection.sendResponsible(
                            device,
                            SmarthomeCommand.FM,
                            new byte[]{(byte) 0xED, (byte) i++, (byte) (freq & 0xFF), (byte) ((freq >> 8) & 0xFF)},
                            receivedMessage -> {
                                return receivedMessage.getSrc() == device
                                        && receivedMessage.getCommand() == SmarthomeCommand.FM_INFO
                                        && receivedMessage.getData().length == 2
                                        && receivedMessage.getData()[0] == (byte) 0xED;
                            },
                            FM_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
                }
            }
        }
    }

    public static void writeFMStationsTOEEPROMInRoom(SmarthomeConnection connection) {
        writeFMStationsToEEPROM(connection, SmarthomeDevice.AUDIOBOX);
    }

    public static void writeFMStationsTOEEPROMInBathroom(SmarthomeConnection connection) {
        writeFMStationsToEEPROM(connection, SmarthomeDevice.AUDIOBATH);
    }


public static void writeHeatfloorProgramsToEEPROM(SmarthomeConnection connection) {
    HeatFloorDictionary hfDict = HeatFloorDictionary.getInstance();
    if (hfDict != null) {

        //сначала стирать ??
        for (int i = 0; i < 10; i++) {   //max 10 programs
            final HeatfloorProgram program = hfDict.getProgramList().get(i);
            if (program != null) {

                final byte[] data = new byte[program.getSchedule().length + 1];
                data[0] = (byte) (0xF0 | i);
                for (int j = 0; j < program.getSchedule().length; j++) {
                    data[j + 1] = (byte) ((int) program.getSchedule()[j]);
                }

                connection.sendResponsible(
                        SmarthomeDevice.RELAY_1,
                        SmarthomeCommand.HEATFLOOR,
                        data,
                        receivedMessage -> {
                            return receivedMessage.getSrc() == SmarthomeDevice.RELAY_1
                                    && receivedMessage.getCommand() == SmarthomeCommand.HEATFLOOR_INFO
                                    && receivedMessage.getData().length > 2
                                    && receivedMessage.getData()[0] == data[0]
                                    && receivedMessage.getData()[1] == program.getSchedule().length / 2;
                        },
                        HEATFLOOR_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
            }
        }
    }
}

    private static void selectHeatfloorMode(SmarthomeConnection connection, HeatfloorMode mode,
                                            int channel, byte[] params) {
        byte[] rdata = new byte[(params == null ? 0 : params.length) + 3];
        rdata[0] = (byte) 0xFE;
        rdata[1] = (byte) (0x01 << channel);
        rdata[2] = (byte) mode.getId();
        if (params != null) {
            System.arraycopy(params, 0, rdata, 3, params.length);
        }

        connection.sendResponsible(
                SmarthomeDevice.RELAY_1,
                SmarthomeCommand.HEATFLOOR, rdata,
                SmarthomeMessageFilter.sc(SmarthomeDevice.RELAY_1, SmarthomeCommand.HEATFLOOR_INFO),
                HEATFLOOR_COMMAND_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
    }

    public static void selectHeatfloorModeOff(SmarthomeConnection connection, int channel) {
        selectHeatfloorMode(connection, HeatfloorMode.OFF, channel, null);
    }

    public static void selectHeatfloorModeManual(SmarthomeConnection connection, int channel, int temperature) {
        selectHeatfloorMode(connection, HeatfloorMode.MANUAL, channel, new byte[]{(byte) temperature});
    }

    public static void selectHeatfloorModeDay(SmarthomeConnection connection, int channel, int program) {
        selectHeatfloorMode(connection, HeatfloorMode.DAY, channel, new byte[]{(byte) program});
    }

    public static void selectHeatfloorModeWeek(SmarthomeConnection connection, int channel, int program_mo_fr, int program_sa, int program_su) {
        selectHeatfloorMode(connection, HeatfloorMode.WEEK, channel,
                new byte[]{(byte) program_mo_fr, (byte) program_sa, (byte) program_su});
    }

    public static void selectHeatfloorModeParty(SmarthomeConnection connection, int channel, int temperature, int num_seconds) {
        selectHeatfloorMode(connection, HeatfloorMode.PARTY, channel,
                new byte[]{(byte) temperature, (byte) (num_seconds & 0xFF), (byte) ((num_seconds >> 8) & 0xFF)});
    }

    public static void selectHeatfloorModeDayForToday(SmarthomeConnection connection, int channel, int program) {
        selectHeatfloorMode(connection, HeatfloorMode.DAY_FOR_TODAY, channel, new byte[]{(byte) program});
    }


//    /**
//     * Устанавливает уровень диммера
//     *
//     * @param connection      текущее соединение
//     * @param deviceId        идентификатор устройства
//     * @param dimmerChannel   номер канала диммера
//     * @param value           значение диммера (0-255)
//     * @param responseTimeout
//     * @return возвращает true, если команда успешно выполнена
//     */
//    public static boolean setDimmerLevel(SmarthomeConnection connection,
//                                         final SmarthomeDevice deviceId, final int dimmerChannel, final int value,
//                                         int responseTimeout
//    ) {
//        return Smarthome.sendResponsible(connection,
//                deviceId,
//                SmarthomeCommand.DIMMER,
//                new byte[]{(byte) dimmerChannel, (byte) value},
//                SmarthomeMessageFilter.filter(deviceId, SmarthomeCommand.DIMMER_INFO, 3),
//                responseTimeout, NUM_ATTEMPTS_STATE) != null;
//    }
//
//    public static boolean setDimmerLevelInKitchenLight(SmarthomeConnection connection, final int value) {
//        return setDimmerLevel(connection, SmarthomeDevice.KITCHEN_LIGHT, 1, value, KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT);
//    }
//
//    public static boolean setDimmerLevelInSocketDimmer(SmarthomeConnection connection, final int value) {
//        return setDimmerLevel(connection, SmarthomeDevice.SOCKET_DIMMER, 1, value, SOCKET_DIMMER_WAITING_RESPONSE_TIMEOUT);
//    }
//
//    /**
//     * Определяет уровень диммера (0-255) по сообщению
//     *
//     * @param message       входящее сообщение для анализа
//     * @param deviceId
//     * @param dimmerChannel
//     * @return возвращает значение диммера; null - если передано неверное по
//     * формату сообщение для анализа
//     */
//    private static Integer getDimmerLevel(SmarthomeMessage message, SmarthomeDevice deviceId, int dimmerChannel) {
//        if (message != null) {
//            if (message.getSrc() == deviceId
//                    && message.getCommand() == SmarthomeCommand.DIMMER_INFO
//                    && message.getData().length > 1) {
//                int cnt = message.getData()[0] & 0xFF;
//                if (message.getData().length == cnt * 2 + 1) {
//                    for (int i = 1; i < message.getData().length; i += 2) {
//                        if ((message.getData()[i] & 0xFF) == dimmerChannel) {
//                            return message.getData()[i + 1] & 0xFF;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//
//    public static Integer getDimmerLevelInKitchen(SmarthomeMessage message) {
//        return getDimmerLevel(message, SmarthomeDevice.KITCHEN_LIGHT, 1);
//    }
//
//    public static Integer getDimmerLevelInSocket(SmarthomeMessage message) {
//        return getDimmerLevel(message, SmarthomeDevice.SOCKET_DIMMER, 1);
//    }
//
//    /**
//     * Получает значение диммера на кухне
//     *
//     * @param connection текущее соединение
//     * @return возвращает значение диммера на кухне; null - не удалось
//     * определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getDimmerLevelInKitchen(SmarthomeConnection connection) {
//        return getDimmerLevel(Smarthome.sendResponsible(connection,
//                SmarthomeDevice.KITCHEN_LIGHT,
//                SmarthomeCommand.DIMMER,
//                new byte[]{(byte) 0xFF},
//                SmarthomeMessageFilter.filter(SmarthomeDevice.KITCHEN_LIGHT, SmarthomeCommand.DIMMER_INFO, null),
//                KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE),
//                SmarthomeDevice.KITCHEN_LIGHT, 1);
//    }
//
//    /**
//     * Получает значение диммера в комнате
//     *
//     * @param connection текущее соединение
//     * @return возвращает значение диммера в комнате; null - не удалось
//     * определить (возникла ошибка при отправке или ответ не получен)
//     */
//    public static Integer getDimmerLevelInSocket(SmarthomeConnection connection) {
//        return getDimmerLevel(Smarthome.sendResponsible(connection,
//                SmarthomeDevice.SOCKET_DIMMER,
//                SmarthomeCommand.DIMMER,
//                new byte[]{(byte) 0xFF},
//                SmarthomeMessageFilter.filter(SmarthomeDevice.SOCKET_DIMMER, SmarthomeCommand.DIMMER_INFO, null),
//                KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE),
//                SmarthomeDevice.SOCKET_DIMMER, 1);
//    }
}
