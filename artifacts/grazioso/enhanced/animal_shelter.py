from pymongo import MongoClient 
import os

class AnimalShelter(object): 
    """ CRUD operations for Animal collection in MongoDB """ 

    def __init__(self): 
        # MongoDB connection settings with local defaults 
        HOST = os.getenv('MONGO_HOST', 'localhost') 
        PORT = int(os.getenv('MONGO_PORT', 27017)) 
        DB = os.getenv('MONGO_DB', 'aac') 
        COL = os.getenv('MONGO_COLLECTION', 'animals') 
 
        # Initialize MongoDB Connection 
        self.client = MongoClient(HOST,PORT)
        self.database = self.client[DB] 
        self.collection = self.database[COL] 

            
    # Method to implement the C in CRUD. 
    def create(self, data):
        if not isinstance(data, dict) or not data:
            print("Create error: data must be a non-empty dictionary")
            return False
        
        try:
            self.collection.insert_one(data)             
            return True
        except Exception as e:
            print("Create error:", e)
            return False

    # Method to implement the R in CRUD.
    def read(self, query):
        if not isinstance(query, dict):
            print("Read error: query must be a dictionary")
            return []
        
        try: 
            cursor = self.collection.find(query)
            return list(cursor)
        except Exception as e:
            print("Read error:", e)
            return []
        
        
    # Method to implement the U in CRUD.  
    def update(self, query, new_values):
        if not isinstance(query, dict) or not query:
            print("Update error: query must be a non-empty dictionary")
            return 0
        if not isinstance(new_values, dict) or not new_values:
            print("Update error: new_values must be a non-empty dictionary")
            return 0
        
        try:
            result = self.collection.update_many(query, {"$set": new_values})
            return result.modified_count
        except Exception as e:
            print("Update error:", e)
            return 0

        
    # Method to implement the D in CRUD.   
    def delete(self, query):
        if not isinstance(query, dict) or not query:
            print("Delete error: query must be a non-empty dictionary")
            return 0
        
        try:
            result = self.collection.delete_many(query)
            return result.deleted_count
        except Exception as e:
            print("Delete error:", e)
            return 0