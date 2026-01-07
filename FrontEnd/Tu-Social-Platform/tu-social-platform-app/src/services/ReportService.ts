import apiClient from "./apiClient";

export type ReportTargetType = "EVENT" | "POST" | "USER"; 

export interface ReportRequest {
  targetId: number;
  targetType: ReportTargetType;
  reason: string;
}

export interface ReportResponse {
  reportId: number;
  targetId: number;
  targetType: ReportTargetType;
  reason: string;
  reporterId: number;
  createdAt: string; 
  isViewed: boolean;
}

const REPORTS_BASE = "/api/reports";

export const reportService = {
  
  submitReport: async (payload: ReportRequest): Promise<ReportResponse> => {
    const res = await apiClient.post<ReportResponse>(REPORTS_BASE, payload);
    return res.data;
  },

  
  getAllReports: async (): Promise<ReportResponse[]> => {
    const res = await apiClient.get<ReportResponse[]>(REPORTS_BASE);
    return res.data;
  },

  
  markAsViewed: async (reportId: number): Promise<void> => {
    await apiClient.post(`${REPORTS_BASE}/reports/viewed`, null, {
      params: { reportId },
    });
  },
};
