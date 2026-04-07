from fastapi import Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import httpx
from sqlalchemy.dialects.postgresql import UUID
from jose import jwt, JWTError

security = HTTPBearer()
SUPABASE_URL = "https://nilbmddhnsvndzrahbmk.supabase.co"

#fetching the key from supabase 
def get_jwks(): 
    response = httpx.get(f"{SUPABASE_URL}/auth/v1/.well-known/jwks.json")
    return response.json()


def get_current_user_id(
        credentials: HTTPAuthorizationCredentials = Depends(security)
) -> UUID:
    token = credentials.credentials

    try: 
        jwks = get_jwks()
        payload = jwt.decode(
            token, 
            jwks, 
            algorithms=["RS256"], 
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
        