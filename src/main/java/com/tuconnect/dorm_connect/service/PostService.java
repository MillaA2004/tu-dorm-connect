package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Post.PostCreateRequest;
import com.tuconnect.dorm_connect.dto.Post.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse createPost(PostCreateRequest request);

    void deletePost(Long postId);

    List<PostResponse> getAll();

    PostResponse getPostById(Long id);

    List<PostResponse> getPostByUser(Long userId);
}
