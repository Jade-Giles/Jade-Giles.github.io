from animal_shelter import AnimalShelter

shelter = AnimalShelter()

filters = {
    "WATER": {
        "animal_type": "Dog",
        "breed": {"$regex": "Labrador|Chesapeake|Newfoundland", "$options": "i"},
        "age_upon_outcome_in_weeks": {"$gte": 26, "$lte": 156}
    },

    "MOUNTAIN": {
        "animal_type": "Dog",
        "breed": {
            "$regex": "German Shepherd|Alaskan Malamute|Old English Sheepdog|Siberian Husky",
            "$options": "i"
        },
        "age_upon_outcome_in_weeks": {"$gte": 26, "$lte": 156}
    },

    "DISASTER": {
        "animal_type": "Dog",
        "breed": {
            "$regex": "Doberman|Rottweiler|Golden Retriever|Bloodhound",
            "$options": "i"
        },
        "age_upon_outcome_in_weeks": {"$gte": 26, "$lte": 156}
    }
}

for filter_name, query in filters.items():
    results = shelter.read(query)

    print(f"\n{filter_name}: {len(results)} result(s)")

    for animal in results:
        print(f"  {animal['name']} - {animal['breed']}")