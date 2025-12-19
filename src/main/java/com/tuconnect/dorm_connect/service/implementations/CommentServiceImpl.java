package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Comment.CommentCreateRequest;
import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.dto.Comment.CommentUpdateRequest;
import com.tuconnect.dorm_connect.mapper.CommentMapper;
import com.tuconnect.dorm_connect.model.Comment;
import com.tuconnect.dorm_connect.model.Post;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.CommentRepository;
import com.tuconnect.dorm_connect.repository.PostRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.CommentService;
import com.tuconnect.dorm_connect.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper,
                              NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.notificationService=notificationService;
    }



    @Override
    public CommentResponse addCommentToPost(CommentCreateRequest request,
                                            Authentication authentication) {

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String email = authentication.getName();

        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        if (!post.getAuthor().getId().equals(author.getId())) {
            notificationService.notifyPostCommented(
                    post.getAuthor().getId(),
                    author.getId(),
                    post.getId()
            );
        }

        return commentMapper.toDTO(saved);
    }


    @Override
    public CommentResponse editComment(Long commentId,
                                       CommentUpdateRequest request,
                                       Authentication authentication) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        String email = authentication.getName();
        User editor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAuthor = comment.getAuthor().getId().equals(editor.getId());

        boolean isAdmin = editor.getRole() != null && editor.getRole().name().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("Not allowed to edit this comment");
        }

        String newContent = request.content() == null ? "" : request.content().trim();
        if (newContent.isBlank()) {
            throw new RuntimeException("Comment content cannot be empty");
        }
        if (newContent.length() > 600) {
            throw new RuntimeException("Comment content is too long");
        }

        comment.setContent(newContent);

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
