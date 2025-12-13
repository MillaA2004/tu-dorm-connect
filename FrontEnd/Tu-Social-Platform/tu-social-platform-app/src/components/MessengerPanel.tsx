// components/MessagesPanel.tsx
import React, { useEffect } from "react";
import type { Conversation } from "../types";
import "../styles/Messenger.css";

interface MessagesPanelProps {
  isOpen: boolean;
  onClose: () => void;
  // if you ever want to pass real data, this is optional
  conversations?: Conversation[];
}

// mock data lives HERE
const mockConversations: Conversation[] = [
  {
    id: "1",
    name: "John Doe",
    lastMessage: "Hey, how are you?",
    lastMessageAt: new Date().toISOString(),
    unreadCount: 2,
    avatarUrl: "",
  },
  {
    id: "2",
    name: "Jane Smith",
    lastMessage: "Let's meet tomorrow.",
    lastMessageAt: new Date().toISOString(),
    unreadCount: 0,
    avatarUrl: "",
  },
  {
    id: "3",
    name: "Study Group",
    lastMessage: "Don't forget the assignment.",
    lastMessageAt: new Date().toISOString(),
    unreadCount: 5,
    avatarUrl: "",
  },
];

export const MessagesPanel: React.FC<MessagesPanelProps> = ({
  isOpen,
  onClose,
  conversations,
}) => {
  const list = conversations ?? mockConversations;

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
        className={`messages-backdrop ${isOpen ? "open" : ""}`}
        onClick={onClose}
      />

      {/* Side panel */}
      <aside className={`messages-panel ${isOpen ? "open" : ""}`}>
        <header className="messages-panel__header">
          <h2>Chats</h2>
          <button
            className="messages-panel__close-btn"
            onClick={onClose}
            aria-label="Close messages"
          >
            ✕
          </button>
        </header>

        <div className="messages-panel__search">
          <input type="text" placeholder="Search Messenger" />
        </div>

        <ul className="messages-panel__list">
          {list.map((conv) => (
            <li key={conv.id} className="messages-panel__item">
              <button className="messages-panel__item-btn">
                <div className="messages-panel__avatar">
                  {conv.avatarUrl ? (
                    <img src={conv.avatarUrl} alt={conv.name} />
                  ) : (
                    <span>
                      {conv.name
                        .split(" ")
                        .map((p) => p[0])
                        .join("")
                        .toUpperCase()}
                    </span>
                  )}
                </div>
                <div className="messages-panel__content">
                  <div className="messages-panel__top-row">
                    <span className="messages-panel__name">{conv.name}</span>
                    <span className="messages-panel__time">
                      {new Date(conv.lastMessageAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>
                  <div className="messages-panel__bottom-row">
                    <span className="messages-panel__last-message">
                      {conv.lastMessage}
                    </span>
                    {conv.unreadCount > 0 && (
                      <span className="messages-panel__badge">
                        {conv.unreadCount}
                      </span>
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
