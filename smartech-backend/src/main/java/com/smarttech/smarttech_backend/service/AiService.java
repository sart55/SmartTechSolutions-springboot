package com.smarttech.smarttech_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class AiService {

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String schemaContent;

    public AiService(JdbcTemplate jdbcTemplate,
                     RestTemplate restTemplate,
                     RedisTemplate<String, String> redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        loadSchema();
    }

    @Value("${groq.api.key}")
    private String apiKey;

    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    // ✅ FIXED: Load schema safely (WORKS IN JAR + LOCAL)
    private void loadSchema() {
        try {
            ClassPathResource resource = new ClassPathResource("ai/db-schema.md");

            try (InputStream inputStream = resource.getInputStream()) {
                schemaContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load schema file", e);
        }
    }

    // 🔥 SESSION KEY
    private String getSessionKey(String username) {
        return "chat_history:" + username;
    }

    // 🔥 GET CHAT HISTORY
    private List<Map<String, String>> getChatHistory(String username) {

        List<String> list = redisTemplate.opsForList()
                .range(getSessionKey(username), 0, -1);

        List<Map<String, String>> history = new ArrayList<>();

        if (list != null) {
            for (String json : list) {
                try {
                    Map<String, String> map =
                            objectMapper.readValue(json, Map.class);
                    history.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return history;
    }

    // 🔥 SAVE MESSAGE
    private void saveMessage(String username, String role, String content) {

        try {
            Map<String, String> msg = new HashMap<>();
            msg.put("role", role);
            msg.put("content", content);

            String json = objectMapper.writeValueAsString(msg);

            redisTemplate.opsForList()
                    .rightPush(getSessionKey(username), json);

            redisTemplate.opsForList()
                    .trim(getSessionKey(username), -10, -1);

            redisTemplate.expire(getSessionKey(username), Duration.ofMinutes(30));

        } catch (Exception e) {
            throw new RuntimeException("Error saving message", e);
        }
    }

    // 🔥 WITHOUT MEMORY GROQ CALL
    private String callGroqWithoutMemory(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama-3.3-70b-versatile");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));

        request.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                GROQ_URL,
                entity,
                Map.class
        );

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
    }

    // 🔥 MAIN METHOD
    public Object processQuery(String question, String username) {

        String type = classifyQuestion(question);

        List<Map<String, String>> history = getChatHistory(username);

        if (!history.isEmpty()) {
            String lastAssistantMsg = history.get(history.size() - 1).get("content");

            if (lastAssistantMsg != null && lastAssistantMsg.toLowerCase().contains("select")) {
                type = "SQL";
            }
        }

        if (question.toLowerCase().matches(".*(all|list|show|get|find|email|name|amount|projects|customers|last|top).*")) {
            type = "SQL";
        }

        if (type.equalsIgnoreCase("SQL")) {

            String sql = convertToSQL(question);
            System.out.println("SQL: " + sql);

            String lowerSql = sql.toLowerCase();

            if (!lowerSql.startsWith("select") ||
                    lowerSql.contains("delete") ||
                    lowerSql.contains("update") ||
                    lowerSql.contains("drop") ||
                    lowerSql.contains("insert")) {
                throw new RuntimeException("Unsafe query blocked!");
            }

            saveMessage(username, "user", question);
            saveMessage(username, "assistant", sql);

            return jdbcTemplate.queryForList(sql);

        } else {
            return Map.of("response",
                    callGroqWithMemory(username, question));
        }
    }

    // 🔥 GROQ WITH MEMORY
    private String callGroqWithMemory(String username, String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama-3.3-70b-versatile");

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", """
You are an SQL assistant for SmartTechSolutions database.

STRICT RULES:
- Generate ONLY SELECT queries
- Do NOT guess names
- No explanation
- PostgreSQL syntax only
- Use EXACT table and column names from schema
- Always use JOIN when data is from multiple tables
- Use given foreign key relationships only
- Never use camelCase, always use snake_case
"""
        ));

        messages.addAll(getChatHistory(username));

        messages.add(Map.of("role", "user", "content", prompt));

        request.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                GROQ_URL,
                entity,
                Map.class
        );

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        String reply = message.get("content").toString();

        saveMessage(username, "user", prompt);
        saveMessage(username, "assistant", reply);

        return reply;
    }

    // 🔥 CLASSIFICATION
    private String classifyQuestion(String question) {

        String prompt = """
Classify:
SQL -> if question needs database query
GENERAL -> otherwise

Only return one word.

Question:
""" + question;

        return callGroqWithoutMemory(prompt).trim();
    }

    // 🔥 SQL GENERATION
    private String convertToSQL(String question) {

        String prompt = """
Database schema:
""" + schemaContent + """

IMPORTANT:
- Only SELECT queries
- Use exact names (snake_case only)
- Add LIMIT 50
- Use LOWER() for text comparisons
- If question asks "email", return email column
- If question asks "all customers", use customers table

Question:
""" + question;

        String sql = callGroqWithoutMemory(prompt);

        return sql.replace("```sql", "")
                .replace("```", "")
                .trim();
    }

    // 🔥 GENERAL RESPONSE
    private String getGeneralAnswer(String question) {

        String prompt = """
Answer in simple and clear terms:

""" + question;

        return callGroqWithMemory("system", prompt);
    }
}
