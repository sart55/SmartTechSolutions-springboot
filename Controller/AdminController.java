package com.smarttech.smarttech_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smarttech.smarttech_backend.entity.Admin;
import com.smarttech.smarttech_backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create")
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }
}
