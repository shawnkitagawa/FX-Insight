from dotenv import load_dotenv
from supabase import create_client, Client 
import os 
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent.parent.parent
env_path = BASE_DIR/ ".env"
load_dotenv(env_path)

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_SECRET_KEY= os.getenv("SECRET_KEY")

supabase: Client = create_client(SUPABASE_URL, SUPABASE_SECRET_KEY)