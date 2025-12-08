from flask import Flask, request, jsonify
from PIL import Image
import io
import numpy as np
from facenet_pytorch import MTCNN, InceptionResnetV1
import torch

app = Flask(__name__)

# Initialize models
print("Loading face recognition models...")
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# MTCNN for face detection
mtcnn = MTCNN(
    image_size=160,
    margin=20,
    min_face_size=40,
    thresholds=[0.6, 0.7, 0.7],  # Stricter thresholds
    factor=0.709,
    post_process=True,
    device=device,
    keep_all=False  # Only keep the best face
)

# InceptionResnetV1 for face embeddings (pre-trained on VGGFace2)
resnet = InceptionResnetV1(pretrained='vggface2').eval().to(device)

print(f"Models loaded successfully on {device}")

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({"status": "healthy", "model": "FaceNet", "device": str(device)})

@app.route('/extract-features', methods=['POST'])
def extract_features():
    """
    Extract face features (embeddings) from an image
    Returns a 512-dimensional embedding vector
    """
    try:
        if 'image' not in request.files:
            return jsonify({"error": "No image provided"}), 400
        
        file = request.files['image']
        image = Image.open(io.BytesIO(file.read())).convert('RGB')
        
        # Detect face
        face_tensor = mtcnn(image)
        
        if face_tensor is None:
            return jsonify({"error": "No face detected in the image"}), 400
        
        # Extract embeddings
        face_tensor = face_tensor.unsqueeze(0).to(device)
        
        with torch.no_grad():
            embedding = resnet(face_tensor).cpu().numpy()[0]
        
        # Convert to list for JSON serialization
        embedding_list = embedding.tolist()
        
        return jsonify({
            "success": True,
            "embedding": embedding_list,
            "dimension": len(embedding_list)
        })
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/compare-faces', methods=['POST'])
def compare_faces():
    """
    Compare two face embeddings and return similarity score
    Expects JSON with two embedding arrays
    """
    try:
        data = request.get_json()
        
        if 'embedding1' not in data or 'embedding2' not in data:
            return jsonify({"error": "Both embedding1 and embedding2 are required"}), 400
        
        emb1 = np.array(data['embedding1'])
        emb2 = np.array(data['embedding2'])
        
        # Compute cosine similarity
        similarity = np.dot(emb1, emb2) / (np.linalg.norm(emb1) * np.linalg.norm(emb2))
        
        # Convert to percentage (0-100)
        similarity_percentage = float((similarity + 1) / 2 * 100)
        
        return jsonify({
            "success": True,
            "similarity": similarity_percentage,
            "cosine_similarity": float(similarity)
        })
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
