from fastapi import FastAPI
from app.routers.currency_router import router as currency_router
from app.routers.alert import router as alert_router , refresh_active_alerts
from app.routers.favorite import router as favorite_router 
from app.routers.profile import router as profile_router
from app.routers.history import router as history_router 
import asyncio 
from contextlib import asynccontextmanager


@asynccontextmanager
async def lifespan(app): 
    async def scheduler_loop(): 
        while True: 
            refresh_active_alerts()
            await asyncio.sleep(60 * 60 * 3) 
    task = asyncio.create_task(scheduler_loop())
    yield
    task.cancel()
            


app = FastAPI(lifespan=lifespan)

app.include_router(currency_router)
app.include_router(favorite_router)
app.include_router(profile_router)
app.include_router(history_router)
app.include_router(alert_router)



@app.get("/")
def home(): 
    return {"message": "Hello World"}


