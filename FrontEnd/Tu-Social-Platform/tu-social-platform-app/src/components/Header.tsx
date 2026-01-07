import React, { useEffect, useMemo, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu } from "lucide-react";
import LogoutIcon from "@mui/icons-material/Logout";
import NotificationsNoneIcon from "@mui/icons-material/NotificationsNone";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import tuLogo from "../assets/tu-logo.png";
import "../styles/Header.css";
import { MessagesPanel } from "../components/MessengerPanel.tsx";
import { NotificationsPanel } from "./NotificationsPanel";
import { notificationService } from "../services/NotificationService";
import { chatService } from "../services/ChatService";
import { logoutUser } from "../services/AuthService";
import { useAuth } from "../services/AuthContext.tsx";
import { chatSocket } from "../services/ChatSocket";
import type { StompSubscription } from "@stomp/stompjs";
import type { MessageDTO } from "../services/MessageService";

interface HeaderProps {
  showButtons?: boolean;
}

const Header: React.FC<HeaderProps> = ({ showButtons = true }) => {
  const navigate = useNavigate();

  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(false);
  const [isMessagesOpen, setIsMessagesOpen] = useState<boolean>(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState<boolean>(false);

  const [unreadNotifCount, setUnreadNotifCount] = useState<number>(0);
  const [unreadMsgCount, setUnreadMsgCount] = useState<number>(0);

  const toggleSidebar = () => setIsSidebarOpen((prev) => !prev);

  const auth = useAuth();
  const isAdmin = auth.user?.role === "Admin";

  const currentUserId: number | null = useMemo(() => {
    const id = (auth.user as any)?.userId ?? (auth.user as any)?.id ?? null;
    return id == null ? null : Number(id);
  }, [auth.user]);

  
  const notifUnreadSubRef = useRef<StompSubscription | null>(null);
  const msgSubsRef = useRef<Map<number, StompSubscription>>(new Map());

  const handleLogout = () => {
    logoutUser();
    setIsSidebarOpen(false);
    navigate("/login", { replace: true });
  };

  const loadUnreadNotifCount = async () => {
    try {
      const count = await notificationService.getUnreadCount();
      setUnreadNotifCount(count);
    } catch (e) {
      console.error("Failed to load unread notifications count", e);
    }
  };

  const loadUnreadMsgCount = async () => {
    try {
      const chats = await chatService.getMyChats();
      const total = chats.reduce((sum, c: any) => sum + Number((c as any).unreadCount ?? 0), 0);
      setUnreadMsgCount(total);
    } catch (e) {
      console.error("Failed to load unread messages count", e);
    }
  };

  const toggleMessages = async () => {
    const next = !isMessagesOpen;
    setIsMessagesOpen(next);
    setIsNotificationsOpen(false);

    if (next) await loadUnreadMsgCount();
  };

  const toggleNotifications = async () => {
    const next = !isNotificationsOpen;
    setIsNotificationsOpen(next);
    setIsMessagesOpen(false);

    if (next) await loadUnreadNotifCount();
  };

  
  useEffect(() => {
    if (!showButtons) return;
    loadUnreadNotifCount();
    loadUnreadMsgCount();
  }, [showButtons]);

  
  useEffect(() => {
    if (!showButtons) return;
    if (!auth.token) return; 
    

    let cancelled = false;

    (async () => {
      try {
        notifUnreadSubRef.current?.unsubscribe();
        notifUnreadSubRef.current = null;

        notifUnreadSubRef.current = await chatSocket.subscribe(
          "/user/queue/notifications.unread",
          (payload: { unreadCount: number }) => {
            if (cancelled) return;
            if (typeof payload?.unreadCount === "number") {
              setUnreadNotifCount(payload.unreadCount);
            }
          }
        );
      } catch (e) {
        console.error("Failed to subscribe to notifications unread count", e);
      }
    })();

    return () => {
      cancelled = true;
      notifUnreadSubRef.current?.unsubscribe();
      notifUnreadSubRef.current = null;
    };
  }, [showButtons]);

 
  useEffect(() => {
  if (!showButtons) return;
  if (!auth.token) return; 
  if (currentUserId == null) return;

  const token = auth.token; 

  msgSubsRef.current.forEach((s) => s.unsubscribe());
  msgSubsRef.current.clear();

  let cancelled = false;

  (async () => {
    try {
      await chatSocket.connect(token); 

      const chats = await chatService.getMyChats();
       const chatIds = chats
        .map((c: any) => Number(c.chatId))
        .filter((id) => Number.isFinite(id) && id > 0);

      for (const id of chatIds) {
        if (msgSubsRef.current.has(id)) continue;

        const sub = await chatSocket.subscribe(`/topic/chats/${id}`, (msg: MessageDTO) => {
          if (cancelled) return;

          
          if (msg?.userId != null && Number(msg.userId) === Number(currentUserId)) return;

          setUnreadMsgCount((prev) => prev + 1);
        });

        msgSubsRef.current.set(id, sub);
      }
    } catch (e) {
      console.error("Failed to subscribe to chat topics for unread badge", e);
    }
  })();

  return () => {
    cancelled = true;
    msgSubsRef.current.forEach((s) => s.unsubscribe());
    msgSubsRef.current.clear();
  };
}, [showButtons, auth.token, currentUserId]);



  return (
    <>
      <header className="header">
        {showButtons && (
          <div className="header-left">
            <button className="menu-button" onClick={toggleSidebar}>
              <div className="menu-icon">
                <Menu size={22} />
              </div>
            </button>
          </div>
        )}

        <div className="header-center">
          <Link to="/home" className="header-logo-link">
            <img src={tuLogo} alt="TU Social" className="header-logo" />
          </Link>
        </div>

        {showButtons && (
          <div className="header-right">
            <button
              className="header-icon-button messages-btn"
              onClick={toggleMessages}
              aria-label="Open messages"
            >
              <MailOutlineIcon style={{ fontSize: 26 }} />
              {unreadMsgCount > 0 && (
                <span className="notif-badge">{unreadMsgCount > 99 ? "99+" : unreadMsgCount}</span>
              )}
            </button>

            <button
              className="header-icon-button notifications-btn"
              onClick={toggleNotifications}
              aria-label="Open notifications"
            >
              <NotificationsNoneIcon style={{ fontSize: 26 }} />
              {unreadNotifCount > 0 && (
                <span className="notif-badge">
                  {unreadNotifCount > 99 ? "99+" : unreadNotifCount}
                </span>
              )}
            </button>
          </div>
        )}
      </header>

      <nav className={`sidebar ${isSidebarOpen ? "open" : ""}`}>
        <div className="sidebar-main">
          <div className="sidebar-header">
            <span>Menu</span>
            <button className="close-button" onClick={toggleSidebar}>
              ✕
            </button>
          </div>

          <ul className="menu-list">
            <li><Link to="/home" onClick={toggleSidebar}>Home</Link></li>
            <li><Link to="/dorms" onClick={toggleSidebar}>Dorms</Link></li>
            <li><Link to="/events" onClick={toggleSidebar}>Events</Link></li>
            <li><Link to="/information" onClick={toggleSidebar}>Information</Link></li>
            <li><Link to="/profile/me" onClick={toggleSidebar}>My Profile</Link></li>
            <li><Link to="/listings" onClick={toggleSidebar}>Find roomate</Link></li>
            <li><Link to="/posts" onClick={toggleSidebar}>Posts</Link></li>
            <li>
              <Link to="/home" onClick={toggleSidebar}>
                Home
              </Link>
            </li>
            <li>
              <Link to="/dorms" onClick={toggleSidebar}>
                Dorms
              </Link>
            </li>
            <li>
              <Link to="/events" onClick={toggleSidebar}>
                Events
              </Link>
            </li>
            <li>
              <Link to="/information" onClick={toggleSidebar}>
                Information
              </Link>
            </li>
            <li>
              <Link to="/profile/me" onClick={toggleSidebar}>
                My Profile
              </Link>
            </li>
            <li>
              <Link to="/listings" onClick={toggleSidebar}>
                Find a Roomie
              </Link>
            </li>
            <li>
              <Link to="/posts" onClick={toggleSidebar}>
                Posts
              </Link>
            </li>
            {isAdmin && (
              <>
                <li>
                  <Link to="/dorms/add" onClick={toggleSidebar}>
                    Add Dorm
                  </Link>
                </li>
                <li>
                  <Link to="/reports" onClick={toggleSidebar}>
                    Reports
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>

        <div className="logout-button-container">
          <button onClick={handleLogout}>
            <LogoutIcon style={{ marginRight: "6px" }} />
            Logout
          </button>
        </div>
      </nav>

      <MessagesPanel
        isOpen={isMessagesOpen}
        onClose={async () => {
          setIsMessagesOpen(false);
          await loadUnreadMsgCount(); 
        }}
      />

      <NotificationsPanel
        isOpen={isNotificationsOpen}
        onClose={async () => {
          setIsNotificationsOpen(false);
          await loadUnreadNotifCount(); 
        }}
      />
    </>
  );
};

export default Header;
