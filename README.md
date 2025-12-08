# Hotel Management System

dev runing commands 
docker compose up -d db postgres keycloak phpmyadmin
cd backend 
mvn clean compile
mvn spring-boot:run

cd frontend
npm start 
cd face-recognition-service
.\.venv\Scripts\Activate.ps1
python app.py

cd chatbot-service
.\venv\Scripts\Activate.ps1
python app.py

phpadmin page:
hotel_user
hotel_pass


prod 
.env
profile=prod
docker compose up -d 
> Full-stack hotel booking application with Keycloak authentication and face recognition

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20.1.7-red.svg)](https://angular.io/)
[![Keycloak](https://img.shields.io/badge/Keycloak-25.0.5-blue.svg)](https://www.keycloak.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://docs.docker.com/compose/)

## Quick Start development (Local Setup)
docker compose up -d db postgres keycloak phpmyadmin
#### 1. **Backend (Spring Boot)**
```powershell
cd hotel-backend

# Build
.\mvnw clean package

# Run
.\mvnw spring-boot:run

# Or run JAR directly
java -jar target\hotel-1.0.0.jar

# Backend will run on: http://localhost:8082
```

#### 2. **Frontend (Angular)**
```powershell
cd hotel-frontend

# Install dependencies (first time only)
npm install

# Run development server
npm start
# Or
ng serve

# Frontend will run on: http://localhost:4200
```

#### 3. **Face Recognition Service (Python/Flask)**
```powershell
cd face-recognition-service

# Create virtual environment (first time only)
python -m venv .venv  3.13.0
C:\Users\LENOVO\.pyenv\pyenv-win\versions\3.11.0\python.exe -m venv venv
3.11.0
pyenv local 3.11.0
# Activate virtual environment
.\.venv\Scripts\Activate.ps1  # PowerShell
# Or: .venv\Scripts\activate.bat  # CMD

# Install dependencies (first time only)
pip install -r requirements.txt

# Run service
python app.py

# Service will run on: http://localhost:5000
```

#### 4. **Chatbot Service (Python/Flask)**
```powershell
cd chatbot-service

# Create virtual environment (first time only)
python -m venv venv

# Activate virtual environment
.\venv\Scripts\Activate.ps1  # PowerShell
# Or: venv\Scripts\activate.bat  # CMD

# Install dependencies (first time only)
pip install -r requirements.txt

# Run service
python app.py

# Service will run on: http://localhost:5001
```

#### 5. **MySQL Database**
```powershell
# Option 1: Use Docker for MySQL only
docker compose up mysql phpmyadmin -d

# Option 2: Install MySQL locally
# Create database:
mysql -u root -p
CREATE DATABASE hoteldb;
```

#### 6. **Keycloak (Optional - for Authentication)**
```powershell
# Use Docker for Keycloak
docker compose up keycloak postgres -d

# Access Keycloak: http://localhost:9090
# Username: admin, Password: admin
```

---

### 🐳 Production Mode (Docker Compose - All Services)

#### Run All Services Together
```powershell
# From project root
cd hotel

# Build and run all services
docker compose up --build

# Or run in background
docker compose up -d --build

# Stop all services
docker compose down

# Stop and remove volumes (clean database)
docker compose down -v
```


```powershell
# Clone and start
git clone https://github.com/nourzakhama2003/hotel.git
cd hotel
docker compose up -d

# Access
# Frontend:   http://localhost:4200
# Backend:    http://localhost:8083/api
# Keycloak:   http://localhost:8080
```

## Default Login

- **Admin:** `admin` / `nour123`
- **User:** `testuser` / `test123`

## Features

✅ Hotel & Room Management  
✅ Online Booking System  
✅ Keycloak OAuth2 Authentication  
✅ Face Recognition Login  
✅ Stripe Payment Integration  
✅ Email Verification  
✅ Docker Deployment  
✅ CI/CD Pipeline (GitHub Actions)

## Documentation

📖 **[Complete Documentation](./DOCUMENTATION.md)** - Setup, configuration, troubleshooting, and deployment guide

## Tech Stack

- **Backend:** Spring Boot 3.3.0, MySQL 8.0, OpenCV 4.9.0
- **Frontend:** Angular 20.1.7, TypeScript, Tailwind CSS
- **Auth:** Keycloak 25.0.5, OAuth2, JWT
- **DevOps:** Docker, Nginx, GitHub Actions

## Project Structure

```
hotel/
├── hotel-backend/          # Spring Boot API
├── hotel-frontend/         # Angular SPA
├── keycloak/               # Realm configurations
├── docker/                 # Dockerfiles
├── .env                    # Environment config
├── docker-compose.yml      # Service orchestration
└── DOCUMENTATION.md        # Complete guide
```

## License

MIT License - see LICENSE file for details

## Author

**Nour Zakhama**  
GitHub: [@nourzakhama2003](https://github.com/nourzakhama2003)
