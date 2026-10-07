package com.example.petstore.pet;

import com.example.petstore.web.BadRequestException;

public record PetQuery(int limit, int offset, PetSort sort) {

    public static final int MAX_LIMIT = 100;

    public PetQuery {
        if (limit < 1) {
            throw new BadRequestException("limit must be at least 1");
        }
        if (offset < 0) {
            throw new BadRequestException("offset must not be negative");
        }
        if (sort == null) {
            sort = PetSort.DEFAULT;
        }
        limit = Math.min(limit, MAX_LIMIT);
    }
}
