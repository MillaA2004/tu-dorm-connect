import type { StompSubscription } from "@stomp/stompjs";
import { chatSocket } from "./ChatSocket";
import type { NotificationDTO } from "./NotificationService";

export const notificationSocket = {
  subscribeToNotifications: async (
    onNotification: (dto: NotificationDTO) => void
  ): Promise<StompSubscription> => {
    return chatSocket.subscribe("/user/queue/notifications", onNotification);
  },

  subscribeToUnreadCount: async (
    onUnread: (payload: { unreadCount: number }) => void
  ): Promise<StompSubscription> => {
    return chatSocket.subscribe("/user/queue/notifications.unread", onUnread);
  },
};
