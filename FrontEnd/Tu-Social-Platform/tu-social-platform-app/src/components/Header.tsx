// import React, { useState } from 'react';
// import { Link, useNavigate } from 'react-router-dom';
// import { Menu } from 'lucide-react';
// import LogoutIcon from '@mui/icons-material/Logout';
// import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
// import MailOutlineIcon from '@mui/icons-material/MailOutline';
// import tuLogo from '../assets/tu-logo.png'; 
// import '../styles/Header.css';
// import { MessagesPanel } from '../components/MessengerPanel.tsx';
// import { NotificationsPanel } from './NotificationsPanel';


// interface HeaderProps {
//   showButtons?: boolean;
// }

// const Header: React.FC<HeaderProps> = ({ showButtons = true }) => {
//   const navigate = useNavigate();
//   const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(false);
//   const [isMessagesOpen, setIsMessagesOpen] = useState<boolean>(false);
//   const [isNotificationsOpen, setIsNotificationsOpen] = useState<boolean>(false);

//   const toggleSidebar = () => {
//     setIsSidebarOpen(prev => !prev);
//   };

//   const handleLogout = () => {
//     navigate('/login');
//   };

//   const toggleMessages = () => {
//     setIsMessagesOpen(prev => !prev);
    
//     setIsNotificationsOpen(false);
//   };

//   const toggleNotifications = () => {
//     setIsNotificationsOpen(prev => !prev);
    
//     setIsMessagesOpen(false);
//   };

//   return (
//     <>
//       <header className="header">
//         {showButtons && (
//           <div className="header-left">
//             <button className="menu-button" onClick={toggleSidebar}>
//               <div className="menu-icon">
//                 <Menu size={22} />
//               </div>
//             </button>
//           </div>
//         )}

        

//         <div className="header-center">
          
//           <Link to="/home" className="header-logo-link">
//             <img
//               src={tuLogo}
//               alt="TU Social"
//               className="header-logo"
//             />
//           </Link>
//         </div>

//         {showButtons && (
//           <div className="header-right">
//             <button
//               className="header-icon-button"
//               onClick={toggleMessages}
//               aria-label="Open messages"
//             >
//               <MailOutlineIcon style={{ fontSize: 26 }} />
//             </button>

//             <button
//               className="header-icon-button"
//               onClick={toggleNotifications}
//               aria-label="Open notifications"
//             >
//               <NotificationsNoneIcon style={{ fontSize: 26 }} />
//             </button>
//           </div>
//         )}
//       </header>

//       <nav className={`sidebar ${isSidebarOpen ? 'open' : ''}`}>
//         <div className="sidebar-main">
//           <div className="sidebar-header">
//             <span>Menu</span>
//             <button className="close-button" onClick={toggleSidebar}>✕</button>
//           </div>

//           <ul className="menu-list">
//             <li><Link to="/home" onClick={toggleSidebar}>Home</Link></li>
//             <li><Link to="/doorsm" onClick={toggleSidebar}>Doorsm</Link></li>
//             <li><Link to="/events" onClick={toggleSidebar}>Events</Link></li>
//             <li><Link to="/information" onClick={toggleSidebar}>Information</Link></li>
//             <li><Link to="/profile/me" onClick={toggleSidebar}>My Profile</Link></li>
//             <li><Link to="/listings" onClick={toggleSidebar}>Find roomate</Link></li>
//             <li><Link to="/posts" onClick={toggleSidebar}>Posts</Link></li>
//           </ul>
//         </div>

//         <div className="logout-button-container">
//           <button onClick={handleLogout}>
//             <LogoutIcon style={{ marginRight: '6px' }} />
//             Logout
//           </button>
//         </div>
//       </nav>

      
//       <MessagesPanel
//         isOpen={isMessagesOpen}
//         onClose={() => setIsMessagesOpen(false)}
//       />

//       <NotificationsPanel
//         isOpen={isNotificationsOpen}
//         onClose={() => setIsNotificationsOpen(false)}
//       />
//     </>
//   );
// };

// export default Header;



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

interface HeaderProps {
  showButtons?: boolean;
}

const Header: React.FC<HeaderProps> = ({ showButtons = true }) => {
  const navigate = useNavigate();

  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(false);
  const [isMessagesOpen, setIsMessagesOpen] = useState<boolean>(false);
  const [isNotificationsOpen, setIsNotificationsOpen] =
    useState<boolean>(false);

  const [unreadCount, setUnreadCount] = useState<number>(0);

  const toggleSidebar = () => {
    setIsSidebarOpen((prev) => !prev);
  };

  const handleLogout = () => {
    navigate("/login");
  };

  const toggleMessages = () => {
    setIsMessagesOpen((prev) => !prev);
    setIsNotificationsOpen(false);
  };

  const loadUnreadCount = async () => {
    try {
      const count = await notificationService.getUnreadCount();
      setUnreadCount(count);
    } catch (e) {
      console.error("Failed to load unread notifications count", e);
    }
  };

  const toggleNotifications = async () => {
    const next = !isNotificationsOpen;
    setIsNotificationsOpen(next);

    setIsMessagesOpen(false);

    // optional: refresh badge when opening panel
    if (next) {
      await loadUnreadCount();
    }
  };

  // Fetch unread count once when header mounts
  useEffect(() => {
    if (!showButtons) return;
    loadUnreadCount();
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
              className="header-icon-button"
              onClick={toggleMessages}
              aria-label="Open messages"
            >
              <MailOutlineIcon style={{ fontSize: 26 }} />
            </button>

            <button
              className="header-icon-button notifications-btn"
              onClick={toggleNotifications}
              aria-label="Open notifications"
            >
              <NotificationsNoneIcon style={{ fontSize: 26 }} />
              {unreadCount > 0 && (
                <span className="notif-badge">
                  {unreadCount > 99 ? "99+" : unreadCount}
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
              <Link to="/doorsm" onClick={toggleSidebar}>
                Doorsm
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
        onClose={() => setIsMessagesOpen(false)}
      />

      <NotificationsPanel
        isOpen={isNotificationsOpen}
        onClose={async () => {
          setIsNotificationsOpen(false);
          await loadUnreadCount(); // refresh badge after closing
        }}
      />
    </>
  );
};

export default Header;
