package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;



public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select distinct p
        from Post p
        join fetch p.author
        left join fetch p.comments c
        left join fetch c.author
        order by p.createdAt desc
    """)
    List<Post> findFeed();

    @Query("""
        select distinct p
        from Post p
        join fetch p.author
        left join fetch p.comments c
        left join fetch c.author
        where p.id = :postId
    """)
    Optional<Post> findByIdWithAll(@Param("postId") Long postId);

    @Query("""
        select distinct p
        from Post p
        join fetch p.author
        left join fetch p.comments c
        left join fetch c.author
        where p.author.id = :userId
        order by p.createdAt desc
    """)
    List<Post> findByAuthorIdWithAll(@Param("userId") Long userId);
}

