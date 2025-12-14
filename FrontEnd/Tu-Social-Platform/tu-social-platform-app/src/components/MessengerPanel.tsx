import React, { useEffect, useMemo, useState } from "react";
import type { Conversation } from "../types";
import "../styles/Messenger.css";
import { chatService, type ChatDTO } from "../services/ChatService";

interface MessagesPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

const toInitials = (name: string) =>
  name
    .split(" ")
    .filter(Boolean)
    .map((p) => p[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

const mapChatToConversation = (c: ChatDTO): Conversation => {
  const isGroup = c.groupChat;

  // Title rule:
  // - group => group name
  // - direct => last message sender name
  const title = isGroup
    ? (c.name ?? "Group")
    : (c.lastMessage?.senderName ?? "Direct chat");

  // Avatar rule:
  // - group => no avatar (for now)
  // - direct => sender image url (if exists)
  const avatarUrl = isGroup
    ? ""
    : (c.lastMessage?.senderImageUrl ?? "");

  return {
    id: String(c.chatId),
    name: title,
    lastMessage: c.lastMessage?.content ?? "",
    lastMessageAt: c.lastMessage?.sentAt ?? new Date(0).toISOString(),
    unreadCount: 0,
    avatarUrl,
  };
};

export const MessagesPanel: React.FC<MessagesPanelProps> = ({ isOpen, onClose }) => {
  const [loading, setLoading] = useState(false);
  const [chats, setChats] = useState<Conversation[]>([]);
  const [query, setQuery] = useState("");

  // Close on Escape
  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  // Load chats when panel opens
  useEffect(() => {
    if (!isOpen) return;

    (async () => {
      try {
        setLoading(true);

        const data = await chatService.getMyChats();
        const mapped = data.map(mapChatToConversation);

        mapped.sort((a, b) => +new Date(b.lastMessageAt) - +new Date(a.lastMessageAt));
        setChats(mapped);
      } catch (e) {
        console.error("Failed to load chats", e);
        setChats([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen]);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return chats;
    return chats.filter((c) => c.name.toLowerCase().includes(q));
  }, [query, chats]);

  return (
    <>
      {/* Backdrop */}
      <div className={`messages-backdrop ${isOpen ? "open" : ""}`} onClick={onClose} />

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
          <input
            type="text"
            placeholder="Search Messenger"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        {loading ? (
          <div style={{ padding: 12 }}>Loading…</div>
        ) : (
          <ul className="messages-panel__list">
            {filtered.map((conv) => (
              <li key={conv.id} className="messages-panel__item">
                <button className="messages-panel__item-btn">
                  <div className="messages-panel__avatar">
                    {conv.avatarUrl ? (
                      <img src={conv.avatarUrl} alt={conv.name} />
                    ) : (
                      <span>{toInitials(conv.name)}</span>
                    )}
                  </div>

                  <div className="messages-panel__content">
                    <div className="messages-panel__top-row">
                      <span className="messages-panel__name">{conv.name}</span>
                      <span className="messages-panel__time">
                        {conv.lastMessageAt && conv.lastMessageAt !== new Date(0).toISOString()
                          ? new Date(conv.lastMessageAt).toLocaleTimeString([], {
                              hour: "2-digit",
                              minute: "2-digit",
                            })
                          : ""}
                      </span>
                    </div>

                    <div className="messages-panel__bottom-row">
                      <span className="messages-panel__last-message">
                        {conv.lastMessage || " "}
                      </span>

                      {conv.unreadCount > 0 && (
                        <span className="messages-panel__badge">{conv.unreadCount}</span>
                      )}
                    </div>
                  </div>
                </button>
              </li>
            ))}
          </ul>
        )}
      </aside>
    </>
  );
};
