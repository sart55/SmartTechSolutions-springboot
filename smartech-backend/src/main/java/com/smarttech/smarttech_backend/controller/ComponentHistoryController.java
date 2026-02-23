package com.smarttech.smarttech_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smarttech.smarttech_backend.entity.ComponentHistory;
import com.smarttech.smarttech_backend.service.ComponentHistoryService;

import java.util.List;

@RestController
@RequestMapping("/component-history") // ✅ match frontend
@RequiredArgsConstructor
@CrossOrigin
public class ComponentHistoryController {

    private final ComponentHistoryService componentHistoryService;

    @GetMapping
    public ResponseEntity<List<ComponentHistory>> getAllHistory() {
        return ResponseEntity.ok(componentHistoryService.getAllHistory());
    }
}