package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Comment.CommentCreateRequest;
import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.dto.Comment.CommentUpdateRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CommentService {

    CommentResponse addCommentToPost(CommentCreateRequest request, Authentication authentication);

    List<CommentResponse> getCommentsForPost(Long postId);

    void deleteComment(Long commentId);

    CommentResponse editComment(Long commentId, CommentUpdateRequest request, Authentication authentication);
}
