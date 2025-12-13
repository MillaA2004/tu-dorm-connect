package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Comment.CommentCreateRequest;
import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public CommentResponse addComment(@RequestBody CommentCreateRequest request) {
        return commentService.addCommentToPost(request);
    }

    @GetMapping("/post/{postId}")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getCommentsForPost(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
