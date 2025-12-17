import React, { useEffect, useMemo, useState } from "react";
import type { Conversation } from "../types";
import "../styles/Messenger.css";
import { chatService, type ChatDTO } from "../services/ChatService";
import { ChatWindow } from "./ChatWindow";
import { useAuth } from "../services/AuthContext";


type ConversationEx = Conversation & {
  chatIdNum: number;
  isGroup: boolean;
  members: ChatDTO["members"];
};

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

const mapChatToConversation = (c: ChatDTO): ConversationEx => {
  const isGroup = c.groupChat;

  const title = isGroup ? (c.name ?? "Group") : (c.lastMessage?.senderName ?? "Direct chat");
  const avatarUrl = isGroup ? "" : (c.lastMessage?.senderImageUrl ?? "");

  return {
    id: String(c.chatId),
    chatIdNum: Number(c.chatId),
    isGroup,
    members: c.members ?? [],

    name: title,
    lastMessage: c.lastMessage?.content ?? "",
    lastMessageAt: c.lastMessage?.sentAt ?? new Date(0).toISOString(),
    unreadCount: (c as any).unreadCount ?? 0,
    avatarUrl,
  };
};

export const MessagesPanel: React.FC<MessagesPanelProps> = ({ isOpen, onClose }) => {
  const { user } = useAuth();
  const currentUserId = Number((user as any)?.userId ?? (user as any)?.id);

  const [loading, setLoading] = useState(false);
  const [chats, setChats] = useState<ConversationEx[]>([]);
  const [query, setQuery] = useState("");

  const [selected, setSelected] = useState<null | {
    chatId: number;
    title: string;
    isGroup: boolean;
    isAdmin: boolean;
  }>(null);

  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

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
      <div className={`messages-backdrop ${isOpen ? "open" : ""}`} onClick={onClose} />

      <aside className={`messages-panel ${isOpen ? "open" : ""}`}>
        <header className="messages-panel__header">
          <h2>Chats</h2>
          <button className="messages-panel__close-btn" onClick={onClose} aria-label="Close messages">
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
            {filtered.map((conv) => {
              const isAdmin =
                conv.isGroup &&
                conv.members?.some((m) => m.userId === currentUserId && m.chatRole === "Admin");

              return (
                <li key={conv.id} className="messages-panel__item">
                  

                  <button
  className="messages-panel__item-btn"
  onClick={async () => {
    setSelected({
      chatId: conv.chatIdNum,
      title: conv.name,
      isGroup: conv.isGroup,
      isAdmin,
    });

    
    if (conv.unreadCount > 0) {
      
      setChats(prev =>
        prev.map(c =>
          c.chatIdNum === conv.chatIdNum
            ? { ...c, unreadCount: 0 }
            : c
        )
      );

      try {
        await chatService.markAsRead(conv.chatIdNum);
      } catch (e) {
        console.error("Failed to mark chat as read", e);
      }
    }
  }}
>

                    <div className="messages-panel__avatar">
                      {conv.avatarUrl ? <img src={conv.avatarUrl} alt={conv.name} /> : <span>{toInitials(conv.name)}</span>}
                    </div>

                    <div className="messages-panel__content">
                      <div className="messages-panel__top-row">
                        <span className="messages-panel__name">{conv.name}</span>
                        <span className="messages-panel__time">
                          {conv.lastMessageAt && conv.lastMessageAt !== new Date(0).toISOString()
                            ? new Date(conv.lastMessageAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
                            : ""}
                        </span>
                      </div>

                      <div className="messages-panel__bottom-row">
                        <span className="messages-panel__last-message">{conv.lastMessage || " "}</span>
                        {conv.unreadCount > 0 && <span className="messages-panel__badge">{conv.unreadCount}</span>}
                      </div>
                    </div>
                  </button>
                </li>
              );
            })}
          </ul>
        )}
      </aside>

      <ChatWindow
        isOpen={!!selected}
        chatId={selected?.chatId ?? null}
        chatTitle={selected?.title ?? "Chat"}
        isGroup={!!selected?.isGroup}
        isAdmin={!!selected?.isAdmin}
        onClose={() => setSelected(null)}
      />
    </>
  );
};

