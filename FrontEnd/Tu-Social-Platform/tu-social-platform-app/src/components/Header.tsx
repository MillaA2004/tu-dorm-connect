
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu } from 'lucide-react';
import LogoutIcon from '@mui/icons-material/Logout';
import '../styles/Header.css';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import MailOutlineIcon from '@mui/icons-material/MailOutline';


interface HeaderProps {
  showButtons?: boolean;
}

const Header: React.FC<HeaderProps> = ({ showButtons = true }) => {
  const navigate = useNavigate();
  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(false);
  

  const toggleSidebar = () => {
    setIsSidebarOpen((prev) => !prev);
  };

  const handleLogout = () => {
    // TODO: add real logout logic if you have tokens / API, then navigate
    navigate('/login');
  };


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
          <h1 className="title">Tu-Social-App</h1>
        </div>

        {showButtons && (
          <div className="header-right">

             
              <MailOutlineIcon style={{ fontSize: 26 }} />
              
              {/*  need to update the logic for those buttons when the rest componets are done!!!!  */}

            
              <NotificationsNoneIcon style={{ fontSize: 26 }} />
            

          </div>
        )}
      </header>

      <nav className={`sidebar ${isSidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-main">
          <div className="sidebar-header">
            <span>Menu</span>
            <button className="close-button" onClick={toggleSidebar}>✕</button>
          </div>

          <ul className="menu-list">
            <li><Link to="/doorsm" onClick={toggleSidebar}>Doorsm</Link></li>
            <li><Link to="/events" onClick={toggleSidebar}>Events</Link></li>
            <li><Link to="/my-events" onClick={toggleSidebar}>My Events</Link></li>
            <li><Link to="/information" onClick={toggleSidebar}>Information</Link></li>
          </ul>
        </div>

        <div className="logout-button-container">
          <button onClick={handleLogout}>
            <LogoutIcon style={{ marginRight: '6px' }} />
            Logout
          </button>
        </div>
      </nav>
    </>
  );
};

export default Header;
