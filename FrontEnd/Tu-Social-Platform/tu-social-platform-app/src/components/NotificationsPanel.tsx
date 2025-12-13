// components/NotificationsPanel.tsx
import React, { useEffect } from "react";
import type { NotificationItem } from "../types";
import "../styles/NotificationsPanel.css";

interface NotificationsPanelProps {
  isOpen: boolean;
  onClose: () => void;
  notifications?: NotificationItem[];
}

// mock data lives here
const mockNotifications: NotificationItem[] = [
  {
    id: "1",
    title: "New comment",
    description: "Alex commented on your post.",
    createdAt: new Date().toISOString(),
    isRead: false,
    type: "comment",
  },
  {
    id: "2",
    title: "Event reminder",
    description: "Friday Night Party starts in 2 hours.",
    createdAt: new Date().toISOString(),
    isRead: false,
    type: "event",
  },
  {
    id: "3",
    title: "New follower",
    description: "John Doe started following you.",
    createdAt: new Date().toISOString(),
    isRead: true,
    type: "system",
  },
];

export const NotificationsPanel: React.FC<NotificationsPanelProps> = ({
  isOpen,
  onClose,
  notifications,
}) => {
  const list = notifications ?? mockNotifications;

  // Close on Escape
  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  return (
    <>
      {/* Backdrop */}
      <div
        className={`notifications-backdrop ${isOpen ? "open" : ""}`}
        onClick={onClose}
      />

      {/* Side panel */}
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
          {list.map((n) => (
            <li
              key={n.id}
              className={`notifications-panel__item ${
                !n.isRead ? "unread" : ""
              }`}
            >
              <button className="notifications-panel__item-btn">
                <div className="notifications-panel__icon-wrapper">
                  {/* Simple circle icon; you can swap with real icons later */}
                  <span className={`notifications-panel__icon ${n.type ?? ""}`}>
                    •
                  </span>
                </div>
                <div className="notifications-panel__content">
                  <div className="notifications-panel__top-row">
                    <span className="notifications-panel__title">
                      {n.title}
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
                      {n.description}
                    </span>
                    {!n.isRead && (
                      <span className="notifications-panel__dot" />
                    )}
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
