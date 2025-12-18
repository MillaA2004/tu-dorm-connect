import React, { useState } from "react";
import { postService } from "../services/PostService";
import "../styles/PostCard.css";
type Props = {
  onCreated?: () => void;
};

const CreatePostForm: React.FC<Props> = ({ onCreated }) => {
  const [content, setContent] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    const trimmed = content.trim();
    if (!trimmed) return;

    setBusy(true);
    setError(null);

    try {
      await postService.createPost({ content: trimmed });
      setContent("");
      onCreated?.();
    } catch (err) {
      console.error("Create post failed:", err);
      setError("Could not create post. Please try again.");
    } finally {
      setBusy(false);
    }
  };


return (
  <section className="createPost">
    <div className="createPostCard">
      <form onSubmit={submit} className="createPostForm">
        <textarea
          className="createPostInput"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What's on your mind?"
          rows={3}
          disabled={busy}
        />

        {error && <div className="errorText">{error}</div>}

        <div className="createPostActions">
          <button
            type="submit"
            className="hero-action-button"
            disabled={busy || !content.trim()}
          >
            Post
          </button>
        </div>
      </form>
    </div>
  </section>
);

};

export default CreatePostForm;
