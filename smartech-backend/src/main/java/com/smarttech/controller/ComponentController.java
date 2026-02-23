package com.smarttech.smarttech_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smarttech.smarttech_backend.entity.Component;
import com.smarttech.smarttech_backend.service.ComponentService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/components")
@CrossOrigin
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<Component> createComponent(
            @RequestBody Component component,
            Principal principal) {

        // Set lastUpdatedBy from logged-in user
        if (principal != null) {
            component.setLastUpdatedBy(principal.getName());
        } else {
            component.setLastUpdatedBy("System");
        }

        return ResponseEntity.ok(componentService.save(component));
    }

    /* ================= EXCEL UPLOAD ================= */

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        componentService.saveFromExcel(file);

        return ResponseEntity.ok("File uploaded successfully");
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public ResponseEntity<Component> updateComponent(
            @PathVariable Long id,
            @RequestBody Component updatedComponent,
            Principal principal) {

        // Always update lastUpdatedBy
        if (principal != null) {
            updatedComponent.setLastUpdatedBy(principal.getName());
        } else {
            updatedComponent.setLastUpdatedBy("System");
        }

        return ResponseEntity.ok(
                componentService.updateComponent(id, updatedComponent)
        );
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComponent(@PathVariable Long id) {

        componentService.deleteComponent(id);
        return ResponseEntity.ok("Component deleted successfully");
    }

    /* ================= GET ================= */

    @GetMapping
    public List<Component> getAllComponents() {
        return componentService.getAllComponents();
    }
}