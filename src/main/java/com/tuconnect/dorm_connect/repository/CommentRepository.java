package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Comment;
import com.tuconnect.dorm_connect.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
