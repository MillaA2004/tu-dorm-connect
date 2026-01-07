import React, { useEffect, useMemo, useRef, useState } from "react";
import type { Conversation } from "../types";
import "../styles/Messenger.css";
import { chatService, type ChatDTO } from "../services/ChatService";
import { ChatWindow } from "./ChatWindow";
import { useAuth } from "../services/AuthContext";
import { chatSocket } from "../services/ChatSocket";
import type { StompSubscription } from "@stomp/stompjs";
import type { MessageDTO } from "../services/MessageService";

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

const formatChatTimestamp = (iso: string) => {
  const sent = new Date(iso);
  const now = new Date();

  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const startOfSentDay = new Date(sent.getFullYear(), sent.getMonth(), sent.getDate());

  const diffDays = Math.floor(
    (startOfToday.getTime() - startOfSentDay.getTime()) / (1000 * 60 * 60 * 24)
  );

  if (diffDays === 0) return sent.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  if (diffDays === 1) return "1 day ago";
  if (diffDays > 1 && diffDays < 7) return `${diffDays} days ago`;

  return sent.toLocaleDateString([], { day: "2-digit", month: "short" });
};

const mapChatToConversation =
  (currentUserId: number) =>
  (c: ChatDTO): ConversationEx => {
    const isGroup = c.groupChat;
    const members = c.members ?? [];

    if (isGroup) {
      return {
        id: String(c.chatId),
        chatIdNum: Number(c.chatId),
        isGroup,
        members,
        name: c.name ?? "Group",
        lastMessage: c.lastMessage?.content ?? "",
        lastMessageAt: c.lastMessage?.sentAt ?? new Date(0).toISOString(),
        unreadCount: (c as any).unreadCount ?? 0,
        avatarUrl: "",
      };
    }

    const other = members.find((m) => Number(m.userId) !== Number(currentUserId));
    const otherName =
      `${other?.firstName ?? ""} ${other?.lastName ?? ""}`.trim() || "Direct chat";
    const otherAvatar = other?.imageUrl ?? "";

    return {
      id: String(c.chatId),
      chatIdNum: Number(c.chatId),
      isGroup,
      members,
      name: otherName,
      lastMessage: c.lastMessage?.content ?? "",
      lastMessageAt: c.lastMessage?.sentAt ?? new Date(0).toISOString(),
      unreadCount: (c as any).unreadCount ?? 0,
      avatarUrl: otherAvatar,
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
    otherUserId: number | null;
    otherAvatarUrl: string;
  }>(null);

  
  const subsRef = useRef<Map<number, StompSubscription>>(new Map());

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

        const mapped = data.map(mapChatToConversation(currentUserId));
        mapped.sort((a, b) => +new Date(b.lastMessageAt) - +new Date(a.lastMessageAt));
        setChats(mapped);
      } catch (e) {
        console.error("Failed to load chats", e);
        setChats([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, currentUserId]);

  
  useEffect(() => {
    if (!isOpen) return;

    
    subsRef.current.forEach((sub) => sub.unsubscribe());
    subsRef.current.clear();

    let cancelled = false;

    (async () => {
      try {
        
        for (const conv of chats) {
          const chatId = conv.chatIdNum;
          if (!chatId || subsRef.current.has(chatId)) continue;

          const sub = await chatSocket.subscribeToChat(chatId, (msg) => {
            if (cancelled) return;

            const m = msg as MessageDTO;

            setChats((prev) => {
              
              const updated = prev.map((c) => {
                if (c.chatIdNum !== chatId) return c;

                const isCurrentlyOpen = selected?.chatId === chatId;
                const nextUnread = isCurrentlyOpen ? 0 : (c.unreadCount ?? 0) + 1;

                return {
                  ...c,
                  lastMessage: m.content ?? c.lastMessage,
                  lastMessageAt: m.sentAt ?? c.lastMessageAt,
                  unreadCount: nextUnread,
                };
              });

              
              updated.sort((a, b) => +new Date(b.lastMessageAt) - +new Date(a.lastMessageAt));
              return updated;
            });
          });

          subsRef.current.set(chatId, sub);
        }
      } catch (e) {
        console.error("Failed to subscribe for chat list updates", e);
      }
    })();

    return () => {
      cancelled = true;
      subsRef.current.forEach((sub) => sub.unsubscribe());
      subsRef.current.clear();
    };
    
  }, [isOpen, chats, selected?.chatId]);

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
                conv.members?.some(
                  (m: any) => m.userId === currentUserId && String(m.chatRole) === "Admin"
                );

              const otherUserId =
                !conv.isGroup
                  ? (conv.members as any[])?.find((m) => Number(m.userId) !== Number(currentUserId))
                      ?.userId ?? null
                  : null;

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
                        otherUserId,
                        otherAvatarUrl: conv.avatarUrl ?? "",
                      });

                      if (conv.unreadCount > 0) {
                        setChats((prev) =>
                          prev.map((c) =>
                            c.chatIdNum === conv.chatIdNum ? { ...c, unreadCount: 0 } : c
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
                            ? formatChatTimestamp(conv.lastMessageAt)
                            : ""}
                        </span>
                      </div>

                      <div className="messages-panel__bottom-row">
                        <span className="messages-panel__last-message">{conv.lastMessage || " "}</span>
                        {conv.unreadCount > 0 && (
                          <span className="messages-panel__badge">{conv.unreadCount}</span>
                        )}
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
        otherUserId={selected?.otherUserId ?? null}
        otherAvatarUrl={selected?.otherAvatarUrl ?? ""}
        onClose={() => setSelected(null)}
      />
    </>
  );
};

