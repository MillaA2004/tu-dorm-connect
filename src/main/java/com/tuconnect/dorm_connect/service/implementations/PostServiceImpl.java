package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.Post.PostCreateRequest;
import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.mapper.PostMapper;
import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.PostRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.PostService;
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
    public PostResponse createPost(PostCreateRequest request) {

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .author(author)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        return postMapper.toDTO(saved);
    }

    @Override
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(postId);
    }

    @Override
    public List<PostResponse> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toDTO)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<PostResponse> getPostByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByAuthor(user)
                .stream()
                .map(postMapper::toDTO)
                .toList();
    }
}
