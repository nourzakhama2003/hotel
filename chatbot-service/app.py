"""
Hotel Recommendation Chatbot Service
Flask API with LangChain, Hugging Face, and RAG
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import logging
from datetime import datetime

# LangChain imports
from langchain_community.document_loaders import CSVLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings, HuggingFacePipeline
from langchain_community.vectorstores import FAISS
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough

# External search tools
from langchain_community.tools import WikipediaQueryRun, DuckDuckGoSearchRun
from langchain_community.utilities import WikipediaAPIWrapper
import requests
from bs4 import BeautifulSoup

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Global variables for chatbot components
vectorstore = None
retriever = None
llm = None
rag_chain = None
wikipedia_tool = None
web_search_tool = None
init_error = None

def initialize_chatbot():
    """Initialize all chatbot components on startup"""
    global vectorstore, retriever, llm, rag_chain, wikipedia_tool, web_search_tool, init_error

    try:
        logger.info("🤖 Initializing Hotel Recommendation Chatbot...")

        # Initialize external search tools
        logger.info("🌐 Initializing external search tools...")
        try:
            wikipedia_tool = WikipediaQueryRun(api_wrapper=WikipediaAPIWrapper())
            web_search_tool = DuckDuckGoSearchRun()
            logger.info("✅ External search tools ready")
        except Exception as e:
            logger.warning(f"⚠️ External search tools failed to initialize: {e}")

        # Check for hotels.csv
        if not os.path.exists("hotels.csv"):
            raise FileNotFoundError("hotels.csv not found in the current directory")


        # Step 1: Load and split documents
        logger.info("📄 Loading hotel data from CSV...")
        loader = CSVLoader(
            file_path="hotels.csv",
            encoding="utf-8",
            csv_args={'delimiter': ','}
        )
        docs = loader.load()

        splitter = RecursiveCharacterTextSplitter(
            chunk_size=500,
            chunk_overlap=50,
            separators=["\n\n", "\n", " ", ""]
        )
        chunks = splitter.split_documents(docs)
        logger.info(f"✅ Loaded {len(chunks)} document chunks")

        # Step 2: Create embeddings and vector store
        logger.info("🔢 Creating embeddings and vector store...")
        embeddings = HuggingFaceEmbeddings(
            model_name="sentence-transformers/all-MiniLM-L6-v2"
        )

        # Check if FAISS index exists
        if os.path.exists("faiss_index"):
            logger.info("📂 Loading existing FAISS index...")
            vectorstore = FAISS.load_local(
                "faiss_index",
                embeddings,
                allow_dangerous_deserialization=True
            )
        else:
            logger.info("🆕 Creating new FAISS index...")
            vectorstore = FAISS.from_documents(
                documents=chunks,
                embedding=embeddings
            )
            # Save to disk for persistence
            vectorstore.save_local("faiss_index")

        retriever = vectorstore.as_retriever(
            search_type="similarity",
            search_kwargs={"k": 3}
        )
        logger.info("✅ Vector store created successfully")

        # Step 3: Load language model
        logger.info("🧠 Loading language model...")
        llm = HuggingFacePipeline.from_model_id(
            model_id="google/flan-t5-base",  # Lighter model for faster responses
            task="text2text-generation",
            model_kwargs={
                "temperature": 0.7,
                "max_length": 200,
                "do_sample": True
            }
        )
        logger.info("✅ Language model loaded")

        # Step 4: Create RAG chain with prompt template
        logger.info("⛓️ Building RAG chain...")

        prompt_template = """You are a helpful hotel recommendation assistant.

Use the following hotel information to answer the question. If the information is not available, say so politely.

Context: {context}

Question: {question}

Answer: Provide a helpful recommendation based on the context."""

        prompt = ChatPromptTemplate.from_template(prompt_template)

        def format_docs(docs):
            return "\n\n".join([f"Hotel: {doc.page_content}" for doc in docs])

        rag_chain = (
            {
                "context": retriever | format_docs,
                "question": RunnablePassthrough()
            }
            | prompt
            | llm
            | StrOutputParser()
        )
        logger.info("✅ RAG chain created")

        logger.info("🎉 Chatbot initialization complete!")

        return True

    except Exception as e:
        init_error = str(e)
        logger.error(f"❌ Chatbot initialization failed: {str(e)}")
        return False

# Initialize on startup
initialize_chatbot()

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'Hotel Chatbot',
        'timestamp': datetime.now().isoformat()
    }), 200

@app.route('/chat', methods=['POST'])
def chat():
    """
    Main chat endpoint
    Expects JSON: {"message": "user query"}
    Returns JSON: {"response": "bot response", "confidence": float}
    """
    if init_error:
        return jsonify({
            'error': 'Chatbot failed to initialize',
            'details': init_error,
            'status': 'service_unavailable'
        }), 503

    if not retriever or not rag_chain:
        return jsonify({
            'error': 'Chatbot is initializing or failed to load components',
            'status': 'service_unavailable'
        }), 503

    try:
        data = request.get_json()

        if not data or 'message' not in data:
            return jsonify({'error': 'Message is required'}), 400

        user_message = data['message'].strip()

        if not user_message:
            return jsonify({'error': 'Message cannot be empty'}), 400

        logger.info(f"💬 User query: {user_message}")

        # Retrieve relevant hotels first
        relevant_docs = retriever.invoke(user_message)
        logger.info(f"📚 Retrieved {len(relevant_docs)} relevant hotels")

        # Check if retrieved hotels are relevant (simple relevance check)
        is_relevant = False
        if relevant_docs:
            # Check if query keywords match retrieved hotels
            query_lower = user_message.lower()
            for doc in relevant_docs:
                content_lower = doc.page_content.lower()
                # Check for location match or general hotel-related terms
                if any(word in content_lower for word in query_lower.split() if len(word) > 3):
                    is_relevant = True
                    break

        # If we have relevant hotels, format them nicely as fallback
        hotels_data = []
        external_search_used = False

        if relevant_docs and is_relevant:
            for i, doc in enumerate(relevant_docs, 1):
                # Parse hotel data from document
                content = doc.page_content
                hotel = {}
                for line in content.split('\n'):
                    if ':' in line:
                        key, value = line.split(':', 1)
                        hotel[key.strip()] = value.strip()
                hotels_data.append(hotel)

            # Format as text response
            hotels_info = []
            for i, hotel in enumerate(hotels_data, 1):
                info = f"{i}. **{hotel.get('name', 'Unknown Hotel')}** ({hotel.get('location', 'Unknown')})\n"
                info += f"   💰 ${hotel.get('price', 'N/A')}/night | ⭐ {hotel.get('rating', 'N/A')}/5\n"
                info += f"   📝 {hotel.get('description', 'No description')}\n"
                amenities = hotel.get('amenities', '').replace('|', ', ')
                if amenities:
                    info += f"   ✨ {amenities}"
                hotels_info.append(info)

            fallback_response = f"Based on your query, here are some recommendations:\n\n" + "\n\n".join(hotels_info)
        else:
            # No relevant hotels in database - search external sources
            logger.info("🌐 No relevant hotels in database, searching external sources...")
            external_search_used = True
            hotels_data = []

            try:
                # Extract location from query (simple extraction)
                location = extract_location(user_message)

                # Search Wikipedia for destination info
                wiki_info = ""
                try:
                    logger.info(f"📖 Searching Wikipedia for: {location}")
                    wiki_result = wikipedia_tool.run(f"{location} tourism hotels")
                    if wiki_result and len(wiki_result) > 50:
                        wiki_info = f"\n\n**About {location}:**\n{wiki_result[:500]}..."
                        logger.info("✅ Wikipedia info retrieved")
                except Exception as wiki_error:
                    logger.warning(f"⚠️ Wikipedia search failed: {str(wiki_error)}")

                # Search web for hotels
                web_info = ""
                try:
                    logger.info(f"🔍 Searching web for hotels in: {location}")
                    web_query = f"best hotels in {location} booking.com tripadvisor"
                    web_result = web_search_tool.run(web_query)
                    if web_result:
                        web_info = f"\n\n**Web Search Results:**\n{web_result}"
                        logger.info("✅ Web search results retrieved")
                except Exception as web_error:
                    logger.warning(f"⚠️ Web search failed: {str(web_error)}")

                # Construct fallback response with external data
                fallback_response = f"I don't have {location} hotels in my database yet, but here's what I found online:"
                fallback_response += wiki_info + web_info

                if not wiki_info and not web_info:
                    fallback_response = f"I couldn't find hotels for '{location}' in my database or online. Try searching on:\n\n" \
                                      f"🔗 **Booking.com**: https://www.booking.com/searchresults.html?ss={location.replace(' ', '+')}\n" \
                                      f"🔗 **TripAdvisor**: https://www.tripadvisor.com/Search?q={location.replace(' ', '+')}+hotels\n" \
                                      f"🔗 **Hotels.com**: https://www.hotels.com/search.do?q-destination={location.replace(' ', '+')}\n\n" \
                                      f"*Tip: Try searching for major cities like Paris, Bali, Tokyo, Dubai, or New York.*"

            except Exception as external_error:
                logger.error(f"❌ External search error: {str(external_error)}")
                fallback_response = "I couldn't find any hotels matching your criteria. Please try searching for cities in my database (Paris, Bali, Tokyo, Dubai, New York)."

        # Try to use RAG chain for better response
        try:
            response = rag_chain.invoke(user_message)
            # If response is too short or just the prompt, use fallback
            if len(response.strip()) < 50 or "You are a helpful" in response:
                logger.warning("⚠️ LLM response too short, using fallback")
                response = fallback_response
        except Exception as llm_error:
            logger.warning(f"⚠️ LLM failed, using fallback: {str(llm_error)}")
            response = fallback_response

        logger.info(f"🤖 Bot response: {response[:100]}...")

        return jsonify({
            'response': response,
            'hotels': hotels_data,  # Add structured hotel data
            'external_search': external_search_used,  # Flag for external search
            'confidence': 0.85 if not external_search_used else 0.5,
            'timestamp': datetime.now().isoformat()
        }), 200

    except Exception as e:
        logger.error(f"❌ Chat error: {str(e)}")
        return jsonify({
            'error': 'An error occurred processing your request',
            'details': str(e)
        }), 500

def extract_location(query):
    """
    Extract location from user query (simple keyword extraction)
    """
    # Common location keywords
    location_keywords = ['in', 'at', 'near', 'around', 'for']

    query_lower = query.lower()

    # Try to find location after keywords
    for keyword in location_keywords:
        if keyword in query_lower:
            parts = query_lower.split(keyword)
            if len(parts) > 1:
                # Get text after keyword
                location_part = parts[1].strip().split()[0] if parts[1].strip() else ""
                if location_part:
                    return location_part.capitalize()

    # If no keyword found, try to extract capitalized words (likely locations)
    words = query.split()
    for word in words:
        if word[0].isupper() and len(word) > 3:
            return word

    # Default fallback
    return "your destination"

@app.route('/recommend', methods=['POST'])
def recommend():
    """
    Specific hotel recommendation endpoint
    Expects JSON: {"location": "Paris", "max_price": 200, "min_rating": 4.0}
    """
    try:
        data = request.get_json()

        location = data.get('location', '')
        max_price = data.get('max_price', float('inf'))
        min_rating = data.get('min_rating', 0.0)

        query = f"Recommend hotels in {location}"
        if max_price < float('inf'):
            query += f" under ${max_price} per night"
        if min_rating > 0:
            query += f" with rating above {min_rating}"

        logger.info(f"🔍 Recommendation query: {query}")

        response = rag_chain.invoke(query)

        return jsonify({
            'response': response,
            'query': query,
            'timestamp': datetime.now().isoformat()
        }), 200

    except Exception as e:
        logger.error(f"❌ Recommendation error: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/destinations', methods=['GET'])
def get_destinations():
    """Get list of available destinations"""
    try:
        destinations = [
            "Paris", "Bali", "Rome", "London", "Tokyo"
        ]
        return jsonify({
            'destinations': destinations,
            'count': len(destinations)
        }), 200
    except Exception as e:
        logger.error(f"❌ Destinations error: {str(e)}")
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5001))
    app.run(host='0.0.0.0', port=port, debug=False)
