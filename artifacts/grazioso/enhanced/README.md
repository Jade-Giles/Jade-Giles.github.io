# Grazioso Salvare Animal Shelter Dashboard

This project is an enhanced version of the Grazioso Salvare animal shelter
dashboard originally created for CS 340. The application uses MongoDB for
animal records and a Dash dashboard for filtering, visualization, and
geolocation data.

## Enhancements

- Updated the MongoDB CRUD module with input validation and error handling
- Removed hard coded database configuration in favor of environment variables
- Added safeguards against empty update and delete queries
- Added a compound database index based on dashboard query patterns
- Added a reproducible sample dataset and database setup script
- Added CRUD and rescue filter testing
- Updated the original JupyterDash implementation to the current Dash framework
- Updated map functionality to use named fields instead of column positions

## Setup

1. Start MongoDB.

2. Install the required Python packages:
    pip install -r requirements.txt
    
3. Set up the database:
    python database_setup.py

This loads the sample animal data and creates the required database index.

## Testing

Run:

    python test_crud.py

    python test_filters.py

## Dashboard

Start JupyterLab:

    jupyter lab

Open the enhanced Grazioso Salvare dashboard notebook and run all cells.

## Database Configuration

The application uses the following defaults:

- Host: localhost
- Port: 27017
- Database: aac
- Collection: animals

These settings can be changed using the environment variables:

- MONGO_HOST
- MONGO_PORT
- MONGO_DB
- MONGO_COLLECTION
