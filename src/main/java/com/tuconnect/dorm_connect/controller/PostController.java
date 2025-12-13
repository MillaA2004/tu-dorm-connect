package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Post.PostCreateRequest;
import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostResponse createPost(@RequestBody PostCreateRequest request) {
        return postService.createPost(request);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    @GetMapping
    public List<PostResponse> getAll() {
        return postService.getAll();
    }

    @GetMapping("/{postId}")
    public PostResponse getById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/user/{userId}")
    public List<PostResponse> getByUser(@PathVariable Long userId) {
        return postService.getPostByUser(userId);
    }
}
