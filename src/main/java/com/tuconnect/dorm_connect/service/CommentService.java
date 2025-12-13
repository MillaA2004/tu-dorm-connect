package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Comment.CommentCreateRequest;
import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse addCommentToPost(CommentCreateRequest request);

    List<CommentResponse> getCommentsForPost(Long postId);

    void deleteComment(Long commentId);
}
