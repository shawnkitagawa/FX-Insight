from fastapi import FastAPI
from app.routers.currency_router import router as currency_router



app = FastAPI()

app.include_router(currency_router)


@app.get("/")
def home(): 
    return {"message": "Hello World"}