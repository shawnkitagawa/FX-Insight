from fastapi import FastAPI, APIRouter, HTTPException
from schemas import HistoryCreate, HistoryResponse
from app.database.base import get_db
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.db import History
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from app.core.secruity import get_current_user_id
from app.services.currency_service import available_currency, currency_exchange
from decimal import Decimal



router = APIRouter(prefix = "/history", tags = ["history"])

AVAILABLE_CURRENCY = available_currency()


@router.post("/", response_model = HistoryResponse)
def create_history(create: HistoryCreate, db:Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)):

    create.target_currency = create.target_currency.upper()
    create.base_currency = create.base_currency.upper()

    if create.target_currency not in AVAILABLE_CURRENCY or create.base_currency not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail = "Currency does not exist")

    data = currency_exchange(create.base_currency, create.target_currency,create.base_amount)
    print(data)

    if data is None: 
        raise HTTPException(status_code=404, detail = "Failed to fetch currency exchange")

    new_history = History(
        user_id = user_id, 
        base_currency = data["base"], 
        target_currency = data["quote"], 
        base_amount = create.base_amount, 
        converted_amount = data["target_total"], 
        rate = data["rate"], 
    )
    try: 
        db.add(new_history)
        db.commit()
        db.refresh(new_history)
        return new_history
    except IntegrityError: 
        db.rollback()
        raise HTTPException(status_code=400, detail="Invalid history data or constraint violation")
    
    

@router.get("/me", response_model = list[HistoryResponse])
def fetch_history(user_id:UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):

    current_history = db.query(History).filter(History.user_id == user_id).all()

    return current_history

@router.delete("{/history_id}")
def delete_history(history_id: UUID, user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):
    
    history = db.query(History).filter(History.user_id == user_id , History.id == history_id).first()

    if not history:
        raise HTTPException(status_code=404, detail = "history not found")
    

    db.delete(history) 
    db.commit()

    return {"message": "History succesfully deleted"}


@router.delete("/me")
def delete_all_history(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)): 

    histories = db.query(History).filter(History.user_id == user_id).all()

    if not histories: 
        raise HTTPException(status_code=404, detail = "Histories not found") 
    for i in histories: 
        db.delete(i)

    db.commit()


    return {"message": {"All history data succesfully deleted"}}
