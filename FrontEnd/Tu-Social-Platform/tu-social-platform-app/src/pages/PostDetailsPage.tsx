import React, { useCallback, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { postService, type PostResponse } from "../services/PostService";
import PostCard from "../components/PostCard";
import Header from "../components/Header";

const PostDetailsPage: React.FC = () => {
  const { postId } = useParams<{ postId: string }>();
  const navigate = useNavigate();
  const HEADER_HEIGHT = 100;
  const [post, setPost] = useState<PostResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    if (!postId) return;

    setLoading(true);
    setError(null);

    try {
      const data = await postService.getPostById(Number(postId));
      setPost(data);
    } catch (e) {
      console.error("Failed to load post:", e);
      setError("Could not load this post.");
      setPost(null);
    } finally {
      setLoading(false);
    }
  }, [postId]);

  useEffect(() => {
    void load();
  }, [load]);


return (
    <>
      <Header />

      <main
        className="postsPage"
        style={{ paddingTop: HEADER_HEIGHT }}
      >
        {loading && <div className="pageStatus">Loading…</div>}

        {error && (
          <div className="pageStatus error">
            {error}
          </div>
        )}

        {!loading && !error && !post && (
          <div className="pageStatus">Post not found.</div>
        )}

        {post && (
          <div className="postsFeed">
            <PostCard
              post={post}
              defaultCommentsOpen
              onPostDeleted={() => navigate("/posts")}
              onPostUpdated={load}
              onCommentChanged={load}
            />
          </div>
        )}
      </main>
    </>
  );
};

export default PostDetailsPage;
