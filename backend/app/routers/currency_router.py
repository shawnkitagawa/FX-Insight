from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from pydantic import BaseModel
from app.services.currency_service import(
weekly_trend,
daily_change,
get_currency_information,
available_currency, 
one_month_market,
one_year_market, 
six_month_market,
available_currency)
app = FastAPI()

router = APIRouter(prefix = "/currency", tags = ["currency"] )

AVAILABLE_CURRENCY = available_currency()


class AlertStatus(str, Enum): 
    ABOVE = "above"
    BELOW = "below"

class TimeGroup(str, Enum): 
    DAY = "day"
    WEEK = "week"
    MONTH = "month"


class AlertCreate(BaseModel): 
    base: str
    target: str
    target_rate: float 
    direction: AlertStatus




# get what kind of available currency 
@router.get("")
def currency_types()-> list: 
    data = available_currency()


    if not data: 
        raise HTTPException(status_code=404, detail = "Failed to fetch available currency types")

    return data 

@router.get("/{base}")
def currency_information(base: str)-> dict: 
    base = base.upper()
    if base not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail= "currency does not exist")
    
    data = get_currency_information(base)

    if not data: 
        raise HTTPException(status_code=404, detail="Failed to Fetch currency information")

    return data



@router.get("/{base}/{target}/week/")
def weekly_statistics(base: str, target: str) -> dict:

    target = target.upper()
    base = base.upper()
    if target not in AVAILABLE_CURRENCY or base not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail = "Currency does not exist")

    data = weekly_trend(base, target)
    if data is None: 
        raise HTTPException(status_code=404, detail= "Failed to fetch weekly statistics")
    return data 


@router.get("/{base}/{target}/day")
def daily_change_data(base: str, target: str) -> dict:

    target = target.upper()
    base = base.upper()

    if target not in AVAILABLE_CURRENCY or base not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail = "Currency does not exist")


    try: 

        data = daily_change(base, target)
        if data is None: 
         raise HTTPException(status_code=404, detail="Failed to fetch daily change analysis")

    except HTTPException: 
        raise

    except Exception: 
        raise HTTPException(status_code=502, detail = "Failed to fetch daily change")
    
    return {"change": data}

# @router.get("/{base}/{target}/{alert}/{alertconfiguration}")
# def alert_notification(base: str, target: str, alert:float, alertconfiguration: AlertStatus) -> dict:
#     now = datetime.now(timezone.utc) 

#     data = alert_check(alert, base, target, alertconfiguration.value)

#     if not data: 
#         raise HTTPException(status_code=404, detail= "Failed to check alert" )
    
#     return data 

@router.get("/{base}/{target}/market")
def historical_graph(base: str, target: str,time_group: TimeGroup): 

    target = target.upper()
    base = base.upper()

    if target not in AVAILABLE_CURRENCY or base not in AVAILABLE_CURRENCY: 
        raise HTTPException(status_code=400, detail = "Currency does not exist")


    if time_group == TimeGroup.DAY: 
        data = one_month_market(base, target)
    elif time_group == TimeGroup.WEEK: 
        data = six_month_market(base, target)
    else: 
        data = one_year_market(base, target)

    if not data:
        raise HTTPException(status_code=404, detail = "Failed to fetch market")
    
    return data

