package com.bookaroom.enums;

import java.util.ArrayList;
import java.util.List;

public enum ListingType {
    ROOM(0),
    HOUSE(1);

    private long value;

    private ListingType(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public static List<String> names() {
        List<String> names = new ArrayList<>();
        for (ListingType type : ListingType.values()) {
            names.add(type.name());
        }

        return names;
    }
}
