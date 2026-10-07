package com.example.petstore.web;

import com.example.petstore.api.NewPet;
import com.example.petstore.api.Pet;
import com.example.petstore.pet.PetNotFoundException;
import com.example.petstore.pet.PetQuery;
import com.example.petstore.pet.PetRepository;
import com.example.petstore.pet.PetSort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetRepository repository;

    public PetController(PetRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Pet> list(
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "orderBy", required = false) String orderBy) {
        return repository.findAll(new PetQuery(limit, offset, PetSort.parse(orderBy)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody NewPet newPet) {
        if (newPet.name() == null || newPet.name().isBlank()) {
            throw new BadRequestException("name is required");
        }
        Pet pet = repository.insert(newPet);
        return ResponseEntity.created(URI.create("/pets/" + pet.id())).build();
    }

    @GetMapping(value = "/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Pet show(@PathVariable("petId") long petId) {
        return repository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));
    }
}
