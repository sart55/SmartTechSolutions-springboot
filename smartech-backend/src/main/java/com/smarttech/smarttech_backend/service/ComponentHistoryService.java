package com.smarttech.smarttech_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.smarttech.smarttech_backend.entity.ComponentHistory;
import com.smarttech.smarttech_backend.repository.ComponentHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentHistoryService {

    private final ComponentHistoryRepository componentHistoryRepository;

    public List<ComponentHistory> getAllHistory() {
        return componentHistoryRepository.findAllByOrderByDateDesc();
    }

    // ✅ Method to save history
    public void saveHistory(String name, int quantity, double price, String admin) {

        ComponentHistory history = new ComponentHistory();
        history.setName(name);
        history.setQuantity(quantity);
        history.setPrice(price);
        history.setAddedBy(admin);

        componentHistoryRepository.save(history);
    }
}