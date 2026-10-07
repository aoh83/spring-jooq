package com.example.petstore.pet;

import com.example.petstore.web.BadRequestException;

import java.util.Locale;

public record PetSort(Field field, Direction direction) {

    public enum Field {
        ID, NAME, TAG
    }

    public enum Direction {
        ASC, DESC
    }

    public static final PetSort DEFAULT = new PetSort(Field.ID, Direction.ASC);

    public static PetSort parse(String orderBy) {
        if (orderBy == null || orderBy.isBlank()) {
            return DEFAULT;
        }
        String[] parts = orderBy.trim().split(":");
        if (parts.length > 2) {
            throw new BadRequestException("orderBy must be <field> or <field>:<asc|desc>");
        }
        Field field = parseField(parts[0]);
        Direction direction = parts.length == 2 ? parseDirection(parts[1]) : Direction.ASC;
        return new PetSort(field, direction);
    }

    private static Field parseField(String raw) {
        try {
            return Field.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("orderBy must be one of id, name, tag");
        }
    }

    private static Direction parseDirection(String raw) {
        try {
            return Direction.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("orderBy direction must be asc or desc");
        }
    }
}
