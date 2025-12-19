import React, { useEffect, useMemo, useState } from "react";
import type { PostResponse } from "../services/PostService";
import { postService } from "../services/PostService";
import { commentService } from "../services/CommentService";
import { useAuth } from "../services/AuthContext";
import { Link } from "react-router-dom";

type Props = {
  post: PostResponse;
  onPostDeleted?: () => void;
  onPostUpdated?: () => void;
  onCommentChanged?: () => void;

  clickable?: boolean;
  onOpen?: (postId: number) => void;
  defaultCommentsOpen?: boolean;
};

function formatDate(iso: string): string {
  const d = new Date(iso);
  return isNaN(d.getTime()) ? iso : d.toLocaleString();
}

function initials(first?: string, last?: string) {
  return `${first?.[0] ?? ""}${last?.[0] ?? ""}` || "?";
}

const PostCard: React.FC<Props> = ({
  post,
  onPostDeleted,
  onPostUpdated,
  onCommentChanged,
  clickable = false,
  onOpen,
  defaultCommentsOpen = false,
}) => {
  const { user } = useAuth();

  const isMyPost = useMemo(
    () => post.author.id === user?.id,
    [post.author.id, user?.id]
  );

 
  const [commentsOpen, setCommentsOpen] =
    useState<boolean>(defaultCommentsOpen);

  
  const [newComment, setNewComment] = useState("");
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editingText, setEditingText] = useState("");

 
  const [editingPost, setEditingPost] = useState(false);
  const [postEditText, setPostEditText] = useState(post.content);


  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  
  useEffect(() => {
    setPostEditText(post.content);
  }, [post.id, post.content]);

  
  useEffect(() => {
    setCommentsOpen(defaultCommentsOpen);
  }, [defaultCommentsOpen, post.id]);

  const openPost = () => {
    if (clickable && onOpen) onOpen(post.id);
  };

  /* ======================
     POST ACTIONS
  ====================== */

  const deletePost = async () => {
    setBusy(true);
    setError(null);
    try {
      await postService.deletePost(post.id);
      onPostDeleted?.();
    } catch (e) {
      console.error(e);
      setError("Failed to delete post.");
    } finally {
      setBusy(false);
    }
  };

  const savePostEdit = async () => {
    const content = postEditText.trim();
    if (!content) return;

    setBusy(true);
    setError(null);
    try {
      await postService.updatePost(post.id, { content });
      setEditingPost(false);
      onPostUpdated?.();
    } catch (e) {
      console.error(e);
      setError("Failed to edit post.");
    } finally {
      setBusy(false);
    }
  };

  /* ======================
     COMMENT ACTIONS
  ====================== */

  const addComment = async (e: React.FormEvent) => {
    e.preventDefault();
    e.stopPropagation();

    if (!newComment.trim()) return;

    setBusy(true);
    setError(null);
    try {
      await commentService.addComment({
        postId: post.id,
        content: newComment.trim(),
      });
      setNewComment("");
      onCommentChanged?.();
    } catch (e2) {
      console.error(e2);
      setError("Failed to add comment.");
    } finally {
      setBusy(false);
    }
  };

  const saveEditComment = async (commentId: number) => {
    if (!editingText.trim()) return;

    setBusy(true);
    setError(null);
    try {
      await commentService.editComment(commentId, {
        content: editingText.trim(),
      });
      setEditingCommentId(null);
      setEditingText("");
      onCommentChanged?.();
    } catch (e2) {
      console.error(e2);
      setError("Failed to edit comment.");
    } finally {
      setBusy(false);
    }
  };

  const deleteComment = async (commentId: number) => {
    setBusy(true);
    setError(null);
    try {
      await commentService.deleteComment(commentId);
      onCommentChanged?.();
    } catch (e2) {
      console.error(e2);
      setError("Failed to delete comment.");
    } finally {
      setBusy(false);
    }
  };

  /* ======================
     RENDER
  ====================== */

  return (
    <article
      className={`postCard ${clickable ? "postCardClickable" : ""}`}
      onClick={openPost}
      role={clickable ? "button" : undefined}
      tabIndex={clickable ? 0 : undefined}
      onKeyDown={(e) => {
        if (!clickable) return;
        if (e.currentTarget !== e.target) return;

        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          openPost();
        }
      }}
    >
      {/* HEADER */}
      <header className="postHeader">
        <div className="avatar">
          {post.author.profileImageUrl ? (
            <img src={post.author.profileImageUrl} alt="avatar" />
          ) : (
            <div className="avatarFallback">
              {initials(post.author.firstName, post.author.lastName)}
            </div>
          )}
        </div>

        <div className="postMeta">
          <Link
            to={`/profile/${post.author.id}`}
            className="authorName"
            onClick={(e) => e.stopPropagation()}
          >
            {post.author.firstName} {post.author.lastName}
          </Link>

          <div className="postDate">{formatDate(post.createdAt)}</div>
        </div>

        {isMyPost && (
          <div className="postActions" onClick={(e) => e.stopPropagation()}>
            <button
              className="hero-action-button danger"
              disabled={busy}
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                void deletePost();
              }}
            >
              Delete
            </button>

            <button
              className="hero-action-button"
              disabled={busy}
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setError(null);
                setEditingPost((v) => !v);
                if (editingPost) setPostEditText(post.content);
              }}
              style={{ marginLeft: 8 }}
            >
              {editingPost ? "Cancel" : "Edit"}
            </button>
          </div>
        )}
      </header>

      {/* CONTENT / EDIT MODE */}
      {!editingPost ? (
        <div className="postContent">{post.content}</div>
      ) : (
        <div className="postEdit" onClick={(e) => e.stopPropagation()}>
          <textarea
            className="postEditInput"
            value={postEditText}
            onChange={(e) => setPostEditText(e.target.value)}
            onClick={(e) => e.stopPropagation()}
            onKeyDown={(e) => e.stopPropagation()}
            rows={3}
            disabled={busy}
          />
          <div className="postEditActions">
            <button
              className="hero-action-button"
              type="button"
              disabled={busy || !postEditText.trim()}
              onClick={(e) => {
                e.stopPropagation();
                void savePostEdit();
              }}
            >
              Save
            </button>
          </div>
        </div>
      )}

      {error && <div className="errorText">{error}</div>}

      {/* COMMENTS TOGGLE */}
      <div className="commentsToggleRow" onClick={(e) => e.stopPropagation()}>
        <button
          type="button"
          className="linkBtn"
          onClick={(e) => {
            e.stopPropagation();
            setCommentsOpen((v) => !v);
          }}
        >
          {commentsOpen
            ? "Hide comments"
            : `View comments (${post.comments.length})`}
        </button>
      </div>

      {/* COMMENTS */}
      {commentsOpen && (
        <section
          className="commentsSection"
          onClick={(e) => e.stopPropagation()}
        >
          <div className="commentsTitle">Comments ({post.comments.length})</div>

          <div className="commentsList">
            {post.comments.map((c) => {
              const isMine = c.author.id === user?.id;
              const isEditing = editingCommentId === c.id;

              return (
                <div
                  key={c.id}
                  className="commentItem"
                  onClick={(e) => e.stopPropagation()}
                >
                  <div className="avatar small">
                    {c.author.profileImageUrl ? (
                      <img src={c.author.profileImageUrl} alt="avatar" />
                    ) : (
                      <div className="avatarFallback">
                        {initials(c.author.firstName, c.author.lastName)}
                      </div>
                    )}
                  </div>

                  <div className="commentBody">
                    <div className="commentTopRow">
                      <Link
                        to={`/profile/${c.author.id}`}
                        className="authorName"
                        onClick={(e) => e.stopPropagation()}
                      >
                        {c.author.firstName} {c.author.lastName}
                      </Link>

                      <div className="commentDate">{formatDate(c.createdAt)}</div>

                      {isMine && (
                        <div className="commentActions">
                          {!isEditing ? (
                            <>
                              <button
                                className="linkBtn"
                                type="button"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  setEditingCommentId(c.id);
                                  setEditingText(c.content);
                                }}
                              >
                                Edit
                              </button>

                              <button
                                className="linkBtn danger"
                                type="button"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  void deleteComment(c.id);
                                }}
                              >
                                Delete
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                className="linkBtn"
                                type="button"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  void saveEditComment(c.id);
                                }}
                              >
                                Save
                              </button>
                              <button
                                className="linkBtn"
                                type="button"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  setEditingCommentId(null);
                                  setEditingText("");
                                }}
                              >
                                Cancel
                              </button>
                            </>
                          )}
                        </div>
                      )}
                    </div>

                    {!isEditing ? (
                      <div className="commentContent">{c.content}</div>
                    ) : (
                      <textarea
                        value={editingText}
                        onChange={(e) => setEditingText(e.target.value)}
                        onClick={(e) => e.stopPropagation()}
                        onKeyDown={(e) => e.stopPropagation()}
                        rows={2}
                      />
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {/* ADD COMMENT */}
          <form className="addCommentForm" onSubmit={addComment}>
            <textarea
              className="addCommentInput"
              placeholder="Write a comment…"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              onClick={(e) => e.stopPropagation()}
              onKeyDown={(e) => e.stopPropagation()}
              rows={2}
              disabled={busy}
            />
            <div className="addCommentActions">
              <button
                className="hero-action-button"
                type="submit"
                disabled={busy || !newComment.trim()}
                onClick={(e) => e.stopPropagation()}
              >
                Comment
              </button>
            </div>
          </form>
        </section>
      )}
    </article>
  );
};

export default PostCard;
