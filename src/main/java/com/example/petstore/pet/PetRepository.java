package com.example.petstore.pet;

import com.example.petstore.api.NewPet;
import com.example.petstore.api.Pet;
import org.jooq.DSLContext;
import org.jooq.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.petstore.db.jooq.generated.Tables.PET;

@Repository
public class PetRepository {

    private final DSLContext dsl;

    public PetRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Pet> findAll(PetQuery query) {
        SortOrder order = query.sort().direction() == PetSort.Direction.DESC ? SortOrder.DESC : SortOrder.ASC;
        var sortField = switch (query.sort().field()) {
            case ID -> PET.ID;
            case NAME -> PET.NAME;
            case TAG -> PET.TAG;
        };
        return dsl.selectFrom(PET)
                .orderBy(sortField.sort(order))
                .limit(query.limit())
                .offset(query.offset())
                .fetch(record -> new Pet(record.getId(), record.getName(), record.getTag()));
    }

    public Optional<Pet> findById(long id) {
        return dsl.selectFrom(PET)
                .where(PET.ID.eq(id))
                .fetchOptional(record -> new Pet(record.getId(), record.getName(), record.getTag()));
    }

    public Pet insert(NewPet newPet) {
        return dsl.insertInto(PET)
                .set(PET.NAME, newPet.name())
                .set(PET.TAG, newPet.tag())
                .returning(PET.ID)
                .fetchOne(record -> new Pet(record.get(PET.ID), newPet.name(), newPet.tag()));
    }
}
