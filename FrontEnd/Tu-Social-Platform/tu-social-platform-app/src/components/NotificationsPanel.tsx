import React, { useEffect, useState } from "react";
import "../styles/NotificationsPanel.css";
import { notificationService, getNotificationRoute, type NotificationDTO } from "../services/NotificationService";
import { useNavigate } from "react-router-dom";

interface NotificationsPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

export const NotificationsPanel: React.FC<NotificationsPanelProps> = ({ isOpen, onClose }) => {
  const [items, setItems] = useState<NotificationDTO[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isOpen) return;

    (async () => {
      const data = await notificationService.getMyNotifications(0, 20);
      setItems(data);
    })();
  }, [isOpen]);

  
  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  const handleClick = async (n: NotificationDTO) => {
    
    if (!n.read) {
      await notificationService.markAsRead(n.id);
      setItems((prev) =>
        prev.map((x) => (x.id === n.id ? { ...x, read: true } : x))
      );
    }

    navigate(getNotificationRoute(n));
    onClose();
  };

  const titleFor = (n: NotificationDTO) =>
    n.type === "POST_COMMENTED" ? "New comment" :
    n.type === "EVENT_JOINED" ? "New participant" : "Notification";

  const uiTypeClass = (n: NotificationDTO) =>
    n.targetType === "POST" ? "comment" :
    n.targetType === "EVENT" ? "event" : "system";

  return (
    <>
      <div
        className={`notifications-backdrop ${isOpen ? "open" : ""}`}
        onClick={onClose}
      />

      <aside className={`notifications-panel ${isOpen ? "open" : ""}`}>
        <header className="notifications-panel__header">
          <h2>Notifications</h2>
          <button
            className="notifications-panel__close-btn"
            onClick={onClose}
            aria-label="Close notifications"
          >
            ✕
          </button>
        </header>

        <ul className="notifications-panel__list">
          {items.map((n) => (
            <li
              key={n.id}
              className={`notifications-panel__item ${!n.read ? "unread" : ""}`}
            >
              <button
                className="notifications-panel__item-btn"
                onClick={() => handleClick(n)}
              >
                <div className="notifications-panel__icon-wrapper">
                  <span className={`notifications-panel__icon ${uiTypeClass(n)}`}>
                    •
                  </span>
                </div>

                <div className="notifications-panel__content">
                  <div className="notifications-panel__top-row">
                    <span className="notifications-panel__title">
                      {titleFor(n)}
                    </span>
                    <span className="notifications-panel__time">
                      {new Date(n.createdAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>

                  <div className="notifications-panel__bottom-row">
                    <span className="notifications-panel__description">
                      {n.actorName ? `${n.actorName} ${n.message ?? ""}` : (n.message ?? "")}
                    </span>
                    {!n.read && <span className="notifications-panel__dot" />}
                  </div>
                </div>
              </button>
            </li>
          ))}
        </ul>
      </aside>
    </>
  );
};
