import json
from animal_shelter import AnimalShelter

shelter = AnimalShelter()

DATA_FILE = "sample_animals.json"

print("Setting up Grazioso Salvare database...\n")

# Load sample animal data
try:
    with open(DATA_FILE, "r") as file:
       animals = json.load(file)

    print(f"Loaded {len(animals)} animals from '{DATA_FILE}'.")

except (FileNotFoundError, json.JSONDecodeError) as e:
    print("Unable to load sample animal data:", e)
    raise SystemExit(1)

#Add sample animals that are not already in the database
added = 0
skipped = 0

for animal in animals:
    animal_id = animal.get("animal_id")

    if not animal_id:
        print("Skipping animal without an 'animal_id':", animal)
        skipped += 1
        continue

    existing = shelter.read({"animal_id": animal_id})
    if existing:
        skipped += 1
    else:
        shelter.create(animal)
        added += 1

print(f"Added {added} new animal(s).")
print(f"Skipped {skipped} existing or invalid animal(s).")

#Create index used by dashboard rescue filters
index_name = shelter.collection.create_index([
    ("animal_type", 1),
    ("breed", 1),
    ("age_upon_outcome_in_weeks", 1)
])
print(f"\nIndex ready: {index_name}")

#Display current indexes
print("\nCurrent indexes:")
for index in shelter.collection.list_indexes():
    print(index["name"], index["key"])

#Display final animal count
total = shelter.collection.count_documents({})
print(f"\nDatabase setup complete.")
print(f"Total animals in the database: {total}")