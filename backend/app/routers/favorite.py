from fastapi import FastAPI, APIRouter, HTTPException
from schemas import FavoriteCreate, FavoriteResponse
from app.database.base import get_db
from app.database.db import Favorite
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from uuid import UUID
from fastapi import Depends
from app.core.secruity import get_current_user_id
from app.services.currency_service import available_currency

AVAILABLE_CURRENCY = available_currency()

app = FastAPI()

router = APIRouter(prefix = "/favorite", tags = ["favorite"] )

@router.post("/", response_model = FavoriteResponse)
def create_favorite(create: FavoriteCreate, db: Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)): 
    
    create.base_currency = create.base_currency.upper()
    create.target_currency = create.target_currency.upper()

    if create.base_currency not in AVAILABLE_CURRENCY or create.target_currency not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=404, detail= "Currency does not exist")

    new_favorite = Favorite(
        user_id = user_id, 
        base_currency = create.base_currency, 
        target_currency = create.target_currency
    )

    try: 
        db.add(new_favorite)
        db.commit()
        db.refresh(new_favorite)
        return new_favorite
    except IntegrityError as e: 
        db.rollback()
        print("IntegrityError:", e)
        raise HTTPException(
            status_code=400, 
            detail = "Favorite already exists or invalid user" 
        )
    
    
@router.get("/me", response_model = list[FavoriteResponse])
def fetch_favorite(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)): 
    favorites = db.query(Favorite).filter(Favorite.user_id == user_id).all()

    return favorites

@router.delete("/me")
def delete_all_favorite(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)): 
    favorites = db.query(Favorite).filter(Favorite.user_id == user_id).all()

    if not favorites: 
        raise HTTPException(status_code=404, detail="favorites not found")


    for i in favorites: 
        db.delete(i)

    db.commit()

    return {"message": "Succesfully delete all"}


@router.delete("/{favorite_id}")
def delete_favorite(favorite_id: UUID, user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)): 
    favorite = db.query(Favorite).filter(Favorite.id == favorite_id,  Favorite.user_id == user_id).first()

    if not favorite: 
        raise HTTPException(status_code=404, detail = "favorite not found") 
    
    db.delete(favorite)
    db.commit()

    return {"message":"Succesfully delete"}









