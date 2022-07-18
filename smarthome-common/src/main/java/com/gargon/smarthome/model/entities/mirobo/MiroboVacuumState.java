package com.gargon.smarthome.model.entities.mirobo;

public enum MiroboVacuumState {
    UNKNOWN(0),
    STARTUP(1),
    CHARGER_CONNECTION_LOST(2),
    IDLE(3),
    REMOTE_CONTROL(4),
    CLEANING(5),
    GOING_HOME(6),
    MANUAL(7),
    CHARGING(8),
    CHARGING_ERROR(9),
    PAUSED(10),
    SPOT_CLEANUP(11),
    ERROR(12),
    SHUTDOWN(13),
    UPDATING(14),
    DOCKING(15),
    GOING_TO_TARGET(16),
    CLEANING_ZONE(17);

    private int code;

    MiroboVacuumState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MiroboVacuumState getByCode(int code) {
        for (MiroboVacuumState state : values()) {
            if (code == state.getCode()) {
                return state;
            }
        }
        return null;
    }
}
