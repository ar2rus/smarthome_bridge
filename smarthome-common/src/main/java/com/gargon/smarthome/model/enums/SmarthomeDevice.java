package com.gargon.smarthome.model.enums;

/**
 * Зарегистрированные устройства и их адреса
 */
public enum SmarthomeDevice {

    SUPRADIN(0x00),
    BROADCAST(0xFF),

    AUDIOBATH(0x0B),

    RELAY_1(0x14),
    RELAY_2(0x15),

    SOCKET_DIMMER(0x20),

    KITCHEN(0x1D),
    BATH_SENSORS(0x1E),
    WARDROBE(0x1F),

    METEO(0x81),
    KITCHEN_LIGHT(0x82),
    WATER_SYSTEM(0x83),
    AUDIOBOX(0x84),
    SWING_SOCLE(0x85),
    ENERGY_METER(0x86),

    TELEPHONE(0x91);

    private int address;

    SmarthomeDevice(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public static SmarthomeDevice getByAddress(int address) {
        for (SmarthomeDevice device : values()) {
            if (address == device.address) {
                return device;
            }
        }
        return null;
    }

    public static final int MULTICAST_DEVICE_MASK = 0x80;

    public static boolean isMulticastDevice(int address) {
        return (address & MULTICAST_DEVICE_MASK) > 0 && address != BROADCAST.address;
    }

    public boolean isMulticastDevice() {
        return isMulticastDevice(address);
    }

    public static final int VIRTUAL_DEVICE_MASK = 0x40;

    public static boolean isVirtualDevice(int address) {
        return (address & VIRTUAL_DEVICE_MASK) > 0 && address != BROADCAST.address;
    }

    public boolean isVirtualDevice() {
        return isVirtualDevice(address);
    }
}
