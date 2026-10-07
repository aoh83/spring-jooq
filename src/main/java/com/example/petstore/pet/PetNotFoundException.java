package com.example.petstore.pet;

public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(long id) {
        super("Pet " + id + " not found");
    }
}
