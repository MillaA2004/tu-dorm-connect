import apiClient from "./apiClient";
import type { UserMatchDTO } from "../types";

const API_BASE = "/api/matches";

class MatchService {
  /**
   * Get matches for a specific viewer/user
   * @param viewerId - The ID of the user viewing matches
   * @param minScore - Optional minimum match score filter (0-100)
   * @returns Array of user matches sorted by score
   */
  async getMatchesForViewer(
    viewerId: number,
    minScore?: number
  ): Promise<UserMatchDTO[]> {
    try {
      const params: any = {};
      if (minScore !== undefined) {
        params.minScore = minScore;
      }

      const res = await apiClient.get(`${API_BASE}/${viewerId}`, { params });
      return res.data;
    } catch (err) {
      console.error("Failed to fetch matches", err);
      throw err;
    }
  }

  /**
   * Get all matches in the system (admin/debug use)
   * @param minScore - Optional minimum match score filter (0-100)
   * @returns Array of all user matches
   */
  async getAllMatches(minScore?: number): Promise<UserMatchDTO[]> {
    try {
      const params: any = {};
      if (minScore !== undefined) {
        params.minScore = minScore;
      }

      const res = await apiClient.get(`${API_BASE}/all`, { params });
      return res.data;
    } catch (err) {
      console.error("Failed to fetch all matches", err);
      throw err;
    }
  }
}

export const matchService = new MatchService();