package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.Comment.CommentCreateRequest;
import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.mapper.CommentMapper;
import com.tuconnect.dorm_connect.model.Comment;
import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.CommentRepository;
import com.tuconnect.dorm_connect.repository.PostRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentResponse addCommentToPost(CommentCreateRequest request) {

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        return commentMapper.toDTO(saved);
    }

    @Override
    public List<CommentResponse> getCommentsForPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}
