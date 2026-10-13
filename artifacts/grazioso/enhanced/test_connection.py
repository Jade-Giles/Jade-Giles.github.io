from animal_shelter import AnimalShelter

shelter = AnimalShelter()

deleted = shelter.delete({"animal_id": "TEST001"})

if deleted == 1:
    print("DELETE test passed.")

    results = shelter.read({"animal_id": "TEST001"})

    if not results:
        print("Verified: Test Pup is gone.")
    else:
        print("DELETE verification failed.")
else:
    print("DELETE test failed.")