import apiClient from "./apiClient";
import type { NotificationItem } from "../types";

const NOTIFICATIONS_BASE = "/api/notifications";


export interface NotificationDTO {
  id: number;
  type: "POST_COMMENTED" | "EVENT_JOINED"; 
  targetType: "POST" | "EVENT";
  targetId: number;
  message: string | null;
  read: boolean;
  createdAt: string;
  actorId: number | null;
  actorName: string | null;
  actorImageUrl: string | null;
}

export interface UnreadCountResponse {
  unreadCount: number;
}


const mapDtoToNotificationItem = (dto: NotificationDTO): NotificationItem => {
  const title =
    dto.type === "POST_COMMENTED"
      ? "New comment"
      : dto.type === "EVENT_JOINED"
      ? "New participant"
      : "Notification";

  const description =
    dto.actorName
      ? `${dto.actorName} ${dto.message ?? ""}`.trim()
      : dto.message ?? "";

  
  const uiType: NotificationItem["type"] =
    dto.targetType === "POST" ? "comment" : dto.targetType === "EVENT" ? "event" : "system";

  return {
    id: String(dto.id),
    title,
    description,
    createdAt: dto.createdAt,
    isRead: dto.read,
    type: uiType,

    
  };
};


export const getNotificationRoute = (dto: NotificationDTO): string => {
  if (dto.targetType === "POST") return `/posts/${dto.targetId}`;
  if (dto.targetType === "EVENT") return `/events/${dto.targetId}`;
  return "/";
};

export const notificationService = {
  
  getMyNotifications: async (page = 0, size = 20): Promise<NotificationDTO[]> => {
    const res = await apiClient.get<NotificationDTO[]>(NOTIFICATIONS_BASE, {
      params: { page, size },
    });
    return res.data;
  },

  
  getMyNotificationItems: async (page = 0, size = 20): Promise<NotificationItem[]> => {
    const dtos = await notificationService.getMyNotifications(page, size);
    return dtos.map(mapDtoToNotificationItem);
  },

  getUnreadCount: async (): Promise<number> => {
    const res = await apiClient.get<UnreadCountResponse>(`${NOTIFICATIONS_BASE}/unread-count`);
    return res.data.unreadCount;
  },

  markAsRead: async (notificationId: number): Promise<void> => {
    await apiClient.post(`${NOTIFICATIONS_BASE}/${notificationId}/read`);
  },
};
