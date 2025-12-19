import apiClient from "./apiClient";
import type { UserSummary } from "../types";
import type { CommentResponse } from "./CommentService";

export interface PostResponse {
  id: number;
  content: string;
  createdAt: string;
  author: UserSummary;
  comments: CommentResponse[];
}

export interface PostCreateRequest {
  content: string;
}

export interface PostUpdateRequest {
  content: string;
}

const POSTS_BASE = "/api/posts";

export const postService = {
  getAllPosts: async (): Promise<PostResponse[]> => {
    const res = await apiClient.get<PostResponse[]>(POSTS_BASE);
    return res.data;
  },

  createPost: async (payload: PostCreateRequest): Promise<PostResponse> => {
    const res = await apiClient.post<PostResponse>(POSTS_BASE, payload);
    return res.data;
  },

  deletePost: async (postId: number): Promise<void> => {
    await apiClient.delete(`${POSTS_BASE}/${postId}`);
  },

  getPostById: async (postId: number): Promise<PostResponse> => {
    const res = await apiClient.get<PostResponse>(`${POSTS_BASE}/${postId}`);
    return res.data;
  },

  updatePost: async (postId: number, payload: PostUpdateRequest): Promise<PostResponse> => {
  const res = await apiClient.put<PostResponse>(`${POSTS_BASE}/${postId}`, payload);
  return res.data;
},

getPostsByUser: async (userId: number): Promise<PostResponse[]> => {
    const res = await apiClient.get<PostResponse[]>(`${POSTS_BASE}/user/${userId}`);
    return res.data;
  },

};
