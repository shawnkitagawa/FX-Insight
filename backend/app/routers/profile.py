from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from schemas import ProfileCreate, ProfileResponse
from app.database.db import Profile
from sqlalchemy.exc import IntegrityError
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.base import get_db




app = FastAPI()

router = APIRouter(prefix = "/profile", tags = ["profile"] )


@router.post("/", response_model= ProfileResponse)
def create_profile(create: ProfileCreate, db: Session = Depends(get_db)):
    new_profile = Profile(
        user_id = create.user_id, 
        user_name = create.user_name
    )

    try:
        db.add(new_profile)
        db.commit()
        db.refresh(new_profile)
        return new_profile
    except IntegrityError: 
        db.rollback()
        raise HTTPException(status_code=400, detail = "Favorite already exists or invalid user")
    
@router.get("/me", response_model = ProfileResponse)
def fetch_profile(user_id: UUID, db: Session = Depends(get_db)):
    profile = db.query(Profile).filter(Profile.user_id == user_id)

    return profile 



                   