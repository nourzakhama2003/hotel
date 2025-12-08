# 🌐 External Search Integration Guide

## Overview

When the chatbot doesn't find relevant hotels in the local database (CSV), it automatically searches external sources to provide helpful information.

## How It Works

```
┌─────────────────────────────────────────────────────────────────┐
│                     User Asks About Hotels                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│            Step 1: Search Local Database (FAISS)                │
│  Example: User asks "Hotels in Rome"                            │
│  System searches: hotels.csv (Paris, Bali, Tokyo, Dubai, NY)    │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│            Step 2: Check Relevance                              │
│  - Found hotels? YES/NO                                         │
│  - Are they relevant to the query? YES/NO                       │
│  - Do keywords match? (location, amenities)                     │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                    ┌─────────────────┐
                    │  Are Hotels     │
                    │  Relevant?      │
                    └─────────────────┘
                      ↙             ↘
                   YES              NO
                    ↓                ↓
         ┌──────────────────┐  ┌──────────────────────────┐
         │ Return Hotels    │  │ EXTERNAL SEARCH          │
         │ from Database    │  │ (Wikipedia + Web)        │
         └──────────────────┘  └──────────────────────────┘
                                          ↓
                         ┌────────────────────────────────┐
                         │ Step 3: Extract Location       │
                         │ "Hotels in Rome" → "Rome"      │
                         └────────────────────────────────┘
                                          ↓
                         ┌────────────────────────────────┐
                         │ Step 4: Search Wikipedia       │
                         │ Query: "Rome tourism hotels"   │
                         │ Result: Destination info       │
                         └────────────────────────────────┘
                                          ↓
                         ┌────────────────────────────────┐
                         │ Step 5: Search Web             │
                         │ Query: "best hotels in Rome    │
                         │        booking.com"            │
                         │ Result: Hotel listings         │
                         └────────────────────────────────┘
                                          ↓
                         ┌────────────────────────────────┐
                         │ Step 6: Provide Direct Links   │
                         │ - Booking.com                  │
                         │ - TripAdvisor                  │
                         │ - Hotels.com                   │
                         └────────────────────────────────┘
                                          ↓
                         ┌────────────────────────────────┐
                         │ Return Response                │
                         │ - Text response                │
                         │ - hotels: []                   │
                         │ - external_search: true        │
                         │ - confidence: 0.5              │
                         └────────────────────────────────┘
```

---

## External Tools Used

### 1. **Wikipedia Search**

```python
from langchain_community.tools import WikipediaQueryRun
from langchain_community.utilities import WikipediaAPIWrapper

# Initialize tool
wikipedia_tool = WikipediaQueryRun(api_wrapper=WikipediaAPIWrapper())

# Search for destination info
result = wikipedia_tool.run("Rome tourism hotels")

# Returns:
# "Rome is the capital city of Italy... known for historic sites like the Colosseum..."
```

**Why Wikipedia?**
- ✅ Provides educational context about destination
- ✅ Free, no API key required
- ✅ Reliable information
- ✅ Helps users learn about location

---

### 2. **DuckDuckGo Web Search**

```python
from langchain_community.tools import DuckDuckGoSearchRun

# Initialize tool
web_search_tool = DuckDuckGoSearchRun()

# Search for hotels
result = web_search_tool.run("best hotels in Rome booking.com tripadvisor")

# Returns:
# "Top-rated hotels in Rome include Hotel Artemide, The St. Regis Rome..."
```

**Why DuckDuckGo?**
- ✅ Free, no API key required
- ✅ Privacy-focused (doesn't track users)
- ✅ Good search quality
- ✅ Instant results

---

### 3. **Direct Booking Links**

```python
# Generate booking site URLs
location = "Rome"

booking_url = f"https://www.booking.com/searchresults.html?ss={location.replace(' ', '+')}"
tripadvisor_url = f"https://www.tripadvisor.com/Search?q={location.replace(' ', '+')}+hotels"
hotels_url = f"https://www.hotels.com/search.do?q-destination={location.replace(' ', '+')}"
```

**Why Direct Links?**
- ✅ User can immediately book hotels
- ✅ No coding needed, just URL construction
- ✅ Works for any location
- ✅ Actionable next steps

---

## Code Walkthrough

### Location Extraction

```python
def extract_location(query):
    """
    Extract location from natural language query
    """
    # Keywords that precede locations
    location_keywords = ['in', 'at', 'near', 'around', 'for']
    
    query_lower = query.lower()
    
    # Method 1: Find location after keyword
    for keyword in location_keywords:
        if keyword in query_lower:
            parts = query_lower.split(keyword)
            if len(parts) > 1:
                # Get first word after keyword
                location_part = parts[1].strip().split()[0]
                if location_part:
                    return location_part.capitalize()
    
    # Method 2: Find capitalized words (likely proper nouns)
    words = query.split()
    for word in words:
        if word[0].isupper() and len(word) > 3:
            return word
    
    # Fallback
    return "your destination"

# Examples:
# extract_location("Hotels in Paris") → "Paris"
# extract_location("Best resorts near Bali") → "Bali"
# extract_location("Where to stay in Tokyo") → "Tokyo"
# extract_location("Rome accommodations") → "Rome"
```

---

### Relevance Check

```python
# Simple relevance check
is_relevant = False

if relevant_docs:
    query_lower = user_message.lower()
    
    for doc in relevant_docs:
        content_lower = doc.page_content.lower()
        
        # Check if query keywords appear in hotel data
        for word in query_lower.split():
            if len(word) > 3 and word in content_lower:
                is_relevant = True
                break
        
        if is_relevant:
            break

# Example:
# Query: "Hotels in Rome"
# Retrieved Hotel: "name: Eiffel Tower Hotel\nlocation: Paris"
# Check: "rome" in "eiffel tower hotel paris" → NO
# is_relevant = False → Trigger external search
```

**Why This Matters:**
- Prevents showing wrong hotels (e.g., Paris hotels when user asks for Rome)
- Improves user experience
- Avoids misleading information

---

### External Search Response

```python
# When no relevant hotels found in database
if not is_relevant:
    location = extract_location(user_message)
    
    # Wikipedia search
    wiki_info = ""
    try:
        wiki_result = wikipedia_tool.run(f"{location} tourism hotels")
        if wiki_result and len(wiki_result) > 50:
            wiki_info = f"\n\n**About {location}:**\n{wiki_result[:500]}..."
    except Exception as e:
        logger.warning(f"Wikipedia search failed: {e}")
    
    # Web search
    web_info = ""
    try:
        web_query = f"best hotels in {location} booking.com tripadvisor"
        web_result = web_search_tool.run(web_query)
        if web_result:
            web_info = f"\n\n**Web Search Results:**\n{web_result}"
    except Exception as e:
        logger.warning(f"Web search failed: {e}")
    
    # Construct response
    response = f"I don't have {location} hotels in my database yet, but here's what I found online:"
    response += wiki_info + web_info
    
    # Add direct links as fallback
    if not wiki_info and not web_info:
        response = f"""I couldn't find hotels for '{location}' in my database or online.

Try searching on:
🔗 **Booking.com**: https://www.booking.com/searchresults.html?ss={location.replace(' ', '+')}
🔗 **TripAdvisor**: https://www.tripadvisor.com/Search?q={location.replace(' ', '+')}+hotels
🔗 **Hotels.com**: https://www.hotels.com/search.do?q-destination={location.replace(' ', '+')}

*Tip: Try searching for major cities like Paris, Bali, Tokyo, Dubai, or New York.*
"""
```

---

## API Response Structure

### Local Hotels Found

```json
{
  "response": "Based on your query, here are some recommendations:\n\n1. **Hotel Le Marais** (Paris)\n   💰 $180/night | ⭐ 4.5/5\n   ...",
  "hotels": [
    {
      "name": "Hotel Le Marais",
      "location": "Paris",
      "price": "180",
      "rating": "4.5",
      "description": "Charming boutique hotel...",
      "amenities": "WiFi|Pool|Restaurant",
      "image_url": "https://example.com/hotel.jpg"
    }
  ],
  "external_search": false,
  "confidence": 0.85,
  "timestamp": "2025-12-01T10:30:00"
}
```

### External Search Triggered

```json
{
  "response": "I don't have Rome hotels in my database yet, but here's what I found online:\n\n**About Rome:**\nRome is the capital city of Italy...\n\n**Web Search Results:**\nTop hotels in Rome include Hotel Artemide...\n\n**Direct Booking Links:**\n🔗 Booking.com: https://...",
  "hotels": [],
  "external_search": true,
  "confidence": 0.5,
  "timestamp": "2025-12-01T10:30:00"
}
```

---

## Installation

### Install Dependencies

```bash
cd chatbot-service

# Install external search packages
pip install beautifulsoup4 requests duckduckgo-search

# Or use requirements.txt
pip install -r requirements.txt
```

### Requirements

```txt
# requirements.txt
flask>=3.0.0
flask-cors>=4.0.0
langchain>=0.1.0
langchain-huggingface>=0.0.1
langchain-community>=0.0.10
sentence-transformers>=2.2.2
transformers>=4.36.0
faiss-cpu>=1.7.4
wikipedia>=1.4.0
pandas>=2.1.0
beautifulsoup4>=4.12.0
requests>=2.31.0
duckduckgo-search>=3.9.0
```

---

## Testing

### Test Local Search (Should Return Database Hotels)

```bash
curl -X POST http://localhost:5001/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hotels in Paris"}'

# Expected: hotels array with Paris hotels, external_search: false
```

### Test External Search (Should Trigger Wikipedia/Web)

```bash
curl -X POST http://localhost:5001/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hotels in Rome"}'

# Expected: hotels array empty, external_search: true, links provided
```

---

## Configuration

### Adjust Search Behavior

```python
# In app.py

# 1. Change relevance threshold (stricter matching)
if len([w for w in query_lower.split() if w in content_lower and len(w) > 4]) >= 2:
    is_relevant = True

# 2. Change Wikipedia summary length
wiki_info = f"\n\n**About {location}:**\n{wiki_result[:300]}..."  # 300 chars instead of 500

# 3. Customize web search query
web_query = f"luxury hotels in {location} 5-star reviews"  # More specific

# 4. Add more booking sites
airbnb_url = f"https://www.airbnb.com/s/{location}/homes"
expedia_url = f"https://www.expedia.com/Hotel-Search?destination={location}"
```

---

## Troubleshooting

### Issue: Wikipedia returns irrelevant info

**Solution:** Make query more specific

```python
# Instead of:
wiki_result = wikipedia_tool.run(f"{location} tourism hotels")

# Use:
wiki_result = wikipedia_tool.run(f"{location} city travel guide hotels accommodation")
```

### Issue: DuckDuckGo search fails

**Cause:** Rate limiting or network issues

**Solution:** Add error handling and fallback

```python
try:
    web_result = web_search_tool.run(web_query)
except Exception as e:
    logger.warning(f"Web search failed: {e}")
    # Skip web results, just show direct links
    web_result = None
```

### Issue: Wrong location extracted

**Solution:** Improve extraction logic

```python
# Add common misspellings/variations
LOCATION_MAP = {
    "ny": "New York",
    "nyc": "New York",
    "sf": "San Francisco",
    "la": "Los Angeles",
    "paris france": "Paris",
}

extracted = extract_location(query)
location = LOCATION_MAP.get(extracted.lower(), extracted)
```

---

## Future Enhancements

### 1. **Cache External Results**

```python
import redis

cache = redis.Redis(host='localhost', port=6379)

def search_external(location):
    # Check cache first
    cached = cache.get(f"hotels:{location}")
    if cached:
        return json.loads(cached)
    
    # Search and cache
    result = perform_search(location)
    cache.setex(f"hotels:{location}", 3600, json.dumps(result))  # 1 hour
    return result
```

### 2. **Better Location Extraction (NLP)**

```python
from transformers import pipeline

ner = pipeline("ner", model="dslim/bert-base-NER")

def extract_location_nlp(query):
    entities = ner(query)
    locations = [e['word'] for e in entities if e['entity'] == 'B-LOC']
    return locations[0] if locations else extract_location(query)
```

### 3. **Scrape Booking.com (Advanced)**

```python
import requests
from bs4 import BeautifulSoup

def scrape_booking(location):
    url = f"https://www.booking.com/searchresults.html?ss={location}"
    response = requests.get(url, headers={'User-Agent': 'Mozilla/5.0'})
    soup = BeautifulSoup(response.content, 'html.parser')
    
    hotels = []
    for hotel in soup.select('.sr_item'):
        name = hotel.select_one('.sr-hotel__name').text
        price = hotel.select_one('.bui-price-display__value').text
        hotels.append({'name': name, 'price': price})
    
    return hotels
```

---

## Summary

✅ **Seamless Experience**: No dead ends, always helpful
✅ **Multiple Sources**: Wikipedia + Web search + Direct links
✅ **Smart Routing**: Local first, external as fallback
✅ **Free Tools**: No API keys required
✅ **Easy to Extend**: Add more sources anytime

**The chatbot now handles ANY location query, not just the 5 cities in the database!** 🌍🏨
