package com.gargon.smarthome.logger.readers;

import org.json.JSONObject;


public class ReaderFactory {

    public enum ReaderEnum {
        TEMPERATURE("temperature"),
        ONEWIRE("temperature"),
        DATABYTE("databyte"),
        DATABYTES("databytes"),
        HEATFLOOR("heatfloor"),
        DIMMER("dimmer");

        private String alias;

        ReaderEnum(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }

        public static ReaderEnum getByAlias(String alias) {
            for (ReaderEnum r : values()) {
                if (r.getAlias().equals(alias)) {
                    return r;
                }
            }
            return null;
        }
    }

    public static Reader createReader(JSONObject config) {
        if (config != null) {
            String readerName = config.optString("name", null);
            ReaderEnum reader = null;
            try {
                reader = ReaderEnum.valueOf(readerName);
            } catch (Exception e) {
                reader = ReaderEnum.getByAlias(readerName);
            }

            if (reader != null) {
                switch (reader) {
                    case TEMPERATURE:
                        return new TemperatureReader();
                    case ONEWIRE:
                        return new OneWireReader();
                    case DATABYTE:
                        return new DataByteReader(config);
                    case DATABYTES:
                        return new DataBytesReader(config);
                    case HEATFLOOR:
                        return new HeatfloorReader();
                    case DIMMER:
                        return new DimmerReader();
                }
            }
        }
        return new DefaultReader();
    }
}
