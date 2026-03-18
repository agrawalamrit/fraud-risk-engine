# 💳 Fraud Risk Engine

A real-time transaction monitoring system built with **Java** and **Spring Boot**. This application evaluates incoming financial transactions against multiple risk vectors to detect potential fraud before processing.

## Live Demo
**Link:** [https://fraud-risk-engine-wc6o.onrender.com](https://fraud-risk-engine-wc6o.onrender.com)  
*(Note: Free tier hosting may take ~40s to wake up on initial load)*

## Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Security (Session Management)
- **Frontend:** HTML5, CSS3 (Flexbox/Grid), Vanilla JavaScript
- **Deployment:** Docker, Render (PaaS)
- **Build Tool:** Maven

## Risk Analysis Logic
The engine evaluates every transaction against 8 distinct rules to calculate a risk score (0-100):
1. **Amount Deviation:** Flags spikes compared to 10x average spend.
2. **Historical Max:** Triggers if a transaction exceeds the user's highest previous spend.
3. **Transaction Velocity:** Detects rapid card-testing (multiple attempts < 5s).
4. **Daily Volume:** Limits total transactions per session to prevent automated attacks.
5. **New Device Detection:** Penalizes attempts from unrecognized hardware.
6. **Odd-Hour Analysis:** Flags transactions occurring between 12 AM and 6 AM.
7. **Location Analysis:** Increases risk score for international transactions.
8. **Merchant Risk:** Scans for high-cashout categories like Crypto, Gaming, and Jewelry.

## Testing the Engine
Use the **"Try Demo Scenarios"** dropdown in the dashboard to instantly simulate:
- **Scenario A:** A standard, low-risk approved purchase.
- **Scenario B:** A high-risk stolen card attempt (Declined).
- **Scenario C:** A rapid card-testing velocity attack.

## Local Setup
1. Clone the repository: `git clone https://github.com/agrawalamrit/fraud-risk-engine.git`
2. Ensure you have **JDK 17** installed.
3. Run with Maven: `./mvnw spring-boot:run`
4. Open `http://localhost:8081` in your browser.