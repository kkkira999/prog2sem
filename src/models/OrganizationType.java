package models;

public enum OrganizationType {
    COMMERCIAL,
    PUBLIC,
    GOVERNMENT,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    public static String names() {
        StringBuilder result = new StringBuilder();

        for (OrganizationType type : values()) {
            result.append(type.name()).append(", ");
        }

        return result.substring(0, result.length() - 2);
    }
}