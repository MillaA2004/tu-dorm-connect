import React, { useEffect, useMemo, useRef, useState } from "react";
import "../styles/ChatWindow.css";
import { messageService, type MessageDTO, type PageResponse } from "../services/MessageService";
import { useAuth } from "../services/AuthContext";
import { chatService, type ChatMemberDTO } from "../services/ChatService";

type ChatWindowProps = {
  isOpen: boolean;
  chatId: number | null;

  chatTitle: string;
  isGroup: boolean;
  isAdmin: boolean;

  onClose: () => void;
};

const toInitials = (name: string) =>
  name
    .split(" ")
    .filter(Boolean)
    .map((p) => p[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

export const ChatWindow: React.FC<ChatWindowProps> = ({
  isOpen,
  chatId,
  chatTitle,
  isGroup,
  isAdmin,
  onClose,
}) => {
  const { user } = useAuth();

  const currentUserId: number | null = useMemo(() => {
    const id = (user as any)?.userId ?? (user as any)?.id ?? null;
    return id == null ? null : Number(id);
  }, [user]);

  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);

  const [messages, setMessages] = useState<MessageDTO[]>([]);
  const [text, setText] = useState("");

  
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(true);

  
  const [settingsOpen, setSettingsOpen] = useState(false);

  
  const [membersLoading, setMembersLoading] = useState(false);
  const [members, setMembers] = useState<ChatMemberDTO[]>([]);
  const [memberErr, setMemberErr] = useState<string | null>(null);
  const [removingId, setRemovingId] = useState<number | null>(null);

  
  const [leaving, setLeaving] = useState(false);
  const [leaveErr, setLeaveErr] = useState<string | null>(null);

  const listRef = useRef<HTMLDivElement | null>(null);

  
  useEffect(() => {
    if (!isOpen) return;
    const handle = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        if (settingsOpen) setSettingsOpen(false);
        else onClose();
      }
    };
    window.addEventListener("keydown", handle);
    return () => window.removeEventListener("keydown", handle);
  }, [isOpen, onClose, settingsOpen]);

  const normalize = (arr: MessageDTO[]) =>
    [...arr].sort((a, b) => +new Date(a.sentAt) - +new Date(b.sentAt));

  const scrollToBottom = () => {
    const el = listRef.current;
    if (!el) return;
    el.scrollTop = el.scrollHeight;
  };

  const loadPage = async (targetPage: number, mode: "replace" | "prepend") => {
    if (!chatId) return;

    const res: PageResponse<MessageDTO> = await messageService.getMessages(chatId, targetPage, 50);

    setFirst(res.first);
    setPage(res.number);

    setMessages((prev) => {
      const next = mode === "replace" ? res.content : [...res.content, ...prev];
      const map = new Map<number, MessageDTO>();
      normalize(next).forEach((m) => map.set(m.messageId, m));
      return Array.from(map.values());
    });
  };

  
  useEffect(() => {
    if (!isOpen || !chatId) return;

    (async () => {
      try {
        setLoading(true);

        
        setSettingsOpen(false);
        setMembers([]);
        setMemberErr(null);
        setMembersLoading(false);
        setRemovingId(null);

        setLeaving(false);
        setLeaveErr(null);

        setMessages([]);
        setPage(0);
        setFirst(true);

        await loadPage(0, "replace");
        setTimeout(scrollToBottom, 0);
      } catch (e) {
        console.error("Failed to load messages", e);
        setMessages([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, chatId]);

  const loadEarlier = async () => {
    if (loading || first || !chatId) return;

    try {
      setLoading(true);
      const el = listRef.current;
      const prevHeight = el?.scrollHeight ?? 0;

      await loadPage(page + 1, "prepend");

      requestAnimationFrame(() => {
        if (!el) return;
        const newHeight = el.scrollHeight;
        el.scrollTop = newHeight - prevHeight;
      });
    } catch (e) {
      console.error("Failed to load earlier messages", e);
    } finally {
      setLoading(false);
    }
  };

  const send = async () => {
    const content = text.trim();
    if (!content || !chatId) return;

    try {
      setSending(true);
      setText("");

      const created = await messageService.sendMessage(chatId, content);
      setMessages((prev) => normalize([...prev, created]));
      requestAnimationFrame(scrollToBottom);
    } catch (e) {
      console.error("Failed to send message", e);
    } finally {
      setSending(false);
    }
  };

  const openSettings = async () => {
    if (!chatId) return;

    setSettingsOpen(true);
    setMemberErr(null);
    setLeaveErr(null);

    
    if (isAdmin) {
      try {
        setMembersLoading(true);
        const data = await chatService.getChatMembers(chatId);
        setMembers(data);
      } catch (e) {
        console.error("Failed to load members", e);
        setMemberErr("Failed to load members.");
        setMembers([]);
      } finally {
        setMembersLoading(false);
      }
    }
    
  };

  const removeMember = async (memberUserId: number) => {
    if (!chatId) return;
    if (!isAdmin) return;

    try {
      setRemovingId(memberUserId);
      await chatService.removeMember(chatId, memberUserId);
      setMembers((prev) => prev.filter((m) => m.userId !== memberUserId));
    } catch (e) {
      console.error("Failed to remove member", e);
      setMemberErr("Failed to remove member (check permissions).");
    } finally {
      setRemovingId(null);
    }
  };

  const canRemove = (m: ChatMemberDTO) => {
    if (!isAdmin) return false;
    if (currentUserId == null) return false;
    if (m.userId === currentUserId) return false; 
    if (String(m.chatRole).toLowerCase() === "admin") return false; 
    return true;
  };

  const leaveGroup = async () => {
    if (!chatId) return;
    if (!isGroup) return;
    if (isAdmin) return; 
    if (currentUserId == null) return;

    try {
      setLeaving(true);
      setLeaveErr(null);

      await chatService.removeMember(chatId, currentUserId);

      
      setSettingsOpen(false);
      onClose();
    } catch (e) {
      console.error("Failed to leave group", e);
      setLeaveErr("Failed to leave group.");
    } finally {
      setLeaving(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="chatwin-backdrop" onClick={onClose} />

      <section className="chatwin" role="dialog" aria-label="Chat window" onClick={(e) => e.stopPropagation()}>
        <header className="chatwin__header">
          <div className="chatwin__title">
            <div className="chatwin__avatar">
              <span>{toInitials(chatTitle)}</span>
            </div>
            <div className="chatwin__titleText">
              <div className="chatwin__name">{chatTitle}</div>
              <div className="chatwin__meta">{isGroup ? "Group chat" : "Direct message"}</div>
            </div>
          </div>

          <div className="chatwin__actions">
            
            {isGroup && (
              <button className="chatwin__iconBtn" onClick={openSettings} aria-label="Chat settings" title="Chat settings">
                ⚙
              </button>
            )}
            <button className="chatwin__iconBtn" onClick={onClose} aria-label="Close" title="Close">
              ✕
            </button>
          </div>
        </header>

        <div className="chatwin__body">
          <div className="chatwin__messages" ref={listRef}>
            {!first && (
              <div className="chatwin__loadEarlierWrap">
                <button className="chatwin__loadEarlierBtn" onClick={loadEarlier} disabled={loading}>
                  {loading ? "Loading…" : "Load earlier"}
                </button>
              </div>
            )}

            {messages.length === 0 && !loading ? (
              <div className="chatwin__placeholder">No messages yet.</div>
            ) : (
              messages.map((m) => {
                const mine = currentUserId != null && Number(m.userId) === Number(currentUserId);
                return (
                  <div key={m.messageId} className={`chatmsg ${mine ? "mine" : ""}`}>
                    {!mine && (
                      <div className="chatmsg__avatar">
                        {m.senderImageUrl ? (
                          <img src={m.senderImageUrl} alt={m.senderName ?? "User"} />
                        ) : (
                          <span>{toInitials(m.senderName ?? "User")}</span>
                        )}
                      </div>
                    )}

                    <div className="chatmsg__bubble">
                      {!mine && <div className="chatmsg__sender">{m.senderName ?? "User"}</div>}
                      <div className="chatmsg__text">{m.content}</div>
                      <div className="chatmsg__time">
                        {new Date(m.sentAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          <div className="chatwin__composer">
            <input
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Write a message…"
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  send();
                }
              }}
            />
            <button className="chatwin__sendBtn" onClick={send} disabled={!text.trim() || sending}>
              {sending ? "…" : "Send"}
            </button>
          </div>
        </div>

        
        {settingsOpen && (
          <aside className="chatwin__settings" role="dialog" aria-label="Chat settings">
            <div className="chatwin__settingsHeader">
              <div className="chatwin__settingsTitle">{isAdmin ? "Group members" : "Group settings"}</div>
              <button className="chatwin__iconBtn" onClick={() => setSettingsOpen(false)} aria-label="Close settings">
                ✕
              </button>
            </div>

            <div className="chatwin__settingsBody">
              
              {isAdmin ? (
                <>
                  {memberErr && <div className="chatwin__placeholder">{memberErr}</div>}

                  {membersLoading ? (
                    <div className="chatwin__placeholder">Loading members…</div>
                  ) : (
                    <ul className="chatwin__members">
                      {members.map((m) => {
                        const fullName = `${m.firstName ?? ""} ${m.lastName ?? ""}`.trim() || `User ${m.userId}`;
                        const role = String(m.chatRole || "").toLowerCase() === "admin" ? "Admin" : "Member";

                        return (
                          <li key={m.chatMemberId} className="chatwin__member">
                            <div className="chatwin__memberLeft">
                              <div className="chatwin__memberAvatar">
                                <span>{toInitials(fullName)}</span>
                              </div>
                              <div className="chatwin__memberName">
                                {fullName}{" "}
                                <span style={{ opacity: 0.7, fontWeight: 700, marginLeft: 6 }}>· {role}</span>
                              </div>
                            </div>

                            {canRemove(m) && (
                              <button
                                className="chatwin__removeBtn"
                                onClick={() => removeMember(m.userId)}
                                disabled={removingId === m.userId}
                              >
                                {removingId === m.userId ? "Removing…" : "Remove"}
                              </button>
                            )}
                          </li>
                        );
                      })}
                    </ul>
                  )}
                </>
              ) : (
                
                <>
                  {leaveErr && <div className="chatwin__placeholder">{leaveErr}</div>}

                  <div className="chatwin__leaveCard">
                    <div className="chatwin__leaveTitle">Leave this group?</div>
                    <div className="chatwin__leaveText">
                      You’ll stop receiving messages from this group and it will disappear from your chats.
                    </div>

                    <button className="chatwin__leaveBtn" onClick={leaveGroup} disabled={leaving}>
                      {leaving ? "Leaving…" : "Leave group"}
                    </button>
                  </div>
                </>
              )}
            </div>
          </aside>
        )}
      </section>
    </>
  );
};
