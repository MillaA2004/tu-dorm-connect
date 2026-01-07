import React, { useState } from "react";
import PostList from "../components/PostList";
import CreatePostForm from "../components/CreatePostForm";
import "../styles/PostCard.css";
import Header from "../components/Header";

const PostPage: React.FC = () => {
  
  const [refreshKey, setRefreshKey] = useState(0);

  const refresh = () => setRefreshKey((k) => k + 1);

  return (
    <>
    <Header/>
    <div className="postsPage">
      <h1 className="postsTitle">Posts</h1>

      <CreatePostForm onCreated={refresh} />

      <div className="postsFeed">
        <PostList refreshKey={refreshKey} />
      </div>
    </div>
    </>
  );
};

export default PostPage;
