import React, { useEffect, useState } from "react";
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
                <span className="notif-badge">
                  {unreadMsgCount > 99 ? "99+" : unreadMsgCount}
                </span>
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
                Find roomate
              </Link>
            </li>
            <li>
              <Link to="/posts" onClick={toggleSidebar}>
                Posts
              </Link>
            </li>
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
