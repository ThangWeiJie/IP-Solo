package com.literacyhub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.literacyhub.entity.AssessmentConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssessmentService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> calculateResult(int rawScore, AssessmentConfig config) {
        int finalScore = rawScore * config.getMultiplier();

        String level = "Completed";
        String color = "text-indigo-600 bg-indigo-50";
        String recommendation = "Thank you for completing this self-assessment.";

        try {
            if (config.getScoringJSON() != null && !config.getScoringJSON().isEmpty()) {
                List<Map<String, Object>> ranges = objectMapper.readValue(
                        config.getScoringJSON(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                for (Map<String, Object> range : ranges) {
                    int min = (int) range.get("min");
                    int max = (int) range.get("max");

                    if (finalScore >= min && finalScore <= max) {
                        level = (String) range.get("label");
                        recommendation = (String) range.get("rec");
                        color = (String) range.get("color");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing scoring JSON: " + e.getMessage());
        }

        return Map.of(
                "score", finalScore,
                "level", level,
                "colorClass", color,
                "recommendation", recommendation
        );
    }
}