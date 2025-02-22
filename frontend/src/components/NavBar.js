import { NavLink } from "react-router-dom";
import { useState } from "react";
import {useAuth} from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

// import NavBarCSS from './NavBar.module.css';

export default function NavBar() {
  const [isNavExpanded, setIsNavExpanded] = useState(false);


  const handleToggle = () => {
    setIsNavExpanded(!isNavExpanded);
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
      

      <nav id="main-nav" aria-expanded={isNavExpanded}>

      </nav>
    </header>
  );
}
