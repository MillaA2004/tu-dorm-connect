package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Post.PostCreateRequest;
import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.dto.Post.PostUpdateRequest;
import com.tuconnect.dorm_connect.mapper.PostMapper;
import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.PostRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.PostService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostResponse createPost(PostCreateRequest request, Authentication authentication) {

        User author = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank()) throw new RuntimeException("Post content cannot be empty");
        if (content.length() > 1000) throw new RuntimeException("Post content is too long");

        Post post = Post.builder()
                .author(author)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        return postMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public void deletePost(Long postId, Authentication authentication) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = post.getAuthor().getId().equals(user.getId());


        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("Admin");

        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("Not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Authentication authentication) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to edit this post");
        }

        post.setContent(request.content());
        post.setCreatedAt(LocalDateTime.now());

        var saved = postRepository.save(post);
        return postMapper.toDTO(saved);
    }

    @Override
    public List<PostResponse> getAll() {
        return postRepository.findFeed()
                .stream()
                .map(postMapper::toDTO)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long id) {
        return postRepository.findByIdWithAll(id)
                .map(postMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<PostResponse> getPostByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        return postRepository.findByAuthorIdWithAll(userId)
                .stream()
                .map(postMapper::toDTO)
                .toList();
    }

}
