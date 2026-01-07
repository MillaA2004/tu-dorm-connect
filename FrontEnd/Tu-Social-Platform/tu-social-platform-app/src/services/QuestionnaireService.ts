import apiClient from "./apiClient";

const API_BASE = "/api/questionnaires";

export interface QuestionnaireData {
  smokes: boolean | null;
  drinks: boolean | null;
  partyHome: boolean | null;
  stayAtHome: boolean | null;
  cleanliness: number | null;
  sharesCleaning: boolean | null;
  mbti: string;
  age: number | null;
  specialty: string;
  earlyRiser: boolean | null;
  bedtime: number | null;
  studiesInRoom: boolean | null;
  needsQuiet: number | null;
  guestFrequency: number | null;
  prefersSocialRoommate: boolean | null;
  cooksInDorm: boolean | null;
  foodSharing: number | null;
  entertainmentFrequency: number | null;
  usesHeadphones: boolean | null;
  personalSpaceImportance: number | null;
  sharesItems: boolean | null;
}

export const questionnaireService = {
  async hasCompleted(userId: number): Promise<boolean> {
    try {
      const res = await apiClient.get(`${API_BASE}/${userId}`);
      return !!res.data;
    } catch (err) {
      console.error("Failed to check questionnaire status", err);
      return false;
    }
  },

  async getByUser(userId: number): Promise<QuestionnaireData | null> {
    try {
      const res = await apiClient.get(`${API_BASE}/${userId}`);
      return res.data;
    } catch (err) {
      console.error("Failed to fetch questionnaire", err);
      return null;
    }
  },

  async saveForUser(
    userId: number,
    data: QuestionnaireData
  ): Promise<QuestionnaireData> {
    const res = await apiClient.post(`${API_BASE}/${userId}`, data);
    return res.data;
  },
};