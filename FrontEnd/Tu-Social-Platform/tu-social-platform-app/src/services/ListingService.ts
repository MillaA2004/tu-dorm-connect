import apiClient from "./apiClient";
import { type ListingItem } from "../types";

const API_BASE = "/listings";

class ListingService {
  // Updated: Accepts optional viewerId for gender filtering
  async getAllListings(viewerId?: number): Promise<ListingItem[]> {
    const params: any = {};
    if (viewerId) params.viewerId = viewerId;

    const res = await apiClient.get(`${API_BASE}/active`, { params });
    return res.data;
  }

  // Updated: Replaced 'type' with 'viewerId' since filtering happens on Backend now
  async searchListings(
    keyword: string,
    viewerId?: number,
    signal?: AbortSignal
  ) {
    const params: any = { keyword };
    if (viewerId) params.viewerId = viewerId;

    const res = await apiClient.get(`${API_BASE}/search`, { params, signal });
    return res.data;
  }

  async getListingById(id: number) {
    const res = await apiClient.get(`${API_BASE}/${id}`);
    return res.data;
  }

  async getListingsByUserId(userId: number): Promise<ListingItem[]> {
    const res = await apiClient.get(`${API_BASE}/user/${userId}`);
    return res.data;
  }

  async createListing(posterId: number, dto: any) {
    const res = await apiClient.post(`${API_BASE}/poster/${posterId}`, dto);
    return res.data;
  }

  async updateListing(id: number, dto: any, currentUserId: number) {
    const res = await apiClient.put(`${API_BASE}/${id}`, dto, {
      params: { currentUserId },
    });
    return res.data;
  }

  async deleteListing(id: number, currentUserId: number) {
    await apiClient.delete(`${API_BASE}/${id}`, {
      params: { currentUserId },
    });
  }
}

export const listingService = new ListingService();