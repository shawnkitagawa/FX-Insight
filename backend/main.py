from fastapi import FastAPI
from app.routers.currency_router import router as currency_router
from app.routers.alert import router as alert_router 
from app.routers.favorite import router as favorite_router 
from app.routers.profile import router as profile_router
from app.routers.history import router as history_router 


app = FastAPI()

app.include_router(currency_router)
app.include_router(favorite_router)
app.include_router(profile_router)
app.include_router(history_router)
app.include_router(alert_router)



@app.get("/")
def home(): 
    return {"message": "Hello World"}