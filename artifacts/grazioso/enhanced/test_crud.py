from animal_shelter import AnimalShelter

shelter = AnimalShelter()

test_animal = {
    "animal_id": "TEST001",
    "animal_type": "Dog",
    "name": "Test Pup",
    "breed": "Test Breed",
    "age_upon_outcome_in_weeks": 20
}

print("Starting CRUD test...\n")

# Clean up TEST001 if a previous test was interrupted
shelter.delete({"animal_id": "TEST001"})

# CREATE
created = shelter.create(test_animal)

if created:
    print("CREATE passed.")
else:
    print("CREATE failed.")


# READ
results = shelter.read({"animal_id": "TEST001"})

if len(results) == 1 and results[0]["name"] == "Test Pup":
    print("READ passed.")
else:
    print("READ failed.")


# UPDATE
updated = shelter.update(
    {"animal_id": "TEST001"},
    {"name": "Updated Test Pup"}
)

updated_results = shelter.read({"animal_id": "TEST001"})

if (
    updated == 1
    and len(updated_results) == 1
    and updated_results[0]["name"] == "Updated Test Pup"
):
    print("UPDATE passed.")
else:
    print("UPDATE failed.")


# DELETE
deleted = shelter.delete({"animal_id": "TEST001"})
remaining = shelter.read({"animal_id": "TEST001"})

if deleted == 1 and len(remaining) == 0:
    print("DELETE passed.")
else:
    print("DELETE failed.")


print("\nCRUD test complete.")