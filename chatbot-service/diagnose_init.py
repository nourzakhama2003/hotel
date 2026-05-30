
import sys
import os
import logging

# Add current dir to path
sys.path.append(os.getcwd())

from app import initialize_chatbot, init_error

# Configure logging to console
logging.basicConfig(level=logging.INFO)

print("--- Starting Diagnostic ---")
success = initialize_chatbot()
if success:
    print("SUCCESS: Chatbot initialized correctly.")
else:
    print(f"FAILURE: Chatbot failed to initialize.")
    print(f"Error: {init_error}")
print("--- End Diagnostic ---")
