from fastapi import FastAPI, APIRouter, HTTPException
from schemas import InsightResponse
from app.database.base import get_db
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.db import History
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from app.core.secruity import get_current_user_id
from app.services.currency_service import available_currency, currency_exchange, target_rate
from decimal import Decimal
from sqlalchemy import text
from app.services.ai_insight_service import ai_insight

router = APIRouter(prefix = "/insight", tags = ["insight"])

AVAILABLE_CURRENCY = available_currency()



@router.get("", response_model = InsightResponse , status_code = 200)
def get_insight(base: str, target: str):

    base = base.upper()
    target = target.upper() 


    if base not in AVAILABLE_CURRENCY or target not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail = "Input currency invalid")
    
    try: 
        results = ai_insight(base,target)
    except Exception as e : 
        raise HTTPException(status_code=500, detail = str(e))
    
    return {
        "pair": f"{base}/{target}",
        "insight": results
    }









