package com.smarttech.smarttech_backend.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smarttech.smarttech_backend.entity.Component;
import com.smarttech.smarttech_backend.entity.ComponentHistory;
import com.smarttech.smarttech_backend.repository.ComponentRepository;
import com.smarttech.smarttech_backend.repository.ComponentHistoryRepository;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;
    private final ComponentHistoryRepository componentHistoryRepository;

    /* ==========================================================
       SAVE FROM EXCEL
       ========================================================== */

    public void saveFromExcel(MultipartFile file) {

        try {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String loggedUser = authentication.getName();

            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue; // Skip header

                String name = row.getCell(0).getStringCellValue();
                double price = row.getCell(1).getNumericCellValue();
                int quantity = (int) row.getCell(2).getNumericCellValue();

                Component component = new Component();
                component.setName(name);
                component.setPrice(price);
                component.setQuantity(quantity);
                component.setLastUpdatedBy(loggedUser);

                Component savedComponent = componentRepository.save(component);

                // ✅ Save History
                saveHistory(savedComponent, loggedUser);
            }

            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("Error processing Excel file", e);
        }
    }

    /* ==========================================================
       SAVE / ADD COMPONENT
       ========================================================== */

    public Component save(Component component) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String loggedUser = authentication.getName();

        Optional<Component> existing =
                componentRepository.findByName(component.getName());

        Component savedComponent;

        if (existing.isPresent()) {

            Component comp = existing.get();
            comp.setQuantity(comp.getQuantity() + component.getQuantity());
            comp.setPrice(component.getPrice());
            comp.setLastUpdatedBy(loggedUser);

            savedComponent = componentRepository.save(comp);

        } else {

            component.setLastUpdatedBy(loggedUser);
            savedComponent = componentRepository.save(component);
        }

        // ✅ Save History
        saveHistory(savedComponent, loggedUser);

        return savedComponent;
    }

    /* ==========================================================
       UPDATE COMPONENT
       ========================================================== */

    public Component updateComponent(Long id, Component updatedComponent) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String loggedUser = authentication.getName();

        Component existing = componentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        existing.setName(updatedComponent.getName());
        existing.setPrice(updatedComponent.getPrice());
        existing.setQuantity(updatedComponent.getQuantity());
        existing.setLastUpdatedBy(loggedUser);

        Component saved = componentRepository.save(existing);

        // ✅ Save History
        saveHistory(saved, loggedUser);

        return saved;
    }

    /* ==========================================================
       DELETE COMPONENT
       ========================================================== */

    public void deleteComponent(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String loggedUser = authentication.getName();

        Component existing = componentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        // ✅ Save history BEFORE delete
        saveHistory(existing, loggedUser);

        componentRepository.delete(existing);
    }

    /* ==========================================================
       GET ALL
       ========================================================== */

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    /* ==========================================================
       PRIVATE METHOD – SAVE HISTORY
       ========================================================== */

    private void saveHistory(Component component, String admin) {

        ComponentHistory history = new ComponentHistory();
        history.setName(component.getName());       // ✅ matches entity
        history.setQuantity(component.getQuantity());
        history.setPrice(component.getPrice());
        history.setAddedBy(admin);                 // ✅ matches entity

        componentHistoryRepository.save(history);
    }
}