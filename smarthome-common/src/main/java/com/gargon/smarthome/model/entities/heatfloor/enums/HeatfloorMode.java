package com.gargon.smarthome.model.entities.heatfloor.enums;

public enum HeatfloorMode {
    OFF(0, "Выкл."),
    MANUAL(1, "режим ручной"),
    DAY(2, "режим дневной"),
    WEEK(3, "режим недельный"),
    PARTY(4, "режим вечеринка"),
    DAY_FOR_TODAY(5, "режим дневной до конца дня");

    private int id;
    private String name;

    HeatfloorMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static HeatfloorMode getById(int id) {
        for (HeatfloorMode mode : values()) {
            if (id == mode.getId()) {
                return mode;
            }
        }
        return null;
    }
}
