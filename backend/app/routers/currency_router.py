from fastapi import FastAPI, APIRouter, HTTPException
from enum import Enum
from datetime import datetime , timezone
from pydantic import BaseModel, ValidationError
from app.services.currency_service import(
weekly_trend,
alert_check,
daily_change,
currency_exchange,
get_currency_information,
available_currency, 
one_month_market,
one_year_market, 
six_month_market)
app = FastAPI()

router = APIRouter(prefix = "/currency", tags = ["currency"] )


class AlertStatus(str, Enum): 
    ABOVE = "above"
    BELOW = "below"

class TimeGroup(str, Enum): 
    DAY = "day"
    WEEK = "week"
    MONTH = "month"
    YEAR = "year"


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
    data = get_currency_information(base)

    if not data: 
        raise HTTPException(status_code=404, detail="Failed to Fetch currency information")

    return data




# currency exchange 
@router.get("/{base}/{target}/{target_amount}")
def convert_currency(base: str, target: str, target_amount: float)-> dict: 
    data = currency_exchange(base, target,target_amount)

    if data is None: 
        raise HTTPException(status_code=404, detail = "Failed to fetch currency exchange")
    
    return data 

@router.get("/{base}/{target}/week/")
def weekly_statistics(base: str, target: str) -> dict:
    data = weekly_trend(base, target)
    if data is None: 
        raise HTTPException(status_code=404, detail= "Failed to fetch weekly statistics")
    return data 


@router.get("/{base}/{target}/day")
def daily_change_data(base: str, target: str) -> dict:
    data = daily_change(base, target)

    if data is None: 
        raise HTTPException(status_code=404, detail="Failed to fetch daily change analysis")
    
    return {"change": data}

# Still working on it 
@router.get("/{base}/{target}/{alert}/{alertconfiguration}")
def alert_notification(base: str, target: str, alert:float, alertconfiguration: AlertStatus) -> dict:
    now = datetime.now(timezone.utc) 

    data = alert_check(alert, base, target, alertconfiguration.value)

    if not data: 
        raise HTTPException(status_code=404, detail= "Failed to check alert" )
    
    return data 



@router.get("/{base}/{target}/market")
def historical_graph(base: str, target: str,time_group): 


# # Still working on it F
# @router.post("/alerts")
# def create_alert(alert:AlertCreate):
#     save_to_db(alert) 
#     return {"message":"Alert created"}







