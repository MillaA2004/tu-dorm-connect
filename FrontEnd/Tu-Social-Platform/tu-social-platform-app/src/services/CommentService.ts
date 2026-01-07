import apiClient from "./apiClient";
import type { UserSummary } from "../types";

export interface CommentResponse {
  id: number;
  content: string;
  createdAt: string;
  author: UserSummary;
}

export interface CommentCreateRequest {
  postId: number;
  content: string;
}

export interface CommentUpdateRequest {
  content: string;
}

const COMMENTS_BASE = "/api/comments";

export const commentService = {
  addComment: async (
    payload: CommentCreateRequest
  ): Promise<CommentResponse> => {
    const res = await apiClient.post<CommentResponse>(COMMENTS_BASE, payload);
    return res.data;
  },

  editComment: async (
    commentId: number,
    payload: CommentUpdateRequest
  ): Promise<CommentResponse> => {
    const res = await apiClient.put<CommentResponse>(
      `${COMMENTS_BASE}/${commentId}`,
      payload
    );
    return res.data;
  },

  deleteComment: async (commentId: number): Promise<void> => {
    await apiClient.delete(`${COMMENTS_BASE}/${commentId}`);
  },
};
