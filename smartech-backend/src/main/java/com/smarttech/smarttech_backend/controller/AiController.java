package com.smarttech.smarttech_backend.controller;

import com.smarttech.smarttech_backend.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {

        String question = body.get("question");
        String username = body.get("username"); // 🔥 REQUIRED

        return ResponseEntity.ok(aiService.processQuery(question, username));
    }
}
