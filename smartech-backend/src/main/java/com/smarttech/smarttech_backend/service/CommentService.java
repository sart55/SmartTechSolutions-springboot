package com.smarttech.smarttech_backend.service;

import com.smarttech.smarttech_backend.entity.Comment;
import com.smarttech.smarttech_backend.entity.Project;
import com.smarttech.smarttech_backend.repository.CommentRepository;
import com.smarttech.smarttech_backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;

    public CommentService(CommentRepository commentRepository, ProjectRepository projectRepository) {
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
    }
    @Transactional
    public void deleteAllCommentsByProject(Long projectId) {
        commentRepository.deleteByProject_Id(projectId);
    }

    public Comment saveComment(Long projectId, Comment comment, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        comment.setProject(project);
        comment.setUsername(username);
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByProject(Long projectId) {
        return commentRepository.findByProject_Id(projectId);
    }
}