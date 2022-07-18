package com.gargon.smarthome.logger.filters;

import org.json.JSONObject;

public class FilterFactory {

    public enum FilterEnum {
        DATA_BYTE_CMP("databytecmp"),
        NOT_STATE_REQUEST_FILTER("notaresponse"),
        DST("dst"),
        SRC("src"),
        SRC_DST("src_dst");

        private String alias;

        FilterEnum(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }

        public static FilterEnum getByAlias(String alias) {
            for (FilterEnum f : values()) {
                if (f.getAlias().equals(alias)) {
                    return f;
                }
            }
            return null;
        }
    }

    public static Filter createFilter(JSONObject config) {
        if (config != null) {
            String filterName = config.optString("name", null);
            FilterEnum filter = null;
            try {
                filter = FilterEnum.valueOf(filterName);
            } catch (Exception e) {
                filter = FilterEnum.getByAlias(filterName);
            }

            if (filter != null) {
                switch (filter) {
                    case DATA_BYTE_CMP:
                        return new DataByteFilter(config);
                    case NOT_STATE_REQUEST_FILTER:
                        return new NotStateRequestFilter();
                    case DST:
                        return new DstFilter(config);
                    case SRC:
                        return new SrcFilter(config);
                    case SRC_DST:
                        return new SrcDstFilter(config);
                }
            }
        }
        return null;
    }
}
