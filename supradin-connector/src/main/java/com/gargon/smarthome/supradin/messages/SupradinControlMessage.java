package com.gargon.smarthome.supradin.messages;

public class SupradinControlMessage {

    //control command codes
    public final static byte CONTROL_COMMAND_PING = 0;
    public final static byte CONTROL_COMMAND_CONNECT = 1;
    public final static byte CONTROL_COMMAND_DISCONNECT = 2;


    private final static int CONTROL_MESSAGE_LENGTH = 7;

    private final static int CONTROL_MESSAGE_OFFSET_ID = 0;    //uint16
    private final static int CONTROL_MESSAGE_OFFSET_COMMAND = 2;
    private final static int CONTROL_MESSAGE_OFFSET_RESULT = 3;
    private final static int CONTROL_MESSAGE_OFFSET_CONNECTION_STATUS = 4;
    private final static int CONTROL_MESSAGE_OFFSET_CONNECTION_USED = 5;
    private final static int CONTROL_MESSAGE_OFFSET_CONNECTION_FREE = 6;

    public static char getCommandId(byte[] data) {
        char r = 0;
        if (data != null && data.length >= CONTROL_MESSAGE_OFFSET_COMMAND) {
            r = (char) ((data[CONTROL_MESSAGE_OFFSET_ID + 0] << 8) | (data[CONTROL_MESSAGE_OFFSET_ID + 1]));
        }
        return r;
    }

    public static byte getCommandType(byte[] data) {
        byte r = -1;
        if (data != null && data.length >= CONTROL_MESSAGE_OFFSET_COMMAND) {
            r = data[CONTROL_MESSAGE_OFFSET_COMMAND];
        }
        return r;
    }

    public static boolean isResultSuccess(byte[] data) {
        boolean r = false;
        if (data != null && data.length == CONTROL_MESSAGE_LENGTH) {
            if (data[CONTROL_MESSAGE_OFFSET_RESULT] == 1) {
                r = true;
            }
        }
        return r;
    }

    public static boolean isConnectionActive(byte[] data) {
        boolean r = false;
        if (data != null && data.length == CONTROL_MESSAGE_LENGTH) {
            if (data[CONTROL_MESSAGE_OFFSET_CONNECTION_STATUS] == 1) {
                r = true;
            }
        }
        return r;
    }

    public static byte getConnectionCountUsed(byte[] data) {
        byte r = -1;
        if (data != null && data.length == CONTROL_MESSAGE_LENGTH) {
            r = data[CONTROL_MESSAGE_OFFSET_CONNECTION_USED];
        }
        return r;
    }

    public static byte getConnectionCountFree(byte[] data) {
        byte r = -1;
        if (data != null && data.length == CONTROL_MESSAGE_LENGTH) {
            r = data[CONTROL_MESSAGE_OFFSET_CONNECTION_FREE];
        }
        return r;
    }

}
