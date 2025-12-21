import apiClient from "./apiClient";
import type { UserSummary } from "../types";


export interface ReviewRequestDTO {
  rating: number;   
  comment: string;
  dormId: number;
}

export interface ReviewResponseDTO {
  id: number;
  rating: number;
  comment: string;
  createdAt: string; 
  author: UserSummary;
  dormId: number;
}

const REVIEWS_BASE = "/api/reviews";

export const reviewService = {
  getById: async (id: number): Promise<ReviewResponseDTO> => {
    const res = await apiClient.get<ReviewResponseDTO>(`${REVIEWS_BASE}/${id}`);
    return res.data;
  },

  getByDorm: async (dormId: number): Promise<ReviewResponseDTO[]> => {
    const res = await apiClient.get<ReviewResponseDTO[]>(
      `${REVIEWS_BASE}/dorm/${dormId}`
    );
    return res.data;
  },

  create: async (payload: ReviewRequestDTO): Promise<ReviewResponseDTO> => {
    const res = await apiClient.post<ReviewResponseDTO>(REVIEWS_BASE, payload);
    return res.data;
  },

  update: async (id: number, payload: ReviewRequestDTO): Promise<ReviewResponseDTO> => {
    const res = await apiClient.put<ReviewResponseDTO>(`${REVIEWS_BASE}/${id}`, payload);
    return res.data;
  },

  remove: async (id: number): Promise<void> => {
    await apiClient.delete(`${REVIEWS_BASE}/${id}`);
  },
};
