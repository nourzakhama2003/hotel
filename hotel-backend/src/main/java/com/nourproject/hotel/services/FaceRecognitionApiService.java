package com.nourproject.hotel.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FaceRecognitionApiService {

    @Value("${face.recognition.service.url:http://localhost:5000}")
    private String faceServiceUrl;

    private final RestTemplate restTemplate;

    public FaceRecognitionApiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Check if the face recognition service is available
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    faceServiceUrl + "/health",
                    Map.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("Face recognition service not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract face embeddings from an image
     * Returns a 512-dimensional embedding vector
     */
    public double[] extractFaceEmbedding(Path imagePath) throws IOException {
        File imageFile = imagePath.toFile();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(imageFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    faceServiceUrl + "/extract-features",
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                if (Boolean.TRUE.equals(responseBody.get("success"))) {
                    @SuppressWarnings("unchecked")
                    List<Double> embeddingList = (List<Double>) responseBody.get("embedding");
                    
                    return embeddingList.stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray();
                } else {
                    String error = (String) responseBody.get("error");
                    throw new RuntimeException(error != null ? error : "Failed to extract face embedding");
                }
            } else {
                throw new RuntimeException("Unexpected response from face recognition service");
            }
        } catch (Exception e) {
            log.error("Error calling face recognition service", e);
            throw new RuntimeException("No face detected in the image. Please ensure your face is clearly visible.");
        }
    }

    /**
     * Compare two face embeddings
     * Returns similarity percentage (0-100)
     */
    public double compareFaceEmbeddings(double[] embedding1, double[] embedding2) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "embedding1", embedding1,
                "embedding2", embedding2
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    faceServiceUrl + "/compare-faces",
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                if (Boolean.TRUE.equals(responseBody.get("success"))) {
                    return ((Number) responseBody.get("similarity")).doubleValue();
                }
            }
            
            throw new RuntimeException("Failed to compare face embeddings");
        } catch (Exception e) {
            log.error("Error comparing face embeddings", e);
            throw new RuntimeException("Error comparing faces: " + e.getMessage());
        }
    }
}
