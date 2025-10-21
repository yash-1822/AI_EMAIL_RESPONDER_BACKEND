package com.emailReplier.dm.service;

import com.emailReplier.dm.model.EmailEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

//    @Value("${GEMINI.API.URL}")
//    private String geminiApiUrl;
//
//    @Value("${GEMINI.API.KEY}")
//    private String geminiApiKey;

    @Value("${GEMINI_API_URL}")
    private String geminiApiUrl;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmail(EmailEntity emailEntity){
        System.out.println("content from sevice folder:"+emailEntity);
        String prompt = buildPrompt(emailEntity);

        Map<String,Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String buildPrompt(EmailEntity emailEntity) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content");
        if(emailEntity.getTone()!=null && !emailEntity.getTone().isEmpty()){
            prompt.append("in a ").append(emailEntity.getTone()).append("tone.And dont give any choices give single content.");
        }
        prompt.append("\nOriginal email data is:\n").append(emailEntity.getEmailContent());
        return prompt.toString();
    }
}
