import React, { useCallback, useEffect, useState } from "react";
import { postService, type PostResponse } from "../services/PostService";
import PostCard from "./PostCard";
import { useNavigate } from "react-router-dom";

type Props = {
  userId: number;
  refreshKey?: number;
};

const UserPostList: React.FC<Props> = ({ userId, refreshKey }) => {
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  const loadPosts = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await postService.getPostsByUser(userId);
      setPosts(data);
    } catch (e) {
      console.error("Failed to load user posts:", e);
      setError("Could not load posts. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    void loadPosts();
  }, [loadPosts, refreshKey]);

  if (loading) return <div>Loading posts…</div>;

  if (error) {
    return (
      <div style={{ display: "grid", gap: 8 }}>
        <div style={{ color: "crimson" }}>{error}</div>
        <button onClick={() => void loadPosts()}>Retry</button>
      </div>
    );
  }

  if (posts.length === 0) return <div>No posts yet.</div>;

  return (
    <div style={{ display: "grid", gap: 16 }}>
      {posts.map((post) => (
        <PostCard
          key={post.id}
          post={post}
          clickable
          onOpen={(id) => navigate(`/posts/${id}`)}
          defaultCommentsOpen={false}
          onPostDeleted={loadPosts}
          onPostUpdated={loadPosts}
          onCommentChanged={loadPosts}
        />
      ))}
    </div>
  );
};

export default UserPostList;
