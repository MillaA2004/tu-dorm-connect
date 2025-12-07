package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
}
