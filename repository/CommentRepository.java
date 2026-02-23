package com.smarttech.smarttech_backend.repository;

import com.smarttech.smarttech_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProject_Id(Long projectId);
    void deleteByProject_Id(Long projectId);
}