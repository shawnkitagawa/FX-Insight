import requests
from app.services.currency_service import weekly_trend, daily_change
from app.core.config import openai_client, MODEL

def ai_insight(base: str, target: str ) -> str:
    
    try: 

        stats = weekly_trend(base, target)
        change = daily_change(base, target)


        prompt = f""" 

        You are a financial market analyst.

        Currency Pair: {base}/{target}


        Weekly Trend: {stats["trend"]}
        Weekly Change Percent: {stats["change_percent"]}
        Weekly Average : {stats["average"]}
        Weekly Median: {stats["median"]}
        Weekly Min: {stats["min"]}
        Weekly Max: {stats["max"]}

        Daily Change: {change}%

        Instructions:
        - Describe the market direction using natural language (e.g., bullish, bearish, sideways)
        - Mention strength of the trend (weak, moderate, strong)
        - Describe volatility (stable vs volatile based on price range)
        - Add a simple interpretation (what this means for the market)
        - Do NOT list raw numbers
        - Keep it concise and human-friendly

        Output: 
        Write 2 short, clear sentences.
        
        """

        response = openai_client.responses.create(
            model = MODEL, 
            input = prompt
        )

        insight_text = response.output[0].content[0].text


        print(insight_text) 

    except Exception as e: 
        print(f"AI ERROR: {e}")
        return f"{base}/{target} is showing a {stats['trend'].lower()} trend this week."
        
    return insight_text