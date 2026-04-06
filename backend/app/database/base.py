from sqlalchemy import create_engine 
from sqlalchemy.orm import sessionmaker 
from sqlalchemy.ext.declarative import declarative_base


DATABASE_URL = "URL"


# Connection to PostgreSQL Creagte a connection tool like a cable
engine = create_engine(DATABASE_URL) 


# Open DB session   fresh connection for each request 
SessionLocal = sessionmaker(
    # you must commit every time 
    autocommit=False, 
    # don't automatically push changes to the Db 

    autoflush=False, 
    #connect session to the database
    bind= engine 
)

# Parent for the model required for the table creation 
Base = declarative_base()

#FastAPi dependency to use DB in routes 
def get_db(): 
    db = SessionLocal()
    try: 
        yield db
    finally: 
        db.close()

