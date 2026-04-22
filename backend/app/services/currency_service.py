import requests
from datetime import date
from dateutil.relativedelta import relativedelta
import sys


sys.stdout.reconfigure(encoding='utf-8')
from decimal import Decimal

  # BASE layer START ----------------------------------

def available_currency(): 
    url = "https://api.frankfurter.dev/v2/currencies"
    response = requests.get(url)
    data = response.json()
    currency = set()
    for i in data: 
        currency.add(i["iso_code"])


    return currency

print(available_currency())



def target_rate(base : str, target : str): 

    base = base.strip().upper()
    target = target.strip().upper()



    # Where USD is the base currency you want to use
    url = f"https://api.frankfurter.dev/v2/rate/{base}/{target}"
    
        # Making our request
    response = requests.get(url)
    data = response.json()

    # Your JSON object
    print(data)

    return data 

print("+++++++++++++++++++++++++++++++")
target_rate("USD", "JPY")



def get_currency_information(currency: str): 
    currency = currency.strip().upper()
    url = f"https://api.frankfurter.dev/v2/currency/{currency}"

    response = requests.get(url) 
    data = response.json()


    print(data)
    return data

# get_currency_information("USD")


def time_range_search(time_from, time_to, base, target, group = None):
    base = base.strip().upper()
    target = target.strip().upper()

    url = (
        f"https://api.frankfurter.dev/v2/rates"
        f"?base={base}"
        f"&quotes={target}"
        f"&from={time_from}"
        f"&to={time_to}"
    )

    if group and group in ["month","year", "week"]: 
        url += f"&group={group}"
    
    response = requests.get(url)
    data = response.json()



    print(data)
    return data


def get_specific_rate_by_date(date, base, target): 
    base = base.strip().upper()
    target = target.strip().upper()
    url =  (f"https://api.frankfurter.dev/v2/rates"
            f"?base={base}"
            f"&quotes={target}"
            f"&date={date}")
    
    response = requests.get(url)

    data = response.json()
    print(data)

    return data 

# get_specific_rate_by_date("2024-01-01","USD", "JPY")

# time_range_search("2024-01-01", "2025-01-01", "USD", "JPY", "month")

# BASE layer END -------------------------------------------------------------------------------------------------------------

# func for one month WORK
def one_month_market(base, target):
    today = date.today()
    one_month_ago = today - relativedelta(months= 1)

    return time_range_search(one_month_ago.strftime("%Y-%m-%d"), today.strftime("%Y-%m-%d"), base, target)

print("-----------------------------------------")
print(one_month_market("USD", "JPY"))

# func for 6 month WORK
def six_month_market(base, target):
    today = date.today()
    six_month_ago = today - relativedelta(months= 6 )

    return time_range_search(six_month_ago.strftime("%Y-%m-%d"), today.strftime("%Y-%m-%d"), base, target, group = "week")

# func for one year WORK
def one_year_market(base, target):
    today = date.today()
    one_year_ago = today - relativedelta(years= 1 )

    return time_range_search(one_year_ago, today, base, target, "month")

# print(one_year_market("USD", "JPY"))


# func that displays the currency rate , result caluclation and the currency sykbol WORK
def currency_exchange(base, target, base_amount): 
    currency_exchange = {}

    # get daily exchange returns a dictionary of    date    base   quote   rate 
    daily_exchange = target_rate(base, target)
    print(f"the daily exchange is {daily_exchange}")
    currency_exchange = daily_exchange

    daily_exchange = Decimal(daily_exchange["rate"])
    target_total = daily_exchange * base_amount 

    base_information = get_currency_information(base) 
    target_information = get_currency_information(target)

    # add iso code , name, and symbol for base and target in dictionary 
    currency_exchange["target_total"] = target_total
    currency_exchange["base_name"] = base_information["name"]
    currency_exchange["base_symbol"] = base_information["symbol"]
    currency_exchange["base_iso_code"] = base_information["iso_code"]
    currency_exchange["target_name"] = target_information["name"]
    currency_exchange["target_symbol"] = target_information["symbol"]
    currency_exchange["target_iso_code"] = target_information["iso_code"]

    # add 24 hour change 

    # add trend 

    # Alert 
    print(f"curency_exchange{ currency_exchange}")
    return currency_exchange




def daily_change(base, target): 
    today = date.today()
    one_day_ago = today - relativedelta(days=1)
    two_days_ago = today - relativedelta(days=2)
    yesterday_data = get_specific_rate_by_date(one_day_ago,base, target)[0]["rate"]
    day_before_yesterday_data = get_specific_rate_by_date(two_days_ago,base, target)[0]["rate"]


    current_change = float(round(((float(yesterday_data) - float(day_before_yesterday_data))/ float(day_before_yesterday_data)) * 100,2 ))
    return current_change

print(daily_change("BRL", "JPY"))


def weekly_trend(base, target):
    today = date.today()
    seven_days_ago = today - relativedelta(weeks=1)

    past_week_data = get_specific_rate_by_date(
        seven_days_ago.strftime("%Y-%m-%d"), base, target
    )
    current_data = target_rate(base, target)

    range_search_data = time_range_search(
        seven_days_ago.strftime("%Y-%m-%d"),
        today.strftime("%Y-%m-%d"),
        base,
        target
    )

    past_week_rate = float(past_week_data[0]["rate"])
    current_rate = float(current_data["rate"])

    change = round(((current_rate - past_week_rate) / past_week_rate) * 100, 2)

    rate_list = [float(r["rate"]) for r in range_search_data]

    total = sum(rate_list)
    avg = total / len(rate_list)

    sorted_rates = sorted(rate_list)
    n = len(sorted_rates)

    if n % 2 == 0:
        median = (sorted_rates[n // 2 - 1] + sorted_rates[n // 2]) / 2
    else:
        median = sorted_rates[n // 2]

    min_rate = min(rate_list)
    max_rate = max(rate_list)

    trend = "UP" if change > 0 else "DOWN" if change < 0 else "STABLE"

    return {
        "trend": trend,
        "change_percent": change,
        "average": avg,
        "median": median,
        "min": min_rate,
        "max": max_rate
    }


def alert_check(alert_target, base, target, alert_configuratoin): 
    today = date.today()
    current_data = target_rate(base, target)



    if alert_configuratoin == "above" and current_data["rate"] >= alert_target:
        message =  {"message": "Above the Target Price"}
    elif alert_configuratoin == "below" and current_data["rate"] <= alert_target:
        message = {"message": "below the Target Price"}


    return message


# fun that stores the Alert require run every day every 1:00 to 2:00 








