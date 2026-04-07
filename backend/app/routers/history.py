from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from schemas import HistoryCreate, HistoryResponse
from app.database.base import get_db
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.db import History
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from app.core.secruity import get_current_user_id

# class HistoryCreate(BaseModel): 
#     user_id: UUID
#     base_currency: str
#     target_currency:str 
#     base_amount: Decimal
#     converted_amount: Decimal 
#     rate: Decimal
    

# class HistoryResponse(BaseModel): 
#     id: UUID
#     user_id: UUID
#     base_currency: str
#     target_currency:str 
#     base_amount: Decimal
#     converted_amount: Decimal 
#     rate: Decimal
#     created_at:datetime

#     class Config: 
#         from_attributes = True 

# class History(Base): 
#     __tablename__ = "history"

#     id = Column(UUID(as_uuid=True), primary_key=True, server_default=text("gen_random_uuid()"))
#     user_id = Column(UUID(as_uuid=True),ForeignKey("profile.user_id", ondelete=
#                                                    "CASCADE"), nullable= False)
#     base_currency = Column(String(10), nullable= False)
#     target_currency = Column(String(10),nullable= False)
#     base_amount = Column(Numeric(12,2), nullable= False)
#     converted_amount = Column(Numeric(12,2), nullable= False)
#     rate = Column(Numeric(12,2), nullable= False)
#     created_at = Column(DateTime(timezone= True),nullable= False, server_default=func.now())


app = FastAPI()

router = APIRouter(prefix = "/history", tags = ["history"])



@router.post("/", response_model = HistoryResponse)
def create_history(create: HistoryCreate, db:Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)):
    new_history = History(
        user_id = user_id, 
        base_currency = create.base_currency, 
        target_currency = create.target_currency, 
        base_amount = create.base_amount, 
        converted_amount = create.converted_amount, 
        rate = create.rate, 
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

