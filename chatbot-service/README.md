# 🤖 Hotel Recommendation Chatbot Service

AI-powered hotel recommendation chatbot using LangChain, Hugging Face, and RAG (Retrieval-Augmented Generation).

## 🎯 Features

- **Natural Language Understanding**: Chat naturally about hotels and travel
- **RAG-based Responses**: Grounded answers using hotel database
- **Smart Recommendations**: Based on location, price, rating, and amenities
- **Quick Actions**: Pre-defined queries for common requests
- **Real-time Chat**: WebSocket-ready Flask API
- **Vector Search**: ChromaDB for semantic similarity matching

## 🏗️ Architecture

```
User Query → Flask API → LangChain RAG Chain
                ↓
          Vector Store (Chroma)
                ↓
          Retrieval (Top 3 hotels)
                ↓
          LLM (Flan-T5) + Prompt
                ↓
          Generated Response
```

## 📦 Installation & Running

### 🛠️ Development Mode (Run Locally - Recommended for Development)

#### Prerequisites
- Python 3.10+
- Node.js 18+
- Java 17
- MySQL 8.0
- PostgreSQL (for Keycloak)

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
python -m venv .venv

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

#### Run Specific Services
```powershell
# Backend only
docker compose up backend mysql -d

# Frontend only
docker compose up frontend -d

# Face recognition only
docker compose up facerecognition -d

# Chatbot only
docker compose up chatbot -d

# All Python services
docker compose up facerecognition chatbot -d
```

#### View Logs
```powershell
# All services
docker compose logs -f

# Specific service
docker compose logs -f chatbot
docker compose logs -f backend
docker compose logs -f frontend
```

#### Rebuild After Code Changes
```powershell
# Rebuild specific service
docker compose up --build chatbot

# Rebuild without cache (if CACHED layers don't detect changes)
docker compose build --no-cache chatbot
docker compose up chatbot
```

---

### 🌐 Service URLs

| Service | Development | Docker/Production |
|---------|------------|-------------------|
| **Frontend** | http://localhost:4200 | http://localhost:4200 |
| **Backend** | http://localhost:8082 | http://localhost:8082 |
| **Face Recognition** | http://localhost:5000 | http://localhost:5000 |
| **Chatbot** | http://localhost:5001 | http://localhost:5001 |
| **MySQL** | localhost:3307 | localhost:3307 |
| **phpMyAdmin** | - | http://localhost:8080 |
| **Keycloak** | - | http://localhost:9090 |

---

### 🔧 Chatbot Service Only (This Directory)

#### Local Development
```bash
cd chatbot-service

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Linux/Mac:
source venv/bin/activate
# Windows PowerShell:
.\venv\Scripts\Activate.ps1
# Windows CMD:
venv\Scripts\activate.bat

# Install dependencies
pip install -r requirements.txt

# Run service
python app.py
```

#### Docker (Standalone)
```bash
# Build
docker build -t hotel-chatbot .

# Run
docker run -p 5001:5001 hotel-chatbot
```

#### Docker Compose (From Project Root)
```bash
# From project root
docker compose up chatbot
```

## 🔌 API Endpoints

### 1. Health Check
```bash
GET /health

Response:
{
  "status": "healthy",
  "service": "Hotel Chatbot",
  "timestamp": "2025-12-01T10:30:00"
}
```

### 2. Chat Message
```bash
POST /chat
Content-Type: application/json

{
  "message": "Recommend hotels in Paris under $200"
}

Response:
{
  "response": "Based on our database, I recommend Hotel Le Marais...",
  "confidence": 0.85,
  "timestamp": "2025-12-01T10:30:00"
}
```

### 3. Get Recommendations
```bash
POST /recommend
Content-Type: application/json

{
  "location": "Paris",
  "max_price": 200,
  "min_rating": 4.0
}

Response:
{
  "response": "Here are the best hotels matching your criteria...",
  "query": "Recommend hotels in Paris under $200 per night with rating above 4.0",
  "timestamp": "2025-12-01T10:30:00"
}
```

### 4. Get Destinations
```bash
GET /destinations

Response:
{
  "destinations": ["Paris", "Bali", "Rome", "London", "Tokyo"],
  "count": 5
}
```

## 📊 Hotel Data Format

The chatbot uses `hotels.csv` with the following structure:

```csv
name,location,description,price,rating,amenities,image_url
Hotel Le Marais,Paris,"Charming boutique hotel...",180,4.5,"WiFi|Breakfast|AC",url
```

**Columns:**
- `name`: Hotel name
- `location`: City/destination
- `description`: Detailed description
- `price`: Price per night (USD)
- `rating`: Rating out of 5.0
- `amenities`: Pipe-separated list
- `image_url`: Hotel image URL

## 🧠 Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------||
| **API Framework** | Flask 3.0 | REST API server |
| **LLM** | Flan-T5 (Hugging Face) | Text generation |
| **Embeddings** | all-MiniLM-L6-v2 | Semantic search |
| **Vector Store** | FAISS (Facebook AI) | Document storage & similarity search |
| **Framework** | LangChain | RAG orchestration |
| **Agent Tools** | Wikipedia | Travel information |

## 🎨 Frontend Integration

The chatbot is integrated into the Angular frontend as a floating chat widget:

```typescript
// In home.component.html
<app-chatbot></app-chatbot>
```

**Features:**
- ✅ Floating button (bottom-right)
- ✅ Minimizable chat window
- ✅ Real-time typing indicator
- ✅ Quick action buttons
- ✅ Message history
- ✅ Responsive design

## 🔧 Configuration

### Environment Variables

```bash
PORT=5001                    # API port
TAVILY_API_KEY=your_key     # Optional: For web search
```

### Customization

**1. Change LLM Model:**
```python
# In app.py
llm = HuggingFacePipeline.from_model_id(
    model_id="google/flan-t5-large",  # Larger model
    # or
    model_id="microsoft/DialoGPT-medium",  # Chat-focused
)
```

**2. Adjust Retrieval:**
```python
retriever = vectorstore.as_retriever(
    search_kwargs={"k": 5}  # Retrieve top 5 (instead of 3)
)
```

**3. Modify Prompt:**
```python
prompt_template = """You are a luxury hotel expert.
Use formal language and emphasize premium features.

Context: {context}
Question: {question}
Answer:"""
```

## 📝 Example Queries

```
✅ "Recommend hotels in Paris"
✅ "What's the cheapest hotel in Bali?"
✅ "Show me luxury hotels in Rome"
✅ "Hotels near Eiffel Tower under $250"
✅ "Best beach resorts in Bali"
✅ "What amenities does Hotel Le Marais have?"
✅ "Tell me about Tokyo accommodations"
```

## 🚀 Performance

- **Response Time**: ~2-3 seconds (first query), ~1 second (subsequent)
- **Model Size**: ~250MB (Flan-T5-base)
- **Memory Usage**: ~1GB RAM
- **Concurrent Users**: 10-20 (single instance)

## 🐛 Troubleshooting

### Issue: "Model download failed"
**Solution:** Pre-download models:
```python
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
AutoTokenizer.from_pretrained("google/flan-t5-base")
AutoModelForSeq2SeqLM.from_pretrained("google/flan-t5-base")
```

### Issue: "FAISS initialization error"
**Solution:** Clear index:
```bash
rm -rf faiss_index/  # Linux/Mac
# Or
Remove-Item -Recurse -Force faiss_index  # PowerShell
python app.py  # Re-initializes
```

### Issue: "Slow responses"
**Solutions:**
1. Use lighter model: `flan-t5-small`
2. Reduce chunk size in `RecursiveCharacterTextSplitter`
3. Lower retrieval count (`k=2`)

## 📈 Future Enhancements

- [ ] **Multi-language support** (French, Spanish, German)
- [ ] **User preferences** (Remember past searches)
- [ ] **Real-time pricing** (API integration with Booking.com)
- [ ] **Image generation** (Hotel room previews)
- [ ] **Voice input** (Speech-to-text)
- [ ] **Sentiment analysis** (Detect urgency/preferences)
- [ ] **A/B testing** (Different prompts/models)

## 🤝 Contributing

1. Add more hotels to `hotels.csv`
2. Improve prompts in `app.py`
3. Add new tools (e.g., flight search, weather)
4. Optimize model selection

## 📄 License

MIT License - Use freely in your projects!

## 🙋 Support

For issues or questions:
- Check logs: `docker logs hotel-chatbot`
- Test health endpoint: `curl http://localhost:5001/health`
- Review Flask logs in terminal

---

**Built with ❤️ using LangChain and Hugging Face**
