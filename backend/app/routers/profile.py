from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from schemas import ProfileCreate, ProfileResponse
from app.database.db import Profile
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.base import get_db
from app.core.secruity import get_current_user_id


app = FastAPI()

router = APIRouter(prefix = "/profile", tags = ["profile"] )


@router.post("/", response_model= ProfileResponse)
def create_profile(create: ProfileCreate, db: Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)):
    new_profile = Profile(
        user_id = user_id, 
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
def fetch_profile(user_id: UUID =  Depends(get_current_user_id), db: Session = Depends(get_db)):
    profile = db.query(Profile).filter(Profile.user_id == user_id).first()

    if profile is None: 
        raise HTTPException(status_code=404, detail= "Profile not found")

    return profile 



                   