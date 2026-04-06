from pydantic import BaseModel 
from uuid import UUID
from datetime import datetime 
from enum import Enum
from decimal import Decimal


class DirectionStatus(str, Enum):
    ABOVE ="above"
    BELOW ="below"

class ProfileCreate(BaseModel): 
    user_id: UUID
    user_name: str

class ProfileResponse(BaseModel): 
    user_id: UUID
    user_name: str

    class Config: 
        from_attributes = True  


class FavoriteCreate(BaseModel): 
    user_id: UUID
    base_currency: str
    target_currency: str 


class FavoriteResponse(BaseModel): 
    id: UUID
    user_id: UUID
    base_currency: str
    target_currency: str
    created_at: datetime
    
    class Config: 
        from_attributes = True 

class HistoryCreate(BaseModel): 
    user_id: UUID
    base_currency: str
    target_currency:str 
    base_amount: Decimal
    converted_amount: Decimal 
    rate: Decimal
    

class HistoryResponse(BaseModel): 
    id: UUID
    user_id: UUID
    base_currency: str
    target_currency:str 
    base_amount: Decimal
    converted_amount: Decimal 
    rate: Decimal
    created_at:datetime

    class Config: 
        from_attributes = True 

class AlertCreate(BaseModel): 
    user_id: UUID
    alert_target: Decimal
    direction: DirectionStatus
    base_currency: str
    target_currency: str
    is_active: bool = True



class AlertResponse(BaseModel): 
    id: UUID
    user_id: UUID
    alert_target:Decimal
    direction: DirectionStatus
    last_checked_rate: Decimal
    last_checked_at: datetime
    base_currency: str
    target_currency: str
    is_active: bool
    created_at: datetime

    class Config: 
        from_attributes = True 





