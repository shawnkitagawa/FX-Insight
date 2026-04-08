from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from app.core.secruity import get_current_user_id
from schemas import AlertCreate, AlertResponse, AlertUpdate
from sqlalchemy.orm import Session
from fastapi import Depends
from app.database.db import Alert
from app.database.base import get_db
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from app.services.currency_service import target_rate
from app.database.base import SessionLocal



router = APIRouter(prefix = "/alert", tags = ["alert"])

@router.post("/", response_model= AlertResponse)
def create_alert(create: AlertCreate, db: Session = Depends(get_db), user_id: UUID = Depends(get_current_user_id)):

    create.base_currency = create.base_currency.upper()
    create.target_currency = create.target_currency.upper()

    try: 
        current_rate = target_rate(create.base_currency, create.target_currency)["rate"]
    except Exception:
        raise HTTPException(status_code=400, detail = "Failed to fetch exchange rate") 
    
    triggered = ((create.direction.value == "above" and current_rate >= create.alert_target)
                 or
              (create.direction.value == "below" and current_rate <= create.alert_target))
    
    if triggered: 
        create.is_active = False

    try: 
        new_alert = Alert(
        user_id = user_id, 
        alert_target = create.alert_target, 
        direction = create.direction.value, 
        last_checked_rate = current_rate,
        last_checked_at = datetime.now(timezone.utc),
        base_currency = create.base_currency, 
        target_currency = create.target_currency, 
        is_active = create.is_active, 
        is_triggered = triggered,
        triggered_at = datetime.now(timezone.utc) if triggered else None)
        

        db.add(new_alert) 
        db.commit()
        db.refresh(new_alert)
        return new_alert

    except IntegrityError: 
        db.rollback()
        raise HTTPException(status_code=409, detail="Failed already existed or invalid user")
    
@router.put("/{alert_id}", response_model=AlertResponse)
def update_alert(
    alert_id: UUID,
    update: AlertUpdate,
    db: Session = Depends(get_db),
    user_id: UUID = Depends(get_current_user_id)
):
    latest_alert = db.query(Alert).filter(
        Alert.id == alert_id,
        Alert.user_id == user_id
    ).first()

    if not latest_alert:
        raise HTTPException(status_code=404, detail="Alert not found")

    if update.alert_target is not None:
        latest_alert.alert_target = update.alert_target

    if update.is_active is not None:
        latest_alert.is_active = update.is_active
        if update.is_active:
            latest_alert.is_triggered = False
            latest_alert.triggered_at = None

    try:
        current_rate = target_rate(
            latest_alert.base_currency,
            latest_alert.target_currency
        )["rate"]
    except Exception:
        raise HTTPException(status_code=400, detail="Failed to fetch exchange rate")

    latest_alert.last_checked_rate = current_rate
    latest_alert.last_checked_at = datetime.now(timezone.utc)

    triggered = (
        (latest_alert.direction == "above" and current_rate >= latest_alert.alert_target)
        or
        (latest_alert.direction == "below" and current_rate <= latest_alert.alert_target)
    )

    if triggered:
        latest_alert.is_active = False
        latest_alert.is_triggered = True
        latest_alert.triggered_at = datetime.now(timezone.utc)
    elif latest_alert.is_active:
        latest_alert.is_triggered = False
        latest_alert.triggered_at = None

    db.commit()
    db.refresh(latest_alert)
    return latest_alert

@router.get("/me", response_model = list[AlertResponse])
def fetch_alert(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):
    alerts = db.query(Alert).filter(Alert.user_id == user_id).all()

    return alerts




@router.delete("/{alert_id}")
def delete_alert(alert_id: UUID, user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):
    alert = db.query(Alert).filter(Alert.id == alert_id, Alert.user_id == user_id).first()

    if not alert: 
        raise HTTPException(status_code=404, detail = "Alert not found")
    
    db.delete(alert)
    db.commit()

    return {"message": "delete succesful", "alert": f"{alert}"}

@router.delete("/me")
def delete_all_alert(user_id: UUID = Depends(get_current_user_id), db: Session = Depends(get_db)):

    alerts = db.query(Alert).filter(Alert.user_id == user_id).all()

    if not alerts: 
        raise HTTPException(status_code=404, detail= "Alerts not found")
    
    for alert in alerts: 
        db.delete(alert)
    db.commit()

    return {"message": "Succesfully delete all alerts"}



def refresh_active_alerts():
    db: Session = SessionLocal()

    try:
        active_alerts = db.query(Alert).filter(Alert.is_active == True).all()

        for alert in active_alerts:
            try:
                result = target_rate(alert.base_currency, alert.target_currency)
                current_rate = result["rate"]

                alert.last_checked_rate = current_rate
                alert.last_checked_at = datetime.now(timezone.utc)

                if alert.direction == "above" and current_rate >= alert.alert_target:
                    alert.is_triggered = True 
                    alert.triggered_at = datetime.now(timezone.utc)
                    alert.is_active = False
                elif alert.direction == "below" and current_rate <= alert.alert_target:
                    alert.is_triggered = True 
                    alert.triggered_at = datetime.now(timezone.utc)
                    alert.is_active = False 

            except Exception as e:
                print(f"Failed refreshing alert {alert.id}: {e}")

        db.commit()

    except Exception as e:
        db.rollback()
        print(f"Scheduler failed: {e}")

    finally:
        db.close()
