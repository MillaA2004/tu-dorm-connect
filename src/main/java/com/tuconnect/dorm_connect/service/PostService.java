package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Post.PostCreateRequest;
import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.dto.Post.PostUpdateRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {

    PostResponse createPost(PostCreateRequest request, Authentication authentication);

    void deletePost(Long postId, Authentication authentication);

    List<PostResponse> getAll();

    PostResponse getPostById(Long id);

    List<PostResponse> getPostByUser(Long userId);

    PostResponse updatePost(Long postId, PostUpdateRequest request, Authentication authentication);
}
