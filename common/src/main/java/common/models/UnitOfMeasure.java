package common.models;

import java.io.Serializable;

public enum UnitOfMeasure implements Serializable {
    KILOGRAMS,
    MILLILITERS,
    GRAMS,
    MILLIGRAMS;

    public static String names() {
        StringBuilder result = new StringBuilder();

        for (UnitOfMeasure unit : values()) {
            result.append(unit.name()).append(", ");
        }

        return result.substring(0, result.length() - 2);
    }
}