import React, { useEffect, useRef, useState } from "react";
import "../styles/NotificationsPanel.css";
import {
  notificationService,
  getNotificationRoute,
  type NotificationDTO,
} from "../services/NotificationService";
import { useNavigate } from "react-router-dom";
import type { StompSubscription } from "@stomp/stompjs";
import { chatSocket } from "../services/ChatSocket"; 

interface NotificationsPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

export const NotificationsPanel: React.FC<NotificationsPanelProps> = ({
  isOpen,
  onClose,
}) => {
  const [items, setItems] = useState<NotificationDTO[]>([]);
  const navigate = useNavigate();

  const subRef = useRef<StompSubscription | null>(null);

  
  useEffect(() => {
    if (!isOpen) return;

    (async () => {
      try {
        const data = await notificationService.getMyNotifications(0, 20);
        setItems(data);
      } catch (e) {
        console.error("Failed to load notifications", e);
        setItems([]);
      }
    })();
  }, [isOpen]);

  
  useEffect(() => {
    if (!isOpen) return;

    let cancelled = false;

    (async () => {
      try {
        
        subRef.current?.unsubscribe();
        subRef.current = null;

        
        subRef.current = await chatSocket.subscribe("/user/queue/notifications", (dto) => {
          if (cancelled) return;

          const n = dto as NotificationDTO;

          setItems((prev) => {
            
            if (prev.some((x) => x.id === n.id)) return prev;
           
            return [n, ...prev].slice(0, 50);
          });
        });
      } catch (e) {
        
        console.error("WS subscribe to notifications failed", e);
      }
    })();

    return () => {
      cancelled = true;
      subRef.current?.unsubscribe();
      subRef.current = null;
    };
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
      try {
        await notificationService.markAsRead(n.id);
        setItems((prev) =>
          prev.map((x) => (x.id === n.id ? { ...x, read: true } : x))
        );
      } catch (e) {
        console.error("Failed to mark notification as read", e);
      }
    }

    navigate(getNotificationRoute(n));
    onClose();
  };

  const formatNotificationTime = (createdAt: string | number | Date) => {
  const d = new Date(createdAt);
  const now = new Date();

  if (Number.isNaN(d.getTime())) return "";

  const diffMs = now.getTime() - d.getTime();
  const diffSec = Math.floor(diffMs / 1000);

  if (diffSec < 0) {
    return d.toLocaleString([], {
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  if (diffSec < 60) return "Just now";

  const diffMin = Math.floor(diffSec / 60);
  if (diffMin < 60) return `${diffMin}m ago`;

  const diffHr = Math.floor(diffMin / 60);
  if (diffHr < 24) return `${diffHr}h ago`;

  const startOfToday = new Date(
    now.getFullYear(),
    now.getMonth(),
    now.getDate()
  );
  const startOfThatDay = new Date(
    d.getFullYear(),
    d.getMonth(),
    d.getDate()
  );

  const dayDiff = Math.round(
    (startOfToday.getTime() - startOfThatDay.getTime()) /
      (24 * 60 * 60 * 1000)
  );

  const time = d.toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  });

  if (dayDiff === 1) return `Yesterday ${time}`;
  if (dayDiff < 7) return `${dayDiff}d ago`;

  return d.toLocaleString([], {
    month: "short",
    day: "2-digit",
    year: now.getFullYear() === d.getFullYear() ? undefined : "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};


  const titleFor = (n: NotificationDTO) =>
    n.type === "POST_COMMENTED"
      ? "New comment"
      : n.type === "EVENT_JOINED"
      ? "New participant"
      : "Notification";

  const uiTypeClass = (n: NotificationDTO) =>
    n.targetType === "POST"
      ? "comment"
      : n.targetType === "EVENT"
      ? "event"
      : "system";

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
                    <span className="notifications-panel__title">{titleFor(n)}</span>
                    <span className="notifications-panel__time">
  {formatNotificationTime(n.createdAt)}
</span>

                  </div>

                  <div className="notifications-panel__bottom-row">
                    <span className="notifications-panel__description">
                      {n.actorName
                        ? `${n.actorName} ${n.message ?? ""}`
                        : n.message ?? ""}
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
