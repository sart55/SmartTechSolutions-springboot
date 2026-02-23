package com.smarttech.smarttech_backend.service;

import com.smarttech.smarttech_backend.entity.Project;
import com.smarttech.smarttech_backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project save(Project project) {

        // ✅ DUPLICATE CHECK
        boolean exists = projectRepository
                .existsByProjectNameAndCustomerContact(
                        project.getProjectName(),
                        project.getCustomerContact()
                );

        if (exists) {
            throw new RuntimeException(
                    "Project already exists with same Project Name and Contact Number"
            );
        }

        // ✅ SET DEFAULT VALUES (VERY IMPORTANT)
        if (project.getQuotationCreated() == null) {
            project.setQuotationCreated(false);
        }

        if (project.getStatus() == null) {
            project.setStatus("OPEN");
        }

        return projectRepository.save(project);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project update(Long id, Project project) {

        Project existing = findById(id);

        // ✅ DUPLICATE CHECK (Ignore same project ID)
        boolean exists = projectRepository
                .existsByProjectNameAndCustomerContactAndIdNot(
                        project.getProjectName(),
                        project.getCustomerContact(),
                        id
                );

        if (exists) {
            throw new RuntimeException(
                    "Project already exists with same Project Name and Contact Number"
            );
        }

        existing.setProjectName(project.getProjectName());
        existing.setCustomerName(project.getCustomerName());
        existing.setCustomerContact(project.getCustomerContact());
        existing.setCustomerEmail(project.getCustomerEmail());
        existing.setCustomerCollege(project.getCustomerCollege());
        existing.setCustomerBranch(project.getCustomerBranch());
        existing.setDescription(project.getDescription());

        return projectRepository.save(existing);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public Project closeProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setStatus("CLOSED"); // Assuming you have a `status` field with values "OPEN"/"CLOSED"

        return projectRepository.save(project);
    }
}