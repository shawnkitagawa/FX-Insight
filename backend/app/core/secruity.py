from fastapi import Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import httpx
from uuid import UUID
from jose import jwt
import requests
import os 
from dotenv import load_dotenv
from pathlib import Path
from app.database.base import get_db
from sqlalchemy.orm import Session

BASE_DIR = Path(__file__).resolve().parent.parent.parent
env_path = BASE_DIR/ ".env"
load_dotenv(env_path)

security = HTTPBearer()
SUPABASE_URL = os.getenv("SUPABASE_URL")
API_KEY = os.getenv("API_KEY")

def get_jwks(): 
    response = httpx.get(f"{SUPABASE_URL}/auth/v1/.well-known/jwks.json")
    return response.json()

def get_current_user_id(
        credentials: HTTPAuthorizationCredentials = Depends(security),
        db: Session = Depends(get_db)
) -> UUID:
    token = credentials.credentials

    try: 
        jwks = get_jwks()
        payload = jwt.decode(
            token, 
            jwks, 
            algorithms=["ES256"],  # algorithm confusion attack
            audience = "authenticated"
        )

        user_id = payload.get("sub")

        if not user_id: 
            raise HTTPException(status_code = 401, detail = "Invalid token")
        
        try: 
         
           return UUID(user_id) 
        except ValueError: 
            raise HTTPException(status_code=401, detail = "Invalid user ID format")
        


    except: 
        raise HTTPException(status_code=401, detail = "Invalid or expired token")
        