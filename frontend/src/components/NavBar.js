import { NavLink } from "react-router-dom";
import { useState } from "react";
import {useAuth} from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

// import NavBarCSS from './NavBar.module.css';

export default function NavBar() {
  const [isNavExpanded, setIsNavExpanded] = useState(false);
  const { logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleToggle = () => {
    setIsNavExpanded(!isNavExpanded);
  };

  const logoutUser = () => {
    logout();
    navigate('/');
  };

  return (
    <header>
      <button
        className="hamburger"
        aria-expanded={isNavExpanded}
        aria-controls="main-nav"
        onClick={handleToggle}
      >
        <div className="bar"></div>
      </button>
      
      <a href="#" className="logo">
        <img src="/images/logoFanfare.png" alt="Fanfare Logo" />
      </a>
      
      <nav id="main-nav" aria-expanded={isNavExpanded}>
        <NavLink 
          exact 
          to="/" 
          activeClassName="active" 
          onClick={handleToggle}
        >
          Home
        </NavLink>
        
        <div className="dropdown" aria-expanded="false">
          <button className="btn2 dropbtn">Fanfare</button>
          <div className="dropdown-content">
            <NavLink 
              to="/fanfare/instrumenten" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Instrumenten
            </NavLink>
            <NavLink 
              to="/fanfare/geschiedenis" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Geschiedenis
            </NavLink>
            <NavLink 
              to="/fanfare/bestuur" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Bestuur
            </NavLink>
            <NavLink 
              to="/fanfare/dirigent" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Dirigent
            </NavLink>
          </div>
        </div>
        
        <NavLink 
          to="/jeugd" 
          activeClassName="active" 
          onClick={handleToggle}
        >
          Jeugd
        </NavLink>
        
        <div className="dropdown">
          <button className="dropbtn">Info</button>
          <div className="dropdown-content">
            <NavLink 
              to="/info/documenten" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Documenten
            </NavLink>
            <NavLink 
              to="/info/privacy" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Privacy
            </NavLink>
          </div>
        </div>
        
        <NavLink 
          to="/sponsors" 
          activeClassName="active" 
          onClick={handleToggle}
        >
          Sponsors
        </NavLink>
        
        <NavLink 
          to="/kalender" 
          activeClassName="active" 
          onClick={handleToggle}
        >
          Kalender
        </NavLink>
        <NavLink 
          to="/products" 
          activeClassName="active" 
          onClick={handleToggle}
        >
          Shop
        </NavLink>
        {isAuthenticated ? (
          <NavLink to="/" onClick={logoutUser}>
          Logout
        </NavLink>
        ) : (
          <NavLink to="/login" onClick={handleToggle}>
            Login
          </NavLink>
        )}

        {isAuthenticated && (
          <NavLink to="/cart">
            Cart
          </NavLink>
        )}
        
        {/* Authenticated Links */}
        { /* Mocking an authenticated state; replace with actual auth logic */ }
        {/* {true ? (
          <>
            <NavLink 
              to="/logout" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Logout
            </NavLink>
            <NavLink 
              to="/dashboard" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              <img className="avatar" src="/path/to/user/avatar.png" alt="User Avatar" />
            </NavLink>
          </>
        ) : (
          <>
            <NavLink 
              to="/register" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Register
            </NavLink>
            <NavLink 
              to="/login" 
              activeClassName="active" 
              onClick={handleToggle}
            >
              Login
            </NavLink>
          </>
        )} */}
      </nav>
    </header>
  );
}
