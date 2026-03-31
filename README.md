#  FX Insight — Currency Converter + Analysis App

FX Insight is a modern Android finance app that allows users to convert currencies in real time, track personal conversion history, and understand short-term exchange trends through rule-based and AI-enhanced insights.

Built with a focus on **clean architecture, real-world data handling, and production-ready UX**.

---


## Screenshots



- Login Screen  
- Converter Dashboard  
- Result + Analysis Card  
- History / Favorites Section  

---

##  Features

###  Currency Conversion
- Real-time exchange rate conversion  
- Swap currencies instantly  
- Live result updates  
- Rate timestamp display  

###  Analysis Engine
- Daily & 7-day percentage change  
- High / Low range  
- Trend direction (Upward / Downward / Stable)  
- Rule-based explanation  

###  AI Insight (Lightweight)
- Short AI-generated summary  
- Based on calculated data (not raw prompts)  
- No financial advice or prediction  

###  History Tracking
- Save recent conversions (Supabase)  
- Tap to reuse past conversions  
- Delete / clear history  

###  Favorites
- Save frequently used currency pairs  
- Quick access for fast conversions  

###  Authentication
- User signup and login  
- Persistent session  
- User-specific data  

### State Handling
- Loading / Error / Empty states  
- Input validation  
- Retry support  

---

###  Architecture
![Architecture](image/Fx-Insight-System-Architecture.png)

###  System Flow 

 ## Sign Up
  ![System Design](image/Authentication-Flow-SignUP-2026-03-30-015121.png)
  
 ## Sign In
  ![System Design](image/Authentication-Flow-Sign-in-2026-03-30-012727.png)

 ## DataFlow & Caching Strategy 
  ![Data Flow](image/Data-Flow-&-Caching-Strategy-2026-03-30-084233.png)


### Tech Stack
- Kotlin  
- Jetpack Compose  
- ViewModel + StateFlow  
- Coroutines  
- Retrofit / Ktor  
- Supabase (Auth + Database)  

---

##  Database (Supabase)

### profiles
- user_id  
- username  
- created_at  

### conversion_history
- id  
- user_id  
- amount  
- base_currency  
- target_currency  
- exchange_rate  
- converted_amount  
- created_at  

### favorite_pairs
- id  
- user_id  
- base_currency  
- target_currency  
- created_at  

---

##  Project Goals

This project demonstrates:

- Clean architecture design  
- Real-world API + database integration  
- State management with StateFlow  
- Transforming raw data into meaningful insights  
- Responsible AI integration (non-predictive, supportive only)  

---

##  Screens

- Auth (Login / Signup)  
- Converter Dashboard  
- (Optional) History / Favorites Detail  

---

##  Scope Control

### Included
- Currency conversion  
- History tracking  
- Favorites  
- Trend analysis  
- AI summary  

### Not Included
- Market prediction  
- Investment advice  
- Complex AI pipelines  
- Chat systems  

---

##  Key Learnings

- Handling async operations with coroutines  
- Managing UI state (Loading / Error / Success)  
- Designing scalable app architecture  
- Integrating Supabase with Android  
- Building user-focused financial tools  

---


##  Future Improvements

- Currency search dropdown  
- Flag icons  
- Pull-to-refresh   
- Dark mode polish  

---

## 👨‍💻 Author

Shawn Kitagawa  
