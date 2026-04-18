package common.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Organization implements Serializable {
    private Long id;
    private String name;
    private String fullName;
    private OrganizationType type;

    private static Long nextId = 0L;
    public Organization(String name, String fullName, OrganizationType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть null или пустым");
        }

        if (fullName != null) {
            if (fullName.trim().isEmpty()) {
                throw new IllegalArgumentException("fullName не может быть пустым");
            }

            if (fullName.length() > 1895) {
                throw new IllegalArgumentException("fullName не может быть длиннее 1895");
            }
        }

        if (type == null) {
            throw new IllegalArgumentException("type не может быть null");
        }

        this.id = generateId();
        this.name = name;
        this.fullName = fullName;
        this.type = type;
    }
    public Organization(Long id, String name, String fullName, OrganizationType type) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть > 0 и не null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть null или пустым");
        }

        if (fullName != null) {
            if (fullName.trim().isEmpty()) {
                throw new IllegalArgumentException("fullName не может быть пустым");
            }

            if (fullName.length() > 1895) {
                throw new IllegalArgumentException("fullName не может быть длиннее 1895");
            }
        }

        if (type == null) {
            throw new IllegalArgumentException("type не может быть null");
        }

        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.type = type;
    }
    private Long generateId() {
        return ++nextId;
    }
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public OrganizationType getType() {
        return type;
    }
}