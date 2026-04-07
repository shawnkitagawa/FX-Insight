from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from app.core.secruity import get_current_user_id
from schemas import AlertCreate, AlertResponse, AlertUpdate
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.db import Alert
from app.database.base import get_db
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from app.services.currency_service import target_rate


app = FastAPI()

router = APIRouter(prefix = "/alert", tags = ["alert"])

@router.post("/", response_model= AlertResponse)
def create_alert(create: AlertCreate, db: Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)):

    try: 
        new_alert = Alert(
        user_id = user_id, 
        alert_target = create.alert_target, 
        direction = create.direction, 
        last_checked_rate = target_rate(create.base_currency, create.target_currency),
        last_checked_at = datetime.now(timezone.utc),
        base_currency = create.base_currency, 
        target_currency = create.target_currency, 
        is_active = create.is_active, 
        )

        db.add(new_alert) 
        db.commit()
        db.refresh(new_alert)
        return new_alert

    except IntegrityError: 
        db.rollback()
        raise HTTPException(status_code=409, detail="Failed already existed or invalid user")
    
@router.put("/{alert_id}", response_model = AlertResponse)
def update_alert(alert_id: UUID, update: AlertUpdate, db: Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)): 
    latest_alert = db.query(Alert).filter(Alert.id == alert_id, Alert.user_id == user_id).first()
    
    if not latest_alert:
        raise HTTPException(status_code=404, detail="Alert not found")

    if update.alert_target is not None:
        latest_alert.alert_target = update.alert_target

    if update.is_active is not None:
        latest_alert.is_active = update.is_active

    db.commit()
    db.refresh(latest_alert)
    return latest_alert

@router.post("/refresh-active")
def auto_alert_check( db: Session = Depends(get_db)): 
    alerts = db.query(Alert).filter(Alert.is_active == True).all()

    if not alerts: 
        raise HTTPException(status_code=404, detail = "Alert not found")
    
    for i in alerts: 
        i.last_checked_rate = target_rate(i.base_currency, i.target_currency)
        i.last_checked_at = datetime.now(timezone.utc)
    
    db.commit()

    return {"message": f"{len(alerts)} alerts updated"}


@router.get("/me", response_model = list[AlertResponse])
def fetch_alert(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):
    alerts = db.query(Alert).filter(Alert.user_id == user_id).all()

    return alerts

