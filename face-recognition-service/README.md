# Face Recognition Service Setup

This directory contains a Python microservice that provides deep learning-based face recognition using FaceNet (InceptionResnetV1) pre-trained on VGGFace2 dataset.

## Features

- **Face Detection**: Uses MTCNN (Multi-task Cascaded Convolutional Networks) for robust face detection
- **Face Embeddings**: Generates 512-dimensional face embeddings using FaceNet
- **High Accuracy**: Much more accurate than histogram-based approaches
- **Distinguishes**: Can properly distinguish faces from hands, objects, and other non-face images

## Quick Start

### 1. Build and Run with Docker Compose

From the hotel directory:

```bash
docker compose up -d face-recognition
```

### 2. Test the Service

```bash
# Health check
curl http://localhost:5000/health

# Expected response:
# {"status": "healthy", "model": "FaceNet", "device": "cpu"}
```

### 3. Restart Backend

The Java backend will automatically detect and use the new service.

```bash
docker compose restart backend
```

### 4. Re-register Your Face

**IMPORTANT**: You must re-register your face because the new system uses 512-dimensional deep learning embeddings instead of the old 221-dimensional histogram features.

1. Go to your profile
2. Click "Register Face" or "Update Face"
3. Capture your face
4. The new system will use the Python service for feature extraction

## API Endpoints

### POST /extract-features

Extract face embeddings from an image.

**Request**: multipart/form-data with `image` field

**Response**:
```json
{
  "success": true,
  "embedding": [0.123, -0.456, ...],  // 512 dimensions
  "dimension": 512
}
```

### POST /compare-faces

Compare two face embeddings.

**Request**:
```json
{
  "embedding1": [0.123, ...],
  "embedding2": [0.456, ...]
}
```

**Response**:
```json
{
  "success": true,
  "similarity": 95.5,  // Percentage (0-100)
  "cosine_similarity": 0.91
}
```

### GET /health

Health check endpoint.

## Model Information

- **Detection**: MTCNN (Multi-task Cascaded Convolutional Networks)
- **Recognition**: InceptionResnetV1 pre-trained on VGGFace2
- **Embedding Size**: 512 dimensions
- **Accuracy**: State-of-the-art face recognition performance

## Why This is Better

**Old System (Histogram)**:
- 221 features (mean, std dev, edges, profiles)
- Could not distinguish hands from faces
- Required 85-90% threshold but still had false positives

**New System (FaceNet)**:
- 512-dimensional deep learning embeddings
- Trained on millions of face images
- Properly distinguishes faces from non-faces
- 85% threshold is actually meaningful

## Troubleshooting

### Service won't start

Check Docker logs:
```bash
docker logs hotel-face-recognition
```

### Connection refused

Make sure the service is running:
```bash
docker ps | grep face-recognition
```

### Slow performance

The first request is slower (model loading). Subsequent requests are fast.

For GPU acceleration, modify Dockerfile to install CUDA-enabled PyTorch.

## Configuration

Edit `app.py` to adjust:
- MTCNN thresholds (face detection strictness)
- Image size (default: 160x160)
- Margin around detected faces

Edit `docker-compose.yml` to change:
- Port mapping (default: 5000)
- Resource limits
