from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from schemas import FavoriteCreate, FavoriteResponse
from app.database.base import get_db
from app.database.db import Favorite
from sqlalchemy.exc import IntegrityError
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Session
from fastapi import Depends

# class FavoriteCreate(BaseModel): 
#     user_id: UUID
#     base_currency: str
#     target_currency: str 


# class FavoriteResponse(BaseModel): 
#     id: UUID
#     user_id: UUID
#     base_currency: str
#     target_currency: str
#     created_at: datetime
    
#     class Config: 
#         from_attributes = True 


app = FastAPI()

router = APIRouter(prefix = "/favorite", tags = ["favorite"] )

@router.post("/", response_model = FavoriteResponse)
def create_favorite(create: FavoriteCreate, db: Session = Depends(get_db)): 

    new_favorite = Favorite(
        user_id = create.user_id, 
        base_currency = create.base_currency, 
        target_currency = create.target_currency
    )

    try: 
        db.add(new_favorite)
        db.commit()
        db.refresh(new_favorite)
        return new_favorite
    except IntegrityError: 
        db.rollback()
        raise HTTPException(
            status_code=400, 
            detail = "Favorite already exists or invalid user" 
        )
    
@router.get("/me", response_model = list[FavoriteResponse])
def fetch_favorite(user_id: UUID, db: Session = Depends(get_db)): 
    favorites = db.query(Favorite).filter(Favorite.user_id == user_id).all()


    return favorites









