from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import logging
from datetime import datetime
from dotenv import load_dotenv

# Try importing LangChain components with fallback
try:
    from langchain_community.document_loaders import CSVLoader
    from langchain_text_splitters import RecursiveCharacterTextSplitter
    from langchain_huggingface import HuggingFaceEmbeddings
    from langchain_google_genai import ChatGoogleGenerativeAI
    from langchain_community.vectorstores import FAISS
    from langchain_core.prompts import ChatPromptTemplate
    from langchain_core.output_parsers import StrOutputParser
    from langchain_core.runnables import RunnablePassthrough
    from langchain_core.runnables import RunnableLambda
except ImportError as e:
    print(f"CRITICAL: Missing dependency: {str(e)}")
    # This will allow the app to start but health check will fail
    pass

# Load environment variables
load_dotenv()

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
init_error = None

def format_docs(docs):
    """Helper to format documents for the prompt"""
    return "\n\n".join([f"Hotel: {doc.page_content}" for doc in docs])

def initialize_chatbot():
    """Initialize all chatbot components on startup"""
    global vectorstore, retriever, llm, rag_chain, init_error
    try:
        logger.info(" Initializing Hotel Assistant Chatbot...")

        # Step 1: Load and split documents
        csv_path = os.environ.get("GUIDE_CSV_PATH", "hotels.csv")
        if not os.path.exists(csv_path):
            raise FileNotFoundError(f"{csv_path} not found! Please provide hotel data.")

        logger.info(f" Loading hotel data from {csv_path}...")
        loader = CSVLoader(
            file_path=csv_path,
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
        logger.info(f" Loaded {len(chunks)} document chunks")

        # Step 2: Create embeddings and vector store
        logger.info(" Creating embeddings and vector store...")
        embeddings = HuggingFaceEmbeddings(
            model_name="sentence-transformers/all-MiniLM-L6-v2"
        )

        index_path = "faiss_index"
        # Check for index.faiss or similar
        if os.path.exists(index_path):
            logger.info(f" Loading existing FAISS index from {index_path}...")
            vectorstore = FAISS.load_local(
                index_path,
                embeddings,
                allow_dangerous_deserialization=True
            )
        else:
            logger.info(" Creating new FAISS index...")
            vectorstore = FAISS.from_documents(chunks, embeddings)
            vectorstore.save_local(index_path)

        retriever = vectorstore.as_retriever(
            search_type="similarity",
            search_kwargs={"k": 3}
        )
        logger.info(" Vector store created successfully")

        # Step 3: Initialize Google Gemini AI
        google_api_key = os.environ.get("GOOGLE_API_KEY")
        logger.info(" Initializing Google Gemini AI (gemini-1.5-flash)...")

        llm = ChatGoogleGenerativeAI(
            model="models/gemini-3-flash-preview",
            google_api_key=google_api_key,
            temperature=0.2,
            max_output_tokens=1024
        )
        logger.info(" Language model loaded")

        # Step 4: Create RAG chain with tailored prompt
        logger.info("⛓️ Building RAG chain...")
        prompt_template = """You are a helpful hotel recommendation assistant. 
Use the context below to answer the user's question. 
Provide a detailed response mentioning ALL suitable hotels from the context.

Context:
{context}

Question: {question}
Answer:"""

        prompt = ChatPromptTemplate.from_template(prompt_template)

        # LCEL chain
        rag_chain = (
            {
                "context": retriever | RunnableLambda(format_docs),
                "question": RunnablePassthrough(),
                "docs": retriever
            }
            | RunnablePassthrough.assign(
                response = prompt | llm | StrOutputParser()
            )
        )

        logger.info(" RAG chain created")
        logger.info(" Hotel Chatbot initialization complete!")
        return True
    except Exception as e:
        init_error = str(e)
        logger.error(f" Initialization failed: {str(e)}")
        return False

# Initialize on startup
initialize_chatbot()

@app.route('/health', methods=['GET'])
def health_check():
    status = 'healthy' if not init_error else 'unhealthy'
    return jsonify({
        'status': status,
        'service': 'Hotel Assistant Chatbot',
        'error': init_error,
        'timestamp': datetime.now().isoformat()
    }), 200 if status == 'healthy' else 503

@app.route('/chat', methods=['POST'])
def chat():
    if init_error:
        return jsonify({'error': 'Chatbot initialization failed', 'details': init_error}), 503
    if not rag_chain:
        return jsonify({'error': 'Chatbot not ready'}), 503

    try:
        data = request.get_json()
        if not data or 'message' not in data:
            return jsonify({'error': 'Message required'}), 400

        user_message = data['message'].strip()
        if not user_message:
            return jsonify({'error': 'Empty message'}), 400

        logger.info(f" User query: {user_message}")

        # Invoke RAG
        result = rag_chain.invoke(user_message)
        response = result.get("response", "").strip()
        source_docs = result.get("docs", [])

        # Parse hotels from source docs
        hotels = []
        for doc in source_docs:
            hotel_data = {}
            # Standard CSVLoader format is "column: value" on separate lines
            lines = [l.strip() for l in doc.page_content.split('\n') if l.strip()]
            for line in lines:
                if ': ' in line: # Use ': ' to be more specific than just ':'
                    key, value = line.split(': ', 1)
                    hotel_data[key.lower().replace(' ', '_')] = value
            if hotel_data and 'name' in hotel_data:
                hotels.append(hotel_data)
        
        logger.info(f" Extracted {len(hotels)} hotels from source docks")

        # Output cleanup
        if "Answer:" in response:
            response = response.split("Answer:")[-1].strip()

        cleanup_patterns = [
            "You are a helpful hotel recommendation assistant.",
            "Use the context below to answer the question concisely and helpfully.",
            "Context:",
            "Question:",
            "Answer:",
            "The answer is:",
            "Based on the information provided,"
        ]
        for pattern in cleanup_patterns:
            response = response.replace(pattern, "").strip()

        # Final cleanup for potential repetition or prompt leakage
        response = " ".join(response.split())

        # Logic for empty or irrelevant responses
        if len(response) < 10:
            if hotels:
                response = f"I found {len(hotels)} hotels that might match your needs:"
            else:
                response = "I'm sorry, I couldn't find a specific hotel matching your request. Could you please specify a city like Paris, Bali, or Tokyo?"

        logger.info(f" Response: {response[:100]}...")

        return jsonify({
            'response': response,
            'hotels': hotels,
            'timestamp': datetime.now().isoformat()
        }), 200

    except Exception as e:
        logger.error(f" Chat error: {str(e)}")
        return jsonify({'error': 'Processing error', 'details': str(e)}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5001))
    app.run(host='0.0.0.0', port=port, debug=False)
