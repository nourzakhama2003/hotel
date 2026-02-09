package com.nourproject.hotel.services;

import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class FaceRecognitionService {

    private final UserRepository userRepository;
    private final FaceRecognitionApiService faceRecognitionApiService;

    @Value("${face.recognition.threshold:0.85}")
    private double recognitionThreshold;

    @Value("${face.recognition.upload.dir:./face-uploads}")
    private String uploadDir;

    @Value("${face.recognition.keep.files:false}")
    private boolean keepFiles;

    public FaceRecognitionService(UserRepository userRepository, FaceRecognitionApiService faceRecognitionApiService) {
        this.userRepository = userRepository;
        this.faceRecognitionApiService = faceRecognitionApiService;
    }

    @PostConstruct
    public void init() {
        try {
            // Normalize upload directory to an absolute path and create it
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            uploadDir = uploadPath.toString();
            Files.createDirectories(uploadPath);
            log.info("✅ Face recognition service initialized successfully using Python deep learning service. Upload dir: {}", uploadDir);
        } catch (Exception e) {
            log.error("Error creating upload directory: {}", e.getMessage(), e);
        }
    }

    /**
     * Register a user's face encoding
     */
    public Map<String, Object> registerFace(Long userId, MultipartFile faceImage) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Save the uploaded image temporarily
        String fileName = "face_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);
        // Ensure parent directory exists and log the path
        Files.createDirectories(filePath.getParent());
        log.debug("Saving uploaded face image to {}", filePath.toAbsolutePath());
        faceImage.transferTo(filePath.toFile());

        try {
            // Use Python deep learning service for face recognition
            double[] faceEmbedding = faceRecognitionApiService.extractFaceEmbedding(filePath);
            
            // Save face encoding to user
            user.setFaceEncoding(Arrays.toString(faceEmbedding));
            user.setFaceAuthEnabled(true);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Face registered successfully using deep learning");
            response.put("userId", userId);
            response.put("faceAuthEnabled", true);

            log.info("Face registered for user: {} using deep learning", userId);
            return response;
            
        } catch (Exception e) {
            log.error("Error extracting face embedding: {}", e.getMessage());
            throw new RuntimeException("Failed to register face: " + e.getMessage());
        } finally {
            // Clean up temporary file based on configuration
            if (!keepFiles) {
                Files.deleteIfExists(filePath);
                log.debug("Deleted temporary face file: {}", filePath);
            } else {
                log.debug("Keeping face file for debugging: {}", filePath);
            }
        }
    }

    /**
     * Register a user's face encoding by email
     */
    public Map<String, Object> registerFaceByUsername(String email, MultipartFile faceImage) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Save the uploaded image temporarily
        String fileName = "face_" + user.getId() + "_" + System.currentTimeMillis() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);
        // Ensure parent directory exists and log the path
        Files.createDirectories(filePath.getParent());
        log.debug("Saving uploaded face image to {}", filePath.toAbsolutePath());
        faceImage.transferTo(filePath.toFile());

        try {
            // Use Python deep learning service for face recognition
            double[] faceEmbedding = faceRecognitionApiService.extractFaceEmbedding(filePath);
            
            // Save face encoding to user
            user.setFaceEncoding(Arrays.toString(faceEmbedding));
            user.setFaceAuthEnabled(true);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Face registered successfully using deep learning");
            response.put("userId", user.getId());
            response.put("faceAuthEnabled", true);

            log.info("Face registered for user: {} using deep learning", email);
            return response;
            
        } catch (Exception e) {
            log.error("Error extracting face embedding: {}", e.getMessage());
            throw new RuntimeException("Failed to register face: " + e.getMessage());
        } finally {
            // Clean up temporary file based on configuration
            if (!keepFiles) {
                Files.deleteIfExists(filePath);
                log.debug("Deleted temporary face file: {}", filePath);
            } else {
                log.debug("Keeping face file for debugging: {}", filePath);
            }
        }
    }

    /**
     * Authenticate user using face recognition
     */
    public Map<String, Object> authenticateByFace(MultipartFile faceImage) throws Exception {
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Save the uploaded image temporarily
        String fileName = "auth_face_" + System.currentTimeMillis() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);
        // Ensure parent directory exists and log the path
        Files.createDirectories(filePath.getParent());
        log.debug("Saving authentication image to {}", filePath.toAbsolutePath());
        faceImage.transferTo(filePath.toFile());

        try {
            // Extract face features using Python deep learning service
            double[] authFaceEmbedding = faceRecognitionApiService.extractFaceEmbedding(filePath);
            
            // Compare with all registered users
            List<User> usersWithFaceAuth = userRepository.findByFaceAuthEnabled(true);
            User matchedUser = null;
            double bestMatchScore = 0.0;

            log.info("Comparing against {} registered users with face auth enabled", usersWithFaceAuth.size());

            for (User user : usersWithFaceAuth) {
                if (user.getFaceEncoding() != null) {
                    // Parse stored encoding
                    double[] storedEmbedding = parseEmbedding(user.getFaceEncoding());
                    
                    // Calculate cosine similarity
                    double similarity = calculateCosineSimilarity(authFaceEmbedding, storedEmbedding);
                    log.info("User: {} - Similarity: {} (threshold: {})", 
                            user.getEmail(), 
                            String.format("%.4f", similarity), 
                            String.format("%.2f", recognitionThreshold));

                    if (similarity > recognitionThreshold && similarity > bestMatchScore) {
                        bestMatchScore = similarity;
                        matchedUser = user;
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            if (matchedUser != null) {
                response.put("success", true);
                response.put("userId", matchedUser.getId());
                response.put("email", matchedUser.getEmail());
                response.put("userName", matchedUser.getUserName());
                response.put("matchScore", bestMatchScore);
                log.info("Face authentication successful for user: {}", matchedUser.getEmail());
            } else {
                response.put("success", false);
                response.put("message", "No matching face found. Please try again or use password login.");
                log.info("Face authentication failed - no match found");
            }

            return response;
            
        } catch (Exception e) {
            log.error("Error during face authentication: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate face: " + e.getMessage());
        } finally {
            // Clean up temporary file based on configuration
            if (!keepFiles) {
                Files.deleteIfExists(filePath);
                log.debug("Deleted temporary authentication file: {}", filePath);
            } else {
                log.debug("Keeping authentication file for debugging: {}", filePath);
            }
        }
    }
    
    /**
     * Parse embedding string back to double array
     */
    private double[] parseEmbedding(String embeddingStr) {
        // Remove brackets and split by comma
        embeddingStr = embeddingStr.trim();
        if (embeddingStr.startsWith("[")) {
            embeddingStr = embeddingStr.substring(1);
        }
        if (embeddingStr.endsWith("]")) {
            embeddingStr = embeddingStr.substring(0, embeddingStr.length() - 1);
        }
        
        String[] parts = embeddingStr.split(",");
        double[] embedding = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            embedding[i] = Double.parseDouble(parts[i].trim());
        }
        return embedding;
    }
    
    /**
     * Calculate cosine similarity between two embeddings
     */
    private double calculateCosineSimilarity(double[] embedding1, double[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embeddings must have same dimension");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Disable face authentication for a user
     */
    public Map<String, Object> disableFaceAuth(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFaceAuthEnabled(false);
        user.setFaceEncoding(null);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Face authentication disabled successfully");
        response.put("userId", userId);

        log.info("Face authentication disabled for user: {}", userId);
        return response;
    }

    /**
     * Check if user has face authentication enabled
     */
    public boolean isFaceAuthEnabled(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFaceAuthEnabled)
                .orElse(false);
    }
}
