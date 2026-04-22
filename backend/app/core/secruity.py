from fastapi import Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import httpx
from uuid import UUID
from jose import jwt, JWTError
from app.database.base import get_db
from sqlalchemy.orm import Session
from app.core.config import SUPABASE_URL, API_KEY


security = HTTPBearer()

def get_jwks(): 
    response = httpx.get(f"{SUPABASE_URL}/auth/v1/.well-known/jwks.json")
    return response.json()

def get_current_user_id(
        credentials: HTTPAuthorizationCredentials = Depends(security),
        db: Session = Depends(get_db)
) -> UUID:
    token = credentials.credentials.strip()

    try: 
        matching_key = None 
        jwks = get_jwks()

        unverified_header = jwt.get_unverified_header(token) 
        kid = unverified_header.get("kid")

        for key in jwks.get("keys", []): 
            if key.get("kid") == kid:
                matching_key = key
                break 

        if not matching_key: 
            raise HTTPException(status_code=401, detail = "No matching JWT found")

        payload = jwt.decode(
            token, 
            matching_key , 
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
    

    except JWTError as e:
        print("JWT decode error:", str(e))
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    except HTTPException:
        raise
    except Exception as e:
        print("Unexpected auth error:", str(e))
        raise HTTPException(status_code=500, detail=f"{str(e)}Authentication failed")
        