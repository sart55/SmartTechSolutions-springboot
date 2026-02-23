package com.smarttech.smarttech_backend.controller;

import com.smarttech.smarttech_backend.entity.Comment;
import com.smarttech.smarttech_backend.service.CommentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/project/{projectId}")
    public Comment saveComment(@PathVariable Long projectId, @RequestBody Comment comment,
                               Authentication authentication){
        String username = authentication.getName();
        return commentService.saveComment(projectId, comment, username);
    }

    @GetMapping("/project/{projectId}")
    public List<Comment> getComments(@PathVariable Long projectId) {
        return commentService.getCommentsByProject(projectId);
    }

    @DeleteMapping("/project/{projectId}/all")
    public void deleteAllCommentsByProject(@PathVariable Long projectId,
                                           Authentication authentication) {

//        // Optional: ensure authenticated user exists
//        String username = authentication.getName();

        commentService.deleteAllCommentsByProject(projectId);
    }

}