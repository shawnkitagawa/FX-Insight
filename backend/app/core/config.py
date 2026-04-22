from dotenv import load_dotenv
from supabase import create_client, Client 
import os 
from pathlib import Path
from openai import OpenAI



BASE_DIR = Path(__file__).resolve().parent.parent.parent
env_path = BASE_DIR/ ".env"
load_dotenv(env_path)

SUPABASE_URL = os.getenv("SUPABASE_URL").strip()
SUPABASE_SECRET_KEY= os.getenv("SECRET_KEY").strip()
API_KEY = os.getenv("API_KEY").strip()
DB_PASSWORD = os.getenv("DB_PASSWORD").strip()

if not SUPABASE_URL:
    raise ValueError("SUPABASE_URL is missing")

if not SUPABASE_SECRET_KEY:
    raise ValueError("API_KEY is missing")

supabase: Client = create_client(SUPABASE_URL, SUPABASE_SECRET_KEY)

#------------------------------------- OPENAI config

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY").strip()

if not OPENAI_API_KEY : 
    raise ValueError("OPENAI_API_KEY is missing")

MODEL ="gpt-4o-mini"

openai_client = OpenAI(api_key= OPENAI_API_KEY)



